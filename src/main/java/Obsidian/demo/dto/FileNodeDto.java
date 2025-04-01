package Obsidian.demo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name", "isFolder", "isPublish", "path", "children"})
public class FileNodeDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isFolder;
    private boolean isPublish;
	private String path;
	private List<FileNodeDto> children;
}
