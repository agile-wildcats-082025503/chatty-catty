package com.agilewildcats.chattyCatty.repo;

import com.agilewildcats.chattyCatty.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    void deleteByFilePath(String filePath);
}
