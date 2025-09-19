package com.agilewildcats.chattyCatty.repo;

import com.agilewildcats.chattyCatty.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "SELECT * FROM documents ORDER BY embedding <-> ?1 LIMIT 3", nativeQuery = true)
    List<Document> findRelevant(float[] queryEmbedding);

    void deleteByFilePath(String filePath);
}
