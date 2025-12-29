package com.example.collaborative_code_editor.controller;

import com.example.collaborative_code_editor.DTO.CreateProjectRequest;
import com.example.collaborative_code_editor.model.Project;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import com.example.collaborative_code_editor.service.ProjectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService service;
    private final ProjectRepository repo;
    public ProjectController(ProjectRepository repo, ProjectService service) {
        this.repo = repo;
        this.service = service;
    }

    @PostMapping
    public void createProject(@RequestBody CreateProjectRequest request) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        service.createProject(request.getName(), userId);
    }

    @GetMapping
    public List<Project> getMyProject() {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return service.getMyProjects(userId);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.DeleteProject(projectId, userId);
    }


}
