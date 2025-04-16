package Obsidian.demo.utils;

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

import Obsidian.demo.config.CustomProperties;
import Obsidian.demo.dto.FileNodeDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileSystemUtil {

	private final CustomProperties customProperties;

	private String rootPath;
	private String vaultPath;
	private String publicPath;

	@PostConstruct
	private void initPaths() {
		// Spring이 빈 주입 완료한 이후에 rootPath 설정

		this.rootPath = customProperties.getMode().equals("prod")
			? "/home/obsidian"
			: System.getProperty("user.home") + "/obsidian";
		System.out.println("customProperties.getMode() = " + customProperties.getMode());
		this.vaultPath = rootPath + "/note/";
		this.publicPath = rootPath + "/public/";

		log.info("FileSystem 초기화 완료 - rootPath: {}", rootPath);
	}

	private final RedisTemplate<String, Object> redisTemplate;

	public Path findPublishedFile(String relativePath) {
		String FilePath = publicPath + relativePath;
		Path mdPath = Paths.get(FilePath);
		return Files.exists(mdPath) ? mdPath : null;
	}

	public void deleteRecursively(Path path) throws IOException {
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

	public FileNodeDto buildFileNode(Path path) throws IOException {
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

	public void markPublishedFiles(List<FileNodeDto> files, Set<String> publicHtmlFiles) {
		Path vaultRoot = Paths.get(vaultPath); // vaultPath

		for (FileNodeDto file : files) {
			if (!file.isFolder() && file.getName().endsWith(".md")) {
				Path filePath = Paths.get(file.getPath());
				if (filePath.startsWith(vaultRoot)) {
					String relativePathStr = vaultRoot.relativize(filePath).toString();
					if (publicHtmlFiles.contains(relativePathStr)) {
						file.setPublish(true);
					}
				}
			}
			if (file.isFolder()) {
				markPublishedFiles(file.getChildren(), publicHtmlFiles);
			}
		}
	}

	@CacheEvict(value = "fileTreeCache", key = "'fileTree'")
	public List<FileNodeDto> updateFileTree() {
		Path root = Paths.get(vaultPath);
		Path publicRoot = Paths.get(publicPath);
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
		sortFileNodes(noteFiles);
		Set<String> publicFiles = new HashSet<>();

		if (Files.exists(publicRoot)) {
			try (Stream<Path> paths = Files.walk(publicRoot)) {
				paths
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".md"))
					.forEach(path -> {
						Path relative = publicRoot.relativize(path);
						publicFiles.add(relative.toString());
					});
			} catch (IOException e) {
				log.error("Public 폴더 조회 중 오류 발생: {}", e.getMessage());
			}
		}

		markPublishedFiles(noteFiles, publicFiles);

		redisTemplate.opsForValue().set("fileTreeCache::fileTree", noteFiles, 300, TimeUnit.SECONDS);
		log.info("[파일 트리 업데이트] 디스크에서 조회 후 Redis에 저장");

		return noteFiles;
	}
	private void sortFileNodes(List<FileNodeDto> nodes) {
		nodes.sort(Comparator
			.comparing(FileNodeDto::isFolder).reversed()  // 폴더 먼저
			.thenComparing(FileNodeDto::getName, String.CASE_INSENSITIVE_ORDER)  // 이름순 오름차순
		);

		for (FileNodeDto node : nodes) {
			if (node.getChildren() != null && !node.getChildren().isEmpty()) {
				sortFileNodes(node.getChildren());  // 자식들도 재귀적으로 정렬
			}
		}
	}
}
