package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.model.Document;
import com.agilewildcats.chattyCatty.repo.DocumentRepository;
import com.agilewildcats.chattyCatty.util.EmbeddingUtils;
import com.agilewildcats.chattyCatty.util.TextChunker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final EmbeddingService embeddingService;
    private final DocumentRepository repository;

    public DocumentIngestionService(EmbeddingService embeddingService, DocumentRepository repository) {
        this.embeddingService = embeddingService;
        this.repository = repository;
    }

    public void addDocument(String content) {
        // Split into chunks
        List<String> chunks = TextChunker.chunkText(content, 1200);

        for (String chunk : chunks) {
            embeddingService.embedText(chunk).subscribe(embedding -> {
                Document doc = new Document();
                doc.setContent(chunk);
                doc.setEmbeddingJson(EmbeddingUtils.toJson(embedding));
                repository.save(doc);
            });
        }
    }

    public void addOrUpdateDocument(String content, String filePath, String sourceName) {
        repository.deleteByFilePath(filePath);

        List<String> chunks = TextChunker.chunkText(content, 1200);
        for (String chunk : chunks) {
            embeddingService.embedText(chunk).subscribe(embedding -> {
                Document doc = new Document();
                doc.setSource(sourceName);
                doc.setFilePath(filePath);
                doc.setContent(chunk);
                doc.setEmbeddingJson(EmbeddingUtils.toJson(embedding));
                repository.save(doc);
            });
        }
    }
}
