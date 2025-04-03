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

	public static Path findPublishedHtmlFile(String relativePath) {
		String htmlFilePath = publicPath + relativePath.replaceAll("\\.md$", ".html");
		Path htmlPath = Paths.get(htmlFilePath);
		return Files.exists(htmlPath) ? htmlPath : null;
	}

	public static void deleteRecursively(Path path) throws IOException {
		if (!Files.exists(path)) {
			return;
		}

		if (Files.isDirectory(path)) {
			try (Stream<Path> paths = Files.walk(path)) {
				paths.sorted(Comparator.reverseOrder())
						.forEach(p -> {
							try {
								Files.delete(p);
							} catch (IOException e) {
								log.error("파일 또는 디렉토리 삭제 중 오류 발생: {}", e.getMessage());
							}
						});
			}
		} else {
			Files.deleteIfExists(path);
		}
	}

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

	public static String getFileNameWithoutExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf('.');
		return (lastDotIndex != -1) ? fileName.substring(0, lastDotIndex) : fileName;
	}

	@CacheEvict(value = "fileTreeCache", key = "'fileTree'")
	public List<FileNodeDto> updateFileTree() {
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