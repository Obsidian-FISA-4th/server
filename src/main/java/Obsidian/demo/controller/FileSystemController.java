package Obsidian.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Obsidian.demo.apiPayload.ApiResponse;
import Obsidian.demo.dto.FileCreateRequestDto;
import Obsidian.demo.dto.FileNodeDto;
import Obsidian.demo.dto.MarkDownSaveRequestDTO;
import Obsidian.demo.service.FileSystemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileSystemController {

    private final FileSystemService fileSystemService;

    /**
     * 파일 및 폴더 조회 (트리 구조)
     */
    @GetMapping
    public ApiResponse<List<FileNodeDto>> getFileTree() {
        try {
            return ApiResponse.onSuccess(fileSystemService.getFileTree());
        } catch (IOException e) {
            return ApiResponse.onFailure("IO_ERROR", "파일 트리 조회 실패", null);
        }
    }

    /**
     * 파일 또는 폴더 생성
     */
    @PostMapping("/create")
    public ApiResponse<String> createFileOrFolder(@RequestParam String path, @RequestParam String type) {
        try {
            fileSystemService.createFileOrFolder(path, type);
            return ApiResponse.onSuccess("파일 또는 폴더 생성 성공");
        } catch (Exception e) {
            return ApiResponse.onFailure("CREATE_ERROR", e.getMessage(), null);
        }
    }

    /**
     * 파일 또는 폴더 이동
     */
    @PutMapping("/move")
    public ApiResponse<String> moveFileOrFolder(
            @RequestParam String path,
            @RequestParam String to) {
        try {
            fileSystemService.moveFileOrFolder(path, to);
            return ApiResponse.onSuccess("파일 또는 폴더 이동 성공");
        } catch (Exception e) {
            return ApiResponse.onFailure("MOVE_ERROR", e.getMessage(), null);
        }
    }

    /**
     * 파일 또는 폴더 삭제
     */
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteFileOrFolder(@RequestParam String path){
        try{
            fileSystemService.deleteFileOrFolder(path);
            return ApiResponse.onSuccess("파일 또는 폴더 삭제 성공");
        } catch (Exception e) {
            return ApiResponse.onFailure("DELETE_ERROR", e.getMessage(), null);
        }
    }
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
