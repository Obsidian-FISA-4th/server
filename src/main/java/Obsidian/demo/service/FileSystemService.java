package Obsidian.demo.service;

import Obsidian.demo.apiPayload.code.status.ErrorStatus;
import Obsidian.demo.apiPayload.exception.GeneralException;
import Obsidian.demo.dto.FileNodeDto;
import Obsidian.demo.dto.MarkDownSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileSystemService {

	private final String rootPath = System.getProperty("user.home");
	private final String vaultPath = rootPath + "/note/";

	private final String publicPath = rootPath + "/public/";


	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 루트 경로부터 파일 트리 조회
	 * Get/api/files 요청이 들어오면, Redis에서 캐싱된 데이터를 반환한다.
	 * 캐시된 데이터가 없을 경우, 직접 디스크에서 조회한다음에 Redis에 저장한다고 생각하면 됩니다.
	 */

	@Cacheable(value = "fileTreeCache", key = "'fileTree'", unless = "#result == null")
	public List<FileNodeDto> getFileTree() throws IOException {
		Path root = Paths.get(vaultPath);

		if (!Files.exists(root)) {
			Files.createDirectories(root);
		}

		List<FileNodeDto> result = new ArrayList<>();
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(root);
		for (Path path : directoryStream) {
			result.add(buildFileNode(path));
		}

		// 직접 TTL 적용
		redisTemplate.opsForValue().set("fileTreeCache::fileTree", result, 300, TimeUnit.SECONDS);

		log.info("[파일 트리 조회] 캐시 없이 디스크에서 직접 조회");
		return result;
	}

	/**
	 * 캐시 삭제(파일 생성, 이동, 삭제 시 호출)
	 */
	@CacheEvict(value = "fileTreeCache", key = "'fileTree'")
	public void evictFileTreeCache() {
		log.info("[캐시 삭제] fileTreeCache 삭제됨");

		//Redis에 저장된 캐시도 강제로 삭제
		redisTemplate.delete("fileTreeCache::fileTree");
	}

	/**
	 * 파일 또는 폴더 생성
	 */
	public void createFileOrFolder(String relativePath, String type) throws IOException {
		String fullPath = vaultPath + relativePath;
		Path path = Paths.get(fullPath);
		System.out.println(fullPath);
		if (Files.exists(path)) {
			throw new RuntimeException("이미 존재하는 파일/폴더: " + fullPath);
		}

		if ("file".equalsIgnoreCase(type)) {
			Files.createFile(path);
			log.info("파일 생성: {}", fullPath);
		} else if ("folder".equalsIgnoreCase(type)) {
			Files.createDirectories(path);
			log.info("폴더 생성: {}", fullPath);
		} else {
			throw new IllegalArgumentException("잘못된 type 값: " + type);
		}

		// 캐시 삭제 (이전 데이터 무효화)
		evictFileTreeCache();
	}

	public void moveFileOrFolder(String fromPath, String toPath) throws IOException {
		String sourcePath = rootPath + File.separator + fromPath;
		String targetPath = rootPath + File.separator + toPath;

		Path source = Paths.get(sourcePath);
		Path target = Paths.get(targetPath);

		if (!Files.exists(source)) {
			throw new RuntimeException("이동할 대상이 존재하지 않음: " + sourcePath);
		}
		if (!Files.exists(target)) {
			throw new RuntimeException("대상 경로가 존재하지 않음: " + targetPath);
		}
		if (!Files.isDirectory(target)) {
			throw new RuntimeException("대상 경로가 폴더가 아님: " + targetPath);
		}

		Path resolvedTarget = target.resolve(source.getFileName());
		Files.move(source, resolvedTarget, StandardCopyOption.REPLACE_EXISTING);
		log.info("이동 완료: {} → {}", sourcePath, resolvedTarget);

		// 캐시 삭제
		evictFileTreeCache();
	}

	/**
	 * 파일 또는 폴더 삭제 (재귀 삭제 지원)
	 */
	public void deleteFileOrFolder(String relativePath) throws IOException {
		String fullPath = rootPath + File.separator + relativePath;
		Path path = Paths.get(fullPath);

		if (!Files.exists(path)) {
			throw new RuntimeException("삭제할 대상이 존재하지 않음: " + fullPath);
		}

		// 파일이면 바로 삭제
		if (!Files.isDirectory(path)) {
			Files.delete(path);
			log.info("파일 삭제 완료 : {}", fullPath);
			return;
		} else {

			// 폴더라면, 내부 파일/폴더 먼저 삭제후 최종 폴더 삭제
			Files.walk(path)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);

			log.info("폴더 및 내부 파일 삭제 완료: {}", fullPath);
		}
		// 캐시 삭제
		evictFileTreeCache();
	}

	/**
	 *트리 구조 생성 (재귀적 탐색)
	 */
	private FileNodeDto buildFileNode(Path path) throws IOException {
		boolean isFolder = Files.isDirectory(path);
		List<FileNodeDto> children = new ArrayList<>();

		if (isFolder) {
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
				for (Path child : directoryStream) {
					children.add(buildFileNode(child));
				}
			}
		}

		return new FileNodeDto(path.getFileName().toString(), isFolder, path.toString(), children);
	}

	public void saveMarkdown(MarkDownSaveRequestDTO requestDTO) {
		try {
			Path storagePath = Paths.get(vaultPath);
			if (!Files.exists(storagePath)) {
				Files.createDirectories(storagePath);
			}

			Path filePath = storagePath.resolve(requestDTO.getFileName() + ".md");
			Files.write(filePath, requestDTO.getContent().getBytes());
		} catch (IOException e) {
			log.error("Markdown 저장 중 오류 발생: {}", e.getMessage());
			throw new GeneralException(ErrorStatus.MARKDOWN_SAVE_ERROR);
		}
	}
}
