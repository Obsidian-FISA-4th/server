package Obsidian.demo.controller;

import Obsidian.demo.apiPayload.ApiResponse;
import Obsidian.demo.service.ImageUploadService;
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
    public ApiResponse<List<String>> uploadImageFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> fileNames = imageUploadService.uploadImageFiles(files);
            return ApiResponse.onSuccess(fileNames.stream().toList());
        } catch (IOException e) {
            return ApiResponse.onFailure("CREATE_ERROR", e.getMessage(), null);
        }
    }
}
