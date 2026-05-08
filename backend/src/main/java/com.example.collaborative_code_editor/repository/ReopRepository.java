package com.example.collaborative_code_editor.repository;

import com.example.collaborative_code_editor.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReopRepository extends JpaRepository<Repo, Long> {
    List<Repo> findByOwnerId(Long ownerId);
}
