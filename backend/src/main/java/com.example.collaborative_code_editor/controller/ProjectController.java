package com.example.collaborative_code_editor.controller;

import com.example.collaborative_code_editor.DTO.CreateInviteToProjectRequest;
import com.example.collaborative_code_editor.DTO.CreateProjectRequest;
import com.example.collaborative_code_editor.entity.Project;
import com.example.collaborative_code_editor.service.ProjectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public void createProject(@RequestBody CreateProjectRequest request) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        System.out.println(userId);
        service.createProject(request.getName(), userId);
    }

    @GetMapping
    public List<Project> getMyProjects() {
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

    @PostMapping("/{projectId}/invite")
    public void inviteUser(
            @PathVariable Long projectId,
            @RequestBody CreateInviteToProjectRequest request
            ) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.inviteUser(projectId, userId, request.getEmail());
    }
    @PostMapping("/invitations/{invitationId}/accept")
    public void acceptInvitation(@PathVariable Long invitationId) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.acceptInvitation(invitationId, userId);
    }
    @PostMapping("/invitations/{invitationId}/decline")
    public void declineInvitation(@PathVariable Long invitationId) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        service.declineInvitation(invitationId, userId);
    }



}
