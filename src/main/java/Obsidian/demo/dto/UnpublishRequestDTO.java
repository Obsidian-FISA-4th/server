package Obsidian.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnpublishRequestDTO {
	private List<String> filePaths;
}
