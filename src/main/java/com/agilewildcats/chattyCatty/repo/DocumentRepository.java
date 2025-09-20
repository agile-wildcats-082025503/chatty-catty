package com.agilewildcats.chattyCatty.repo;

import com.agilewildcats.chattyCatty.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "SELECT * FROM documents ORDER BY embedding <-> ?1 LIMIT 3", nativeQuery = true)
    List<Document> findRelevant(float[] queryEmbedding);

    @Query(value = """
        SELECT d.*, (d.embedding <-> CAST(:embedding AS vector)) AS distance
        FROM documents d
        ORDER BY d.embedding <-> CAST(:embedding AS vector)
        LIMIT :k
        """, nativeQuery = true)
    List<Object[]> findTopKWithScores(String embedding, int k);

    void deleteByFilePath(String filePath);
}
