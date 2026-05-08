package com.example.collaborative_code_editor.repository;

import com.example.collaborative_code_editor.entity.Blob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlobRepository extends JpaRepository<Blob, Long> {
}
