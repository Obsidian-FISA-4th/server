package Obsidian.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileCreateRequestDto {
    private String parentPath;
    private String name;
    private boolean isFolder;
}