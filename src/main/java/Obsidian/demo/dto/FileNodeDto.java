package Obsidian.demo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileNodeDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean isFolder;
    private String path;
    private List<FileNodeDto> children;
}
