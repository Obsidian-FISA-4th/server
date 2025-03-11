package Obsidian.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import Obsidian.demo.apiPayload.ApiResponse;
import Obsidian.demo.dto.PublishRequestDTO;
import Obsidian.demo.dto.PublishResultDTO;
import Obsidian.demo.dto.UnpublishRequestDTO;
import Obsidian.demo.dto.UnpublishResultDTO;
import Obsidian.demo.service.PublishService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class PublishController {

	private final PublishService publishService;

	@PostMapping("/publish")
	public ApiResponse<PublishResultDTO> publishMarkdown(@RequestBody PublishRequestDTO request) {
		return ApiResponse.onSuccess(publishService.publishMarkdownFiles(request));
	}
	@DeleteMapping("/unpublish")
	public ApiResponse<UnpublishResultDTO> unpublishMarkdown(@RequestBody UnpublishRequestDTO request) {
		return ApiResponse.onSuccess(publishService.unPublishFiles(request));
	}
}
