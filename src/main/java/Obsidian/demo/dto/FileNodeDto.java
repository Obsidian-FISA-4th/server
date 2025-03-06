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
    private static final long serialVersionUID = 1L; // 직렬화 버전을 관리
    private String name;
    private boolean isFolder;
    private String path;
    private List<FileNodeDto> children;
}
