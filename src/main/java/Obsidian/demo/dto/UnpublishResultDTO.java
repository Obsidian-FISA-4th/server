package Obsidian.demo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnpublishResultDTO {
	private List<String> filePaths;

	@Builder
	public UnpublishResultDTO(List<String> filePaths) {
		this.filePaths = filePaths;
	}

	public UnpublishResultDTO(List<String> deletedFiles, List<String> failedFiles) {
	}
}
