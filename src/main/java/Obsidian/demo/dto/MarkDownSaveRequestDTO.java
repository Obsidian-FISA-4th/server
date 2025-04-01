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

	private String filePath;
	private String fileName;
	private String content;
}