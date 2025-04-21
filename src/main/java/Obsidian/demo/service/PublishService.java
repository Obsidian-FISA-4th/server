package Obsidian.demo.service;

import Obsidian.demo.apiPayload.code.status.ErrorStatus;
import Obsidian.demo.apiPayload.exception.GeneralException;
import Obsidian.demo.config.CustomProperties;
import Obsidian.demo.dto.PublishRequestDTO;
import Obsidian.demo.dto.PublishResultDTO;
import Obsidian.demo.dto.UnpublishRequestDTO;
import Obsidian.demo.dto.UnpublishResultDTO;
import Obsidian.demo.utils.FileSystemUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {

	private final FileSystemUtil fileSystemUtil;
	private final CustomProperties customProperties;

	private String rootPath;
	private String vaultPath;
	private String publicPath;

	@PostConstruct
	private void initPaths() {
		// Spring이 빈 주입 완료한 이후에 rootPath 설정

		this.rootPath = customProperties.getMode().equals("prod")
			? "/home/ubuntu"
			: System.getProperty("user.home") + "/obsidian";
		System.out.println("customProperties.getMode() = " + customProperties.getMode());
		this.vaultPath = rootPath + "/note/";
		this.publicPath = rootPath + "/public/";

		log.info("FileSystem 초기화 완료 - rootPath: {}", rootPath);
	}

	public PublishResultDTO publishMarkdownFiles(PublishRequestDTO request) {
		List<String> filePaths = request.getFilePaths();

		// 변경된 copyMarkdownFile() 사용
		List<String> publishedFiles = filePaths.stream()
			.map(this::copyMarkdownFile)  // 기존: processMarkdownFile → 변경됨
			.collect(Collectors.toList());

		fileSystemUtil.updateFileTree();

		return PublishResultDTO.builder()
			.filePaths(publishedFiles)
			.build();
	}

	public UnpublishResultDTO unPublishFiles(UnpublishRequestDTO request) {
		List<String> deletedFiles = new ArrayList<>();
		List<String> failedFiles = new ArrayList<>();

		deleteFiles(request.getFilePaths(), deletedFiles, failedFiles);
		deleteEmptyDirectories(deletedFiles);
		fileSystemUtil.updateFileTree();

		return new UnpublishResultDTO(deletedFiles, failedFiles);
	}

	// 개별 파일 삭제
	private List<String> deleteFiles(List<String> filePaths, List<String> deletedFiles, List<String> failedFiles) {
		for (String filePath : filePaths) {
			File file = new File(publicPath + filePath);
			if (file.exists() && file.isFile()) {
				try {
					System.out.println("Deleted File: " + file.getName());
					Files.delete(file.toPath());
					deletedFiles.add(filePath);  // 전체 경로 포함
				} catch (Exception e) {
					failedFiles.add(filePath);
				}
			} else {
				// 파일이 존재하지 않으면 실패로 처리
				failedFiles.add(filePath);
			}
		}

		return deletedFiles;
	}

	// 삭제 후 비어있는 폴더 삭제
	private void deleteEmptyDirectories(List<String> filePaths) {
		Set<File> checkedDirectories = new HashSet<>();

		for (String filePath : filePaths) {
			File parentDir = new File(publicPath + filePath).getParentFile();

			// 동일한 폴더를 중복 검사하지 않도록 Set 사용
			if (parentDir != null && parentDir.exists() && parentDir.isDirectory() && checkedDirectories.add(
				parentDir)) {
				File[] remainingFiles = parentDir.listFiles();
				if (remainingFiles == null || remainingFiles.length == 0) {
					try {
						System.out.println("Deleted Empty Folder: " + parentDir.getAbsolutePath());
						Files.delete(parentDir.toPath());
					} catch (Exception e) {
						throw new GeneralException(ErrorStatus.PUBLIC_DIRECTORY_CLEAR_ERROR);
					}
				}
			}
		}
	}

	private String copyMarkdownFile(String filePath) {
		File markdownFile = validateFileExistence(filePath);

		// 퍼블릭 경로에서 사용할 상대 경로 추출
		String relativePath = filePath.replaceFirst("^" + vaultPath, "");
		String parentDir = new File(relativePath).getParent();
		String fileName = new File(relativePath).getName(); // .md 유지

		// 대상 디렉토리 + 파일 경로 설정
		File targetFolder = (parentDir != null)
			? new File(publicPath, parentDir)
			: new File(publicPath);
		File targetFile = new File(targetFolder, fileName);

		// 디렉토리 없으면 생성
		createDirectoryIfNotExists(targetFolder);

		// 파일 복사 (덮어쓰기)
		try {
			Files.copy(markdownFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("마크다운 파일 복사 중 오류 발생", e);
		}
		return targetFile.getPath();
	}

	private File validateFileExistence(String filePath) {
		File markdownFile = new File(vaultPath + filePath);
		if (!markdownFile.exists()) {
			throw new GeneralException(ErrorStatus.MARKDOWN_FILE_NOT_FOUND);
		}
		return markdownFile;
	}

	private void createDirectoryIfNotExists(File directory) {
		if (!directory.exists() && !directory.mkdirs()) {
			throw new GeneralException(ErrorStatus.DIRECTORY_CREATE_ERROR);
		}
	}

}
