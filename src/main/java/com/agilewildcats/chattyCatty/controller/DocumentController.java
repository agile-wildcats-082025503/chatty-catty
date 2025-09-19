package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/docs")
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    public DocumentController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    // Upload plain text directly
    @PostMapping("/upload")
    public String uploadDoc(@RequestBody String content) {
        ingestionService.addDocument(content);
        return "Document uploaded and embedded.";
    }

    // Upload a text file
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        ingestionService.addDocument(content);
        return "File uploaded and embedded.";
    }
}
