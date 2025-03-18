package Obsidian.demo.controller;

import Obsidian.demo.apiPayload.ApiResponse;
import Obsidian.demo.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageUploadService imageUploadService;

	@PostMapping("/images")
	@Operation(summary = "이미지 파일 업로드", description = "이미지 파일을 업로드하고, 업로드된 파일들의 URL을 반환합니다.")
	public ApiResponse<List<String>> uploadImageFiles(@RequestParam("files") MultipartFile[] files) {
		try {
			List<String> fileNames = imageUploadService.uploadImageFiles(files);
			return ApiResponse.onSuccess(fileNames.stream().toList());
		} catch (IOException e) {
			return ApiResponse.onFailure("CREATE_ERROR", e.getMessage(), null);
		}
	}
}
