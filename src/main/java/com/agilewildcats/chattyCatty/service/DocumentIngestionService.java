package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.model.Document;
import com.agilewildcats.chattyCatty.repo.DocumentRepository;
import com.agilewildcats.chattyCatty.util.EmbeddingUtils;
import com.agilewildcats.chattyCatty.util.TextChunker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ingests uploaded documents
 */
@Service
public class DocumentIngestionService {

    private final EmbeddingService embeddingService;
    private final DocumentRepository repository;
    private final static Logger logger = LoggerFactory.getLogger(DocumentIngestionService.class);

    public DocumentIngestionService(EmbeddingService embeddingService, DocumentRepository repository) {
        this.embeddingService = embeddingService;
        this.repository = repository;
    }

    public void addDocument(String content, String sourceName) {
        List<String> chunks;
        // Debug to just send the entire text
        // Issue: sending a flood of requests to embeddingService causes a flood of exceptions at the moment
        if (true) {
            chunks = Arrays.asList(content, "");
        } else {
            chunks = TextChunker.chunkText(content, 1200);
        }

        logger.debug("addDocument : content={}, sourceName={}, chunks.size()={}",
                content, sourceName, chunks.size());

        for (String chunk : chunks) {
            float[] embedding = embeddingService.embedText(chunk).block();
            Document doc = new Document();
            doc.setSource(sourceName);
            doc.setContent(chunk);
            doc.setEmbeddingJson(EmbeddingUtils.toJson(embedding));
            repository.save(doc);
            // Debugging loop break to prevent overloading embeddingService with exceptions
            break;
        }
    }

    public void addOrUpdateDocument(String content, String filePath, String sourceName) {
        repository.deleteByFilePath(filePath);

        List<String> chunks;
        // Debug to just send the entire text
        // Issue: sending a flood of requests to embeddingService causes a flood of exceptions at the moment
        if (true) {
            chunks = Arrays.asList(content, "");
        } else {
            chunks = TextChunker.chunkText(content, 1200);
        }

        logger.debug("addOrUpdateDocument : content={}, filePath={}, sourceName={}, chunks.size()={}",
                content, filePath, sourceName, chunks.size());

        for (String chunk : chunks) {
            float[] embedding = embeddingService.embedText(chunk).block();
            Document doc = new Document();
            doc.setSource(sourceName);
            doc.setFilePath(filePath);
            doc.setContent(chunk);
            doc.setEmbeddingJson(EmbeddingUtils.toJson(embedding));
            repository.save(doc);
        }
    }
}
