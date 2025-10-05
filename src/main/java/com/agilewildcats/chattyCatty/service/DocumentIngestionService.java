package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.util.TextChunker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Ingests uploaded documents
 */
@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    private final static Logger logger = LoggerFactory.getLogger(DocumentIngestionService.class);

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = new TokenTextSplitter();
    }

    public void addDocument(String content, String filePath, String sourceName) {
        Document docOrig = new Document(content);
        List<Document> documents = Collections.singletonList(docOrig);
        List<Document> chunkedDocs = tokenTextSplitter.apply(documents);

        for (Document doc : chunkedDocs) {
            doc.getMetadata().put("sourceName", sourceName);
            doc.getMetadata().put("filePath", filePath);
            doc.getMetadata().put("publishDate", LocalDate.now());
        }

        // Debugging call to show full context
        logger.info("addDocument : content={}, filePath={}, sourceName={}, chunks.size()={}",
                content, filePath, sourceName, chunkedDocs.size());

        vectorStore.add(chunkedDocs);
    }

    public void deleteDocument(String filePath, String sourceName) {
        vectorStore.delete(new Filter.Expression(
                Filter.ExpressionType.AND,
                new Filter.Expression(
                        Filter.ExpressionType.EQ,
                        new Filter.Key("filePath"),
                        new Filter.Value(filePath)
                ),
                new Filter.Expression(
                        Filter.ExpressionType.EQ,
                        new Filter.Key("sourceName"),
                        new Filter.Value(sourceName)
                )
        ));
    }

    public void addOrUpdateDocument(String content, String filePath, String sourceName) {
        deleteDocument(filePath, sourceName);
        addDocument(content, filePath, sourceName);
    }
}
