package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.service.DocumentIngestionService;
import com.agilewildcats.chattyCatty.util.PdfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/docs")
public class DocumentController {

    private final DocumentIngestionService ingestionService;
    private final static Logger logger = LoggerFactory.getLogger(DocumentController.class);

    public DocumentController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    // Upload a text file
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String content = filename != null && filename.endsWith(".pdf")
                ? PdfUtil.pdfFilesToText(file)
                : new String(file.getBytes(), StandardCharsets.UTF_8);
        logger.debug("uploadFile : fileName={}", filename);

        ingestionService.addDocument(content, filename);
        return "File '" + filename + "' uploaded and embedded.";
    }

    // Upload multiple files
    @PostMapping("/uploadFiles")
    public String uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String content = filename != null && filename.endsWith(".pdf")
                    ? PdfUtil.pdfFilesToText(file)
                    : new String(file.getBytes(), StandardCharsets.UTF_8);

            ingestionService.addDocument(content, filename);
        }
        logger.debug("uploadFiles : files.size={}", files.size());
        return files.size() + " documents uploaded and embedded.";
    }
}
