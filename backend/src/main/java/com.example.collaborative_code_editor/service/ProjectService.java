package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.entity.ProjectMember;
import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.entity.Project;
import com.example.collaborative_code_editor.entity.User;
import com.example.collaborative_code_editor.repository.ProjectMemberRepository;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import com.example.collaborative_code_editor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository repo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ProjectMemberRepository memberRepo;

    private void checkAccess(Project project, Long userId) {

        if (project.getOwner().getId().equals(userId)) {
            return;
        }

        if (memberRepo.existsByProjectIdAndUserId(project.getId(), userId)) {
            return;
        }
        throw new ForbiddenException("You cannot access this project");
    }

    public void createProject(String name, Long userId) {
        Project project = new Project();
        User user = userRepo.findById(userId).orElseThrow(
                () -> new RuntimeException(new ResourceNotFoundException("User not found"))
        );
        project.setOwner(user);
        project.setName(name);
        repo.save(project);
    }

    public List<Project> getMyProjects(Long userId) {
        List<Project> ownedProjects = repo.findByOwnerId(userId);
        List<Project> memberProjects  = memberRepo.findByUserId(userId)
                .stream()
                .map(ProjectMember::getProject)
                .toList();
        ownedProjects.addAll(memberProjects);
        return ownedProjects;

    }

    public void DeleteProject(Long projectId, Long userId) {
        Project project = repo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to delete this project");
        }
        repo.delete(project);
    }
}
