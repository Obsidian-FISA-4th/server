package Obsidian.demo.service;

import static Obsidian.demo.utils.FileSystemUtil.*;

import Obsidian.demo.apiPayload.code.status.ErrorStatus;
import Obsidian.demo.apiPayload.exception.GeneralException;
import Obsidian.demo.dto.FileNodeDto;
import Obsidian.demo.dto.MarkDownSaveRequestDTO;
import Obsidian.demo.utils.FileSystemUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileSystemService {

	private final String rootPath = System.getProperty("user.home") + "/obsidian";
	private final String vaultPath = rootPath + "/note/";
	private final String publicPath = rootPath + "/public/";

	private final RedisTemplate<String, Object> redisTemplate;

	private final FileSystemUtil fileSystemUtil;

	public List<FileNodeDto> getFileTree() {
		String cacheKey = "fileTreeCache::fileTree";

		List<FileNodeDto> cachedTree = (List<FileNodeDto>)redisTemplate.opsForValue().get(cacheKey);
		if (cachedTree != null) {
			log.info("[파일 트리 조회] Redis 캐시에서 가져옴");
			return cachedTree;
		}

		return fileSystemUtil.updateFileTree();
	}

	


	public void createFileOrFolder(String relativePath, String type) throws IOException {
		String fullPath = vaultPath + relativePath;
		Path path = Paths.get(fullPath);
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

		fileSystemUtil.updateFileTree();
	}

	public void moveFileOrFolder(String fromPath, String toPath) throws IOException {
		Path source = Paths.get(vaultPath + fromPath);
		Path target = Paths.get(vaultPath + toPath);

		if (!Files.exists(source)) {
			throw new RuntimeException("이동할 대상이 존재하지 않음: " + source);
		}
		if (!Files.exists(target)) {
			throw new RuntimeException("대상 경로가 존재하지 않음: " + target);
		}
		if (!Files.isDirectory(target)) {
			throw new RuntimeException("대상 경로가 폴더가 아님: " + target);
		}
		Path resolvedTarget = target.resolve(source.getFileName());
		Files.move(source, resolvedTarget, StandardCopyOption.REPLACE_EXISTING);
		log.info("이동 완료: {} → {}", source, resolvedTarget);

		// HTML 동기화
		Path htmlPath = findPublishedHtmlFile(fromPath);
		if (htmlPath != null) {
			Path htmlTarget = Paths.get(publicPath + toPath)
				.resolve(source.getFileName().toString().replace(".md", ".html"));
			Files.move(htmlPath, htmlTarget, StandardCopyOption.REPLACE_EXISTING);
			log.info("연동된 HTML 이동 완료: {} → {}", htmlPath, htmlTarget);
		}
		fileSystemUtil.updateFileTree();

	}

	public void deleteFileOrFolder(String relativePath) throws IOException {
		Path path = Paths.get(vaultPath + relativePath);

		if (!Files.exists(path)) {
			throw new RuntimeException("삭제할 대상이 존재하지 않음: " + path);
		}

		deleteRecursively(path);

		log.info("삭제 완료: {}", path);

		Path htmlPath = findPublishedHtmlFile(relativePath);
		if (htmlPath != null) {
			deleteRecursively(htmlPath);
			log.info("연동된 HTML 삭제 완료: {}", htmlPath);
		}

		fileSystemUtil.updateFileTree();
	}

	public void saveMarkdown(MarkDownSaveRequestDTO requestDTO) {
		try {
			Path storagePath = Paths.get(vaultPath);
			if (!Files.exists(storagePath)) {
				Files.createDirectories(storagePath);
			}

			Path filePath = storagePath.resolve(requestDTO.getFileName() + ".md");
			Files.write(filePath, requestDTO.getContent().getBytes());

			fileSystemUtil.updateFileTree();
		} catch (IOException e) {
			log.error("Markdown 저장 중 오류 발생: {}", e.getMessage());
			throw new GeneralException(ErrorStatus.MARKDOWN_SAVE_ERROR);
		}
	}
}
