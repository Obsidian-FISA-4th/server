/*
package Obsidian.demo;

import org.junit.jupiter.api.Test;


import static org.apache.commons.io.file.PathUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import Obsidian.demo.apiPayload.code.status.ErrorStatus;
import Obsidian.demo.apiPayload.exception.GeneralException;
import Obsidian.demo.service.PublishService;

@ExtendWith(MockitoExtension.class) // Mockito 기반 테스트 설정
class PublishServiceTest {
	private PublishService publishService = new PublishService();

	@InjectMocks
	private PublishService mockPublishService;

	private String homeDir = System.getProperty("user.home");
	private String testVaultPath = homeDir + "/testNote/";
	private String testPublishPath = homeDir + "/testNote/public/";

	@BeforeEach
	private void setUp() throws IOException {
		ReflectionTestUtils.setField(publishService, "vaultPath", System.getProperty("user.home") + "/testNote/");
		ReflectionTestUtils.setField(publishService, "publicPath",
			System.getProperty("user.home") + "/testNote/public/");

		Files.createDirectories(Paths.get(testVaultPath));
		Files.createDirectories(Paths.get(testPublishPath));
		Files.writeString(Paths.get(testVaultPath + "sample.md"), "# Publish Test");
	}

	@AfterEach
	void tearDown() throws IOException {
		deleteDirectory(Paths.get(testVaultPath)); // 테스트 폴더 삭제
	}

	@Test
	@DisplayName("마크다운이 실제 서비스 메서드 호출 이후, html로 배포되어야함.")
	void MarkdownShouldConvertHtml() {

		//given
		List<String> paths = List.of("sample.md");

		//when
		List<String> result = publishService.publishMarkdownFiles(paths);

		//then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("/Users/sengjun0624/testNote/public/sample.html", result.get(0));
	}

	@Test
	@DisplayName("마크다운 파일이 존재하지 않을 때 MARKDOWN_FILE_NOT_FOUND 오류 발생")
	void shouldThrowWhenMarkdownFileNotFound() {
		// given
		String nonExistentFile = "non_existent.md";

		// when & then
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			publishService.validateFileExistence(nonExistentFile);
		});

		assertEquals(ErrorStatus.MARKDOWN_FILE_NOT_FOUND, exception.getErrorStatus());
	}

	@Test
	@DisplayName("마크다운 파일 읽기 실패 시 MARKDOWN_READ_ERROR 발생")
	void shouldThrowWhenReadingMarkdownFails() {
		// given
		PublishService spy = spy(publishService);
		doThrow(new IOException()).when(spy).readMarkdownFile(any());

		// when & then
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			ReflectionTestUtils.invokeMethod(spy, "readMarkdownFile", new Object[]{new Path("invalid_path.md")});
		});

		assertEquals(ErrorStatus.MARKDOWN_READ_ERROR, exception.getErrorStatus());
	}

	@Test
	@DisplayName("마크다운 변환 실패 시 MARKDOWN_CONVERT_ERROR 발생")
	void shouldThrowWhenMarkdownConversionFails() {
		// given
		PublishService spy = spy(publishService);
		doThrow(new RuntimeException()).when(spy).convertMarkdownToHtml(anyString());

		// when & then
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			spy.convertMarkdownToHtml("# Invalid Markdown");
		});

		assertEquals(ErrorStatus.MARKDOWN_CONVERT_ERROR, exception.getErrorStatus());
	}

	@Test
	@DisplayName("HTML 저장 실패 시 HTML_SAVE_ERROR 발생")
	void shouldThrowWhenHtmlSaveFails() {
		// given
		PublishService spy = spy(publishService);
		doThrow(new IOException()).when(spy).saveHtmlFile(any(), anyString());

		// when & then
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			spy.saveHtmlFile(new File("invalid_path.html"), "<html>Content</html>");
		});

		assertEquals(ErrorStatus.HTML_SAVE_ERROR, exception.getErrorStatus());
	}

	@Test
	@DisplayName("디렉토리 생성 실패 시 DIRECTORY_CREATE_ERROR 발생")
	void shouldThrowWhenCreatingDirectoryFails() {
		// given
		PublishService spy = spy(publishService);
		doThrow(new GeneralException(ErrorStatus.DIRECTORY_CREATE_ERROR)).when(spy).createDirectoryIfNotExists(any());

		// when & then
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			spy.createDirectoryIfNotExists(new File("invalid_directory"));
		});

		assertEquals(ErrorStatus.DIRECTORY_CREATE_ERROR, exception.getErrorStatus());
	}

	@Test
	@DisplayName("마크다운 배포 전 퍼블리시 폴더 삭제 실패 시 PUBLIC_DIRECTORY_CLEAR_ERROR를 Throw 한다.")
	void shouldThrowWhenCleanDirectoriesFailed() {
		// given
		PublishService spy = spy(mockPublishService);
		doReturn(false).when(spy).clearPublicDirectory();

		// when & then
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			spy.publishMarkdownFiles(List.of("sample.md"));
		});

		assertEquals(ErrorStatus.PUBLIC_DIRECTORY_CLEAR_ERROR, exception.getErrorStatus());
	}
}/Users/sengjun0624/Desktop/Obsidian/server/src/test/java/Obsidian/demo/PublishServiceTest.java:96:84
	java: java.nio.file.Path is abstract; cannot be instantiated
*/
