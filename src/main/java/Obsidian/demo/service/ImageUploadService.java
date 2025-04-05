package Obsidian.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {
    private final String homeDir = "/home/obsidian";
    // private final String homeDir = System.getProperty("user.home")+"/obsidian";
    private final String imagePath = homeDir + "/images/";

    public List<String> uploadImageFiles(MultipartFile[] multipartFiles) throws IOException {
        List<String> fileNames = new ArrayList<>();

        // 저장 폴더가 없으면 생성
        File directory = new File(imagePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        for (MultipartFile file : multipartFiles) {
            if (!file.isEmpty()) {
                // 파일 이름에 UUID 추가
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(imagePath, fileName);

                // 파일을 InputStream으로 읽어서 저장
                try (InputStream inputStream = file.getInputStream()) {
                    // 파일 덮어쓰기
                    Files.copy(inputStream, filePath);
                }

                fileNames.add(imagePath+fileName);
            }
        }

        return fileNames;
    }
}
