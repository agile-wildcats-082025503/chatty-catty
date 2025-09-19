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

    private String source;

    private String filePath;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String embeddingJson;
}
