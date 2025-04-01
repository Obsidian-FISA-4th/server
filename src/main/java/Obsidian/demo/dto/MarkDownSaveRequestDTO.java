package Obsidian.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MarkDownSaveRequestDTO {

	private String filePath; // 파일의 전체 경로
	private String fileName;
	private String content;
}
