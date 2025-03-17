package Obsidian.demo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class PublishResultDTO {
	private List<String> filePaths;

	@Builder
	public PublishResultDTO(List<String> filePaths) {
		this.filePaths = filePaths;
	}
}
