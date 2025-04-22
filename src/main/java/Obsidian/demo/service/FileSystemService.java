package Obsidian.demo.service;

import Obsidian.demo.apiPayload.code.status.ErrorStatus;
import Obsidian.demo.apiPayload.exception.GeneralException;
import Obsidian.demo.config.CustomProperties;
import Obsidian.demo.dto.FileNodeDto;
import Obsidian.demo.dto.MarkDownSaveRequestDTO;
import Obsidian.demo.utils.FileSystemUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileSystemService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final FileSystemUtil fileSystemUtil;
	private final CustomProperties customProperties;

	private String rootPath;
	private String vaultPath;
	private String publicPath;

	@PostConstruct
	private void initPaths() {
		// Spring이 빈 주입 완료한 이후에 rootPath 설정

		this.rootPath = customProperties.getMode().equals("prod") ? "/home/ubuntu" :
			System.getProperty("user.home") + "/obsidian";
		System.out.println("customProperties.getMode() = " + customProperties.getMode());
		this.vaultPath = rootPath + "/note/";
		this.publicPath = rootPath + "/public/";

		log.info("FileSystem 초기화 완료 - rootPath: {}", rootPath);
	}

	public List<FileNodeDto> getFileTree() {

		String cacheKey = "fileTreeCache::fileTree";

		List<FileNodeDto> cachedTree = (List<FileNodeDto>)redisTemplate.opsForValue().get(cacheKey);
		if (cachedTree != null) {
			return cachedTree;
		}

		return fileSystemUtil.updateFileTree();
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

		// 파일일 경우 .md 확장자 추가
		if ("file".equalsIgnoreCase(type) && !fullPath.endsWith(".md")) {
			fullPath += ".md";
		}

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

		// 캐시 삭제 (이전 데이터 무효화)
		evictFileTreeCache();
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

		Path sourcePath = fileSystemUtil.findPublishedFile(fromPath);

		if (sourcePath != null) {
			Path targetPath = Paths.get(publicPath + toPath);
			Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			log.info("연동된 파일 이동 완료: {} → {}", sourcePath, targetPath);
		}
		fileSystemUtil.updateFileTree();

	}

	public void deleteFileOrFolder(String relativePath) throws IOException {
		Path path = Paths.get(vaultPath + relativePath);

		if (!Files.exists(path)) {
			throw new RuntimeException("삭제할 대상이 존재하지 않음: " + path);
		}

		fileSystemUtil.deleteRecursively(path);

		log.info("삭제 완료: {}", path);

		Path publishedFile = fileSystemUtil.findPublishedFile(relativePath);
		if (publishedFile != null) {
			fileSystemUtil.deleteRecursively(publishedFile);
		}

		fileSystemUtil.updateFileTree();
	}

	public void updateMarkdown(MarkDownSaveRequestDTO requestDTO) {
		try {
			Path filePath = Paths.get(requestDTO.getFilePath());

			// 파일 이름 추출
			String fileName = filePath.getFileName().toString();
			log.info("업데이트할 파일 이름: {}", fileName);

			// 파일 존재 여부 확인
			if (!Files.exists(filePath)) {
				throw new RuntimeException("파일이 존재하지 않습니다: " + filePath);
			}

			// 파일 내용 업데이트
			Files.write(filePath, requestDTO.getContent().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

			log.info("Markdown 파일 내용 업데이트 성공: {}", filePath);
		} catch (IOException e) {
			log.error("Markdown 내용 업데이트 중 오류 발생: {}", e.getMessage());
			throw new GeneralException(ErrorStatus.MARKDOWN_SAVE_ERROR);
		}
	}

	public String readFileContent(String relativePath) throws IOException {
		Path filePath =
			relativePath.startsWith(vaultPath) ? Paths.get(relativePath) : Paths.get(vaultPath, relativePath);

		// 파일 존재 여부 확인
		if (!Files.exists(filePath)) {
			throw new RuntimeException("파일을 찾을 수 없습니다: " + filePath);
		}

		// 파일 내용 읽기
		return Files.readString(filePath);
	}

	public void renameFile(String path, String newName) throws IOException {
		Path sourcePath = Paths.get(vaultPath + path);

		if (!Files.exists(sourcePath)) {
			throw new RuntimeException("이름을 변경할 파일이 존재하지 않음: " + path);
		}

		Path parentPath = sourcePath.getParent();
		Path renamedPath = parentPath.resolve(newName);

		Files.move(sourcePath, renamedPath, StandardCopyOption.REPLACE_EXISTING);
		log.info("파일 이름 변경 완료: {} → {}", sourcePath, renamedPath);

		fileSystemUtil.updateFileTree();
	}
}


