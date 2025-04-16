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

import Obsidian.demo.config.CustomProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {
	private final CustomProperties customProperties;

	private String rootPath;
	private String imagePath;

	@PostConstruct
	private void initPaths() {

		this.rootPath = customProperties.getMode().equals("prod")
			? "/home/obsidian"
			: System.getProperty("user.home") + "/obsidian";
		System.out.println("customProperties.getMode() = " + customProperties.getMode());
		this.imagePath = rootPath + "/images/";

		log.info("FileSystem 초기화 완료 - rootPath: {}", rootPath);
	}

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

				fileNames.add(imagePath + fileName);
			}
		}

		return fileNames;
	}
}
