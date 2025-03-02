package Obsidian.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileNodeDto {
    private String name;
    private boolean isFolder;
    private String path;
    private List<FileNodeDto> children;
}
