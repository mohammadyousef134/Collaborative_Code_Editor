package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.model.Project;
import com.example.collaborative_code_editor.model.User;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import com.example.collaborative_code_editor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    ProjectRepository repo;

    @Autowired
    private UserRepository userRepo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public void createProject(String name, Long userId) {
        Project project = new Project();
        User user = userRepo.findById(userId).orElseThrow(
                () -> new RuntimeException(new ResourceNotFoundException(""))
        );
        project.setOwner(user);
        project.setName(name);
        repo.save(project);
    }

    public List<Project> getMyProjects(Long userId) {
        return repo.findByOwnerId(userId);
    }

    public void DeleteProject(Long projectId, Long userId) {
        Project project = repo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwner().getId().equals(userId)) {

            throw new ForbiddenException("You are not allowed to delete this project");
        }
        repo.delete(project);
    }
}
