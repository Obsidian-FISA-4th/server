package Obsidian.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnpublishResultDTO {
	private List<String> deletedFiles;
	private List<String> failedFiles;

}
