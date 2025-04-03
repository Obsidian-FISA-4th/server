package Obsidian.demo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import Obsidian.demo.dto.FileNodeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Component
public class FileSystemUtil {
	private static final String homeDir = System.getProperty("user.home") + "/obsidian";
	private static final String vaultPath = homeDir + "/note/";
	private static final String publicPath = homeDir + "/public/";

	private final RedisTemplate<String, Object> redisTemplate;
	/**
	 * 이미 배포되어있는 파일인지 확인하는 메서드
	 * @param relativePath
	 * @return htmlPath
	 */
	public static Path findPublishedHtmlFile(String relativePath) {
		String htmlFilePath = publicPath + relativePath.replaceAll("\\.md$", ".html");
		Path htmlPath = Paths.get(htmlFilePath);
		return Files.exists(htmlPath) ? htmlPath : null;
	}

	/**
	 * DeleteFolderOrFile 서비스 메서드에서 재귀적으로 폴더나 파일을 삭제를 수행하는 함수
	 * @param path
	 * @throws IOException
	 */
	public static void deleteRecursively(Path path) throws IOException {
		if (!Files.exists(path)) {
			return;
		}

		if (Files.isDirectory(path)) {
			Files.walk(path)
				.sorted(Comparator.reverseOrder()) // 내부 파일 먼저 삭제 후 폴더 삭제
				.map(Path::toFile)
				.forEach(File::delete);
		} else {
			Files.deleteIfExists(path);
		}
	}

	/**
	 * 파일 혹은 폴더를 FileTree의 하나의 노드로 변환
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static FileNodeDto buildFileNode(Path path) throws IOException {
		boolean isFolder = Files.isDirectory(path);
		List<FileNodeDto> children = new ArrayList<>();

		if (isFolder) {
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
				for (Path child : directoryStream) {
					children.add(buildFileNode(child));
				}
			}
		}
		return new FileNodeDto(path.getFileName().toString(), isFolder, false, path.toString(), children);
	}

	/**
	 * isPublish 값 할당 함수. 파일트리를 구성할때 해당 파일이 /public에 존재하는지 체크하는 함수
	 * @param files
	 * @param publicHtmlFiles
	 */
	public static void markPublishedFiles(List<FileNodeDto> files, Set<String> publicHtmlFiles) {
		for (FileNodeDto file : files) {
			if (!file.isFolder() && file.getName().endsWith(".md")) {
				String fileNameWithoutExtension = getFileNameWithoutExtension(file.getName());
				if (publicHtmlFiles.contains(fileNameWithoutExtension)) {
					file.setPublish(true);
				}
			}
			if (file.isFolder()) {
				markPublishedFiles(file.getChildren(), publicHtmlFiles);
			}
		}
	}

	/**
	 * 파일 이름에서 확장자를 제거하고 순수한 파일 이름만 반환하는 함수
	 * @param fileName
	 * @return
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf('.');
		return (lastDotIndex != -1) ? fileName.substring(0, lastDotIndex) : fileName;
	}

	@CacheEvict(value = "fileTreeCache", key = "'fileTree'")
	public  List<FileNodeDto> updateFileTree() {
		Path root = Paths.get(vaultPath);
		Path publicRoot = Paths.get(publicPath);
		System.out.println("UpdateCache");
		if (!Files.exists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				log.error("디렉토리 생성 중 오류 발생: {}", e.getMessage());
				return Collections.emptyList();
			}
		}

		List<FileNodeDto> noteFiles = new ArrayList<>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(root)) {
			for (Path path : directoryStream) {
				noteFiles.add(buildFileNode(path));
			}
		} catch (IOException e) {
			log.error("파일 시스템 조회 중 오류 발생: {}", e.getMessage());
			return Collections.emptyList();
		}

		Set<String> publicHtmlFiles = new HashSet<>();

		if (Files.exists(publicRoot)) {
			try (Stream<Path> paths = Files.walk(publicRoot)) {
				paths
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".html"))
					.forEach(path -> {
						System.out.println("path = " + path);
						String fileNameWithoutExtension = getFileNameWithoutExtension(path.getFileName().toString());
						publicHtmlFiles.add(fileNameWithoutExtension);
					});
			} catch (IOException e) {
				log.error("Public 폴더 조회 중 오류 발생: {}", e.getMessage());
			}
		}


		markPublishedFiles(noteFiles, publicHtmlFiles);

		redisTemplate.opsForValue().set("fileTreeCache::fileTree", noteFiles, 300, TimeUnit.SECONDS);
		log.info("[파일 트리 업데이트] 디스크에서 조회 후 Redis에 저장");

		return noteFiles;
	}

}
