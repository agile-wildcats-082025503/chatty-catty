package com.agilewildcats.chattyCatty.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;   // filename (e.g., "intro_to_computing.pdf")

    private String filePath; // absolute/relative path, unique per file

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String embeddingJson;
}
