package Obsidian.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/temp")
public class TempController {

    // 폴더 및 파일이 저장될 루트 경로 지정
    private static final String BASE_PATH = "/Users/iseoyeon/Desktop/Obsidian-FISA-4th";

    // 폴더 및 파일 생성
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam("path") String path) {
        File target = new File(BASE_PATH, path);

        try {
            if (path.contains(".")) { // 확장자가 있으면 파일로 인식
                File parentDir = target.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if (target.createNewFile()) {
                    return ResponseEntity.ok("파일 생성됨: " + target.getPath());
                }
            } else { // 폴더 생성
                if (target.mkdirs()) {
                    return ResponseEntity.ok("폴더 생성됨: " + target.getPath());
                }
            }
            return ResponseEntity.badRequest().body("이미 존재하거나 생성 실패");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("에러: " + e.getMessage());
        }
    }

    // 폴더 및 파일 위치 이동
    @PutMapping("/move")
    public ResponseEntity<String> move(@RequestParam("path") String path, @RequestParam("to") String to) {
        File source = new File(BASE_PATH, path);
        File destinationDir = new File(BASE_PATH, to);
        File destination = new File(destinationDir, source.getName()); // 수정된 부분

        if (!source.exists()) {
            return ResponseEntity.badRequest().body("이동 실패: 원본 경로 없음");
        }

        if (destination.exists()) {
            return ResponseEntity.badRequest().body("이동 실패: 대상 경로에 같은 이름의 폴더가 이미 존재");
        }

        try {
            copyRecursively(source, destination);
            deleteRecursively(source);
            return ResponseEntity.ok("이동 완료: " + path + " → " + destination.getPath());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("이동 실패: " + e.getMessage());
        }
    }


    // 폴더 및 파일 복사
    private void copyRecursively(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            destination.mkdirs(); // 대상 디렉토리 생성
            for (File file : Objects.requireNonNull(source.listFiles())) {
                copyRecursively(file, new File(destination, file.getName()));
            }
        } else {
            java.nio.file.Files.copy(source.toPath(), destination.toPath());
        }
    }

    // 폴더 및 파일 삭제 (기존 방식 유지)
    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                deleteRecursively(subFile);
            }
        }
        file.delete();
    }

    // 폴더 및 파일 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("path") String path) {
        File target = new File(BASE_PATH, path);

        if (!target.exists()) {
            return ResponseEntity.badRequest().body("삭제 실패: 경로 없음");
        }

        deleteRecursively(target);
        return ResponseEntity.ok("삭제 완료: " + target.getPath());
    }
}
