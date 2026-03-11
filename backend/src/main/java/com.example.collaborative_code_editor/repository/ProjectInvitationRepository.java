package com.example.collaborative_code_editor.repository;

import com.example.collaborative_code_editor.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, Long> {
    List<ProjectInvitation> findByUserIdAndStatus(Long userId, String status);
}
