package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.DTO.InvitationResponse;
import com.example.collaborative_code_editor.entity.ProjectInvitation;
import com.example.collaborative_code_editor.entity.ProjectMember;
import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.entity.Project;
import com.example.collaborative_code_editor.entity.User;
import com.example.collaborative_code_editor.repository.ProjectInvitationRepository;
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
    @Autowired
    private ProjectInvitationRepository invitationRepo;

    private Project getProjectWithAccess(Long projectId, Long userId) {

        Project project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // owner allowed
        if (project.getOwner().getId().equals(userId)) {
            return project;
        }

        // member allowed
        if (memberRepo.existsByProjectIdAndUserId(projectId, userId)) {
            return project;
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
        getProjectWithAccess(projectId, userId);
        repo.delete(project);
    }

    public void inviteUser(Long projectId, Long userId, String email) {
        Project project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can invite users");
        }
        if(memberRepo.existsByProjectIdAndUserId(projectId, user.getId())){
            throw new RuntimeException("User already member");
        }

        ProjectInvitation invitation = new ProjectInvitation();
        invitation.setProject(project);
        invitation.setStatus("PENDING");
        invitation.setUser(user);

        invitationRepo.save(invitation);
    }

    public void acceptInvitation(Long invitationId, Long userId) {

        ProjectInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invitation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This invitation is not for you");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(invitation.getProject());
        member.setUser(invitation.getUser());

        memberRepo.save(member);

        invitation.setStatus("ACCEPTED");
        invitationRepo.save(invitation);
    }

    public void declineInvitation(Long invitationId, Long userId) {

        ProjectInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invitation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This invitation is not for you");
        }

        invitation.setStatus("DECLINED");

        invitationRepo.save(invitation);
    }

    public List<InvitationResponse> getMyInvitations(Long userId) {

        List<ProjectInvitation> invitations =
                invitationRepo.findByUserIdAndStatus(userId, "PENDING");

        return invitations.stream()
                .map(inv -> new InvitationResponse(
                        inv.getId(),
                        inv.getProject().getId(),
                        inv.getProject().getName(),
                        inv.getProject().getOwner().getEmail()
                ))
                .toList();
    }
}
