package Obsidian.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import Obsidian.demo.apiPayload.ApiResponse;
import Obsidian.demo.dto.PublishRequestDTO;
import Obsidian.demo.dto.PublishResultDTO;
import Obsidian.demo.dto.UnpublishRequestDTO;
import Obsidian.demo.dto.UnpublishResultDTO;
import Obsidian.demo.service.PublishService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class PublishController {

	private final PublishService publishService;

	@PostMapping("/publish")
	@Operation(summary = "마크다운 배포", description = "강사가 선택한 파일 리스트를 받아 HTML로 변환해 배포경로에 저장합니다.")
	public ApiResponse<PublishResultDTO> publishMarkdown(@RequestBody PublishRequestDTO request) {
		return ApiResponse.onSuccess(publishService.publishMarkdownFiles(request));
	}
	@DeleteMapping("/unpublish")
	@Operation(summary = "마크다운 회수", description = "회수하고 싶은 파일들을 배포 경로에서 삭제합니다.")
	public ApiResponse<UnpublishResultDTO> unpublishMarkdown(@RequestBody UnpublishRequestDTO request) {
		return ApiResponse.onSuccess(publishService.unPublishFiles(request));
	}
}
