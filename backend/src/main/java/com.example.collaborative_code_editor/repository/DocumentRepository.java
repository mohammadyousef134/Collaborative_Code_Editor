package com.example.collaborative_code_editor.repository;

import com.example.collaborative_code_editor.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByRepoId(Long project_id);
}
