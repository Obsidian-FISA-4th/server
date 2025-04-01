package Obsidian.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Obsidian.demo.apiPayload.ApiResponse;
import Obsidian.demo.dto.FileNodeDto;
import Obsidian.demo.dto.MarkDownSaveRequestDTO;
import Obsidian.demo.service.FileSystemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "파일 시스템 API", description = "파일 및 폴더 관련 기능을 제공하는 API")
public class FileSystemController {

	private final FileSystemService fileSystemService;

	@Operation(summary = "파일 및 폴더 조회", description = "~/note 폴더 하위 파일 및 폴더의 트리 구조를 조회합니다.")
	@GetMapping
	public ApiResponse<List<FileNodeDto>> getFileTree() {
		return ApiResponse.onSuccess(fileSystemService.getFileTree());
	}

	@Operation(summary = "파일 또는 폴더 생성", description = "지정된 경로에 파일 또는 폴더를 생성합니다.")
	@PostMapping("/create")
	public ApiResponse<String> createFileOrFolder(@RequestParam("path") String path,
												  @RequestParam("type") String type) {
		try {
			fileSystemService.createFileOrFolder(path, type);
			return ApiResponse.onSuccess("파일 또는 폴더 생성 성공");
		} catch (Exception e) {
			return ApiResponse.onFailure("CREATE_ERROR", e.getMessage(), null);
		}
	}

	@Operation(summary = "파일 또는 폴더 이동", description = "파일 또는 폴더를 지정된 타겟 폴더로 이동합니다.")
	@Parameters(
			{@Parameter(name = "path", description = "옮길 파일,폴더 경로입력"),
					@Parameter(name = "to", description = "target 폴더 입력 (주의! 폴더만 입력)" )}
	)
	@PutMapping("/move")
	public ApiResponse<String> moveFileOrFolder(
			@RequestParam("path") String path,

			@RequestParam("to") String to) {
		try {
			fileSystemService.moveFileOrFolder(path, to);
			return ApiResponse.onSuccess("파일 또는 폴더 이동 성공");
		} catch (Exception e) {
			return ApiResponse.onFailure("MOVE_ERROR", e.getMessage(), null);
		}
	}

	@Operation(summary = "파일 또는 폴더 삭제", description = "지정된 파일 또는 폴더를 삭제합니다.")
	@DeleteMapping("/delete")
	public ApiResponse<String> deleteFileOrFolder(@RequestParam("path") String path) {
		try {
			fileSystemService.deleteFileOrFolder(path);
			return ApiResponse.onSuccess("파일 또는 폴더 삭제 성공");
		} catch (Exception e) {
			return ApiResponse.onFailure("DELETE_ERROR", e.getMessage(), null);
		}
	}

	@Operation(summary = "파일 내용 읽기", description = "지정된 경로의 파일 내용을 읽어옵니다.")
	@GetMapping("/content")
	public ResponseEntity<ApiResponse<String>> getFileContent(@RequestParam String path) {
		try {
			// 서비스 메서드 호출로 파일 내용 읽기
			String content = fileSystemService.readFileContent(path);
			return ResponseEntity.ok(ApiResponse.onSuccess(content));
		} catch (IOException e) {
			return ResponseEntity.status(500).body(ApiResponse.onFailure("FILE_READ_ERROR", e.getMessage(), null));
		}
	}

	@Operation(summary = "마크다운 저장", description = "마크다운 파일을 저장합니다.")
	@PostMapping("/save")
	public ResponseEntity<ApiResponse<String>> saveMarkdown(@RequestBody MarkDownSaveRequestDTO requestDTO) {
		try {
			fileSystemService.saveMarkdown(requestDTO);
			return ResponseEntity.ok(ApiResponse.onSuccess("마크다운 파일 저장 성공"));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(ApiResponse.onFailure("MARKDOWN_SAVE_ERROR", e.getMessage(), null));
		}
	}

}