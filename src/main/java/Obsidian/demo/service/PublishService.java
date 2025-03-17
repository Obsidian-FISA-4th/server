package Obsidian.demo.service;

import Obsidian.demo.apiPayload.code.status.ErrorStatus;
import Obsidian.demo.apiPayload.exception.GeneralException;
import Obsidian.demo.dto.PublishRequestDTO;
import Obsidian.demo.dto.PublishResultDTO;
import Obsidian.demo.dto.UnpublishRequestDTO;
import Obsidian.demo.dto.UnpublishResultDTO;

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
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublishService {

	private final String homeDir = System.getProperty("user.home");
	private final String vaultPath = homeDir + "/note/";
	private final String publicPath = homeDir + "/note/public/";

	public PublishResultDTO publishMarkdownFiles(PublishRequestDTO request) {
		List<String> filePaths = request.getFilePaths();
		List<String> publishedFiles = filePaths.stream()
			.map(this::processMarkdownFile)
			.collect(Collectors.toList());

		return PublishResultDTO.builder()
			.filePaths(publishedFiles)
			.build();
	}
	public UnpublishResultDTO unPublishFiles(UnpublishRequestDTO request) {
		List<String> deletedFiles = deleteFiles(request.getFilePaths());
		deleteEmptyDirectories(request.getFilePaths());
		return new UnpublishResultDTO(deletedFiles);
	}

	// 개별 파일 삭제
	private List<String> deleteFiles(List<String> filePaths) {
		List<String> deletedFiles = new ArrayList<>();

		for (String filePath : filePaths) {
			File file = new File(publicPath + filePath);
			if (file.exists() && file.isFile()) {
				try {
					System.out.println("Deleted File: " + file.getName());
					Files.delete(file.toPath());
					deletedFiles.add(file.getName());
				} catch (Exception e) {
					throw new GeneralException(ErrorStatus.PUBLIC_DIRECTORY_CLEAR_ERROR);
				}
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
			if (parentDir != null && parentDir.exists() && parentDir.isDirectory() && checkedDirectories.add(parentDir)) {
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

	private String processMarkdownFile(String filePath) {
		File markdownFile = validateFileExistence(filePath);

		String relativePath = filePath.replaceFirst("^" + vaultPath, "");
		String parentDir = new File(relativePath).getParent();
		String fileName = new File(relativePath).getName().replace(".md", ".html");

		File htmlFolder = (parentDir != null)
			? new File(publicPath, parentDir)
			: new File(publicPath);
		File htmlFile = new File(htmlFolder, fileName);

		createDirectoryIfNotExists(htmlFolder);

		String markdownContent = readMarkdownFile(markdownFile);
		String htmlContent = convertMarkdownToHtml(markdownContent);
		saveHtmlFile(htmlFile, htmlContent);

		return htmlFile.getPath();
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

	private String readMarkdownFile(File markdownFile) {
		try {
			return Files.readString(markdownFile.toPath(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new GeneralException(ErrorStatus.MARKDOWN_READ_ERROR);
		}
	}

	private String convertMarkdownToHtml(String markdown) {
		try {
			Parser parser = Parser.builder().build();
			HtmlRenderer renderer = HtmlRenderer.builder().build();
			Node document = parser.parse(markdown);
			return "<!DOCTYPE html>\n" +
				"<html lang=\"ko\">\n" +
				"<head>\n" +
				"    <meta charset=\"UTF-8\">\n" +
				"    <title>Markdown Render</title>\n" +
				"</head>\n" +
				"<body>\n" +
				renderer.render(document) +
				"\n</body>\n</html>";
		} catch (Exception e) {
			throw new GeneralException(ErrorStatus.MARKDOWN_CONVERT_ERROR);
		}
	}

	private void saveHtmlFile(File htmlFile, String htmlContent) {
		try {
			Files.writeString(htmlFile.toPath(), htmlContent, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new GeneralException(ErrorStatus.HTML_SAVE_ERROR);
		}
	}

	private boolean clearPublicDirectory() {
		Path publicDir = Paths.get(publicPath);

		if (Files.exists(publicDir)) {
			try {
				Files.walkFileTree(publicDir, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file); // 파일 삭제
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir); // 디렉토리 삭제
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				throw new GeneralException(ErrorStatus.PUBLIC_DIRECTORY_CLEAR_ERROR);
			}
		}

		return publicDir.toFile().mkdirs(); // 삭제 후 다시 폴더 생성
	}
}
