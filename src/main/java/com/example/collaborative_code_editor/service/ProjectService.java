package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.model.Project;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    ProjectRepository repo;
    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }
    public void createProject(String name, Long userId) {
        Project project = new Project();
        project.setOwnerId(userId);
        project.setName(name);
        repo.save(project);
    }

    public List<Project> getMyProjects(Long userId) {
        return repo.findByOwnerId(userId);
    }

    public void DeleteProject(Long projectId, Long userId) {
        Project project = repo.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        repo.delete(project);
    }
}
