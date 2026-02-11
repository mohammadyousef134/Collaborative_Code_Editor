package com.example.collaborative_code_editor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long createdBy;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

}
