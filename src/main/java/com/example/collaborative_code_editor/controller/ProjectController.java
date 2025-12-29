package com.example.collaborative_code_editor.controller;

import com.example.collaborative_code_editor.model.Project;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import com.example.collaborative_code_editor.service.ProjectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository repo;
    public ProjectController(ProjectRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public void createProject(@RequestBody Project project) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        project.setOwnerId(userId);
        repo.save(project);
    }
    @GetMapping
    public List<Project> getMyProject() {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return repo.findByOwnerId(userId);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Project prject = repo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!prject.getOwnerId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        repo.delete(prject);
    }


}
