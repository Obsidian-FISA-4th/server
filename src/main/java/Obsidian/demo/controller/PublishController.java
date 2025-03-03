package Obsidian.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import Obsidian.demo.dto.PublishRequestDTO;
import Obsidian.demo.service.command.PublishService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class PublishController {

	private final PublishService publishService;

	@PostMapping("/publish")
	public ResponseEntity<?> publishMarkdown(@RequestBody PublishRequestDTO request) {
		return publishService.publishMarkdownFiles(request.getFilePaths());
	}
}
