package com.example.collaborative_code_editor.repository;

import com.example.collaborative_code_editor.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Long> {
    List<Node> findByRepoId(Long repo_id);
}
