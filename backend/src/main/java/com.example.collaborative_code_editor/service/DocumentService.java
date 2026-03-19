package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.entity.Document;
import com.example.collaborative_code_editor.entity.DocumentVersion;
import com.example.collaborative_code_editor.entity.Project;
import com.example.collaborative_code_editor.entity.User;
import com.example.collaborative_code_editor.repository.DocumentRepository;
import com.example.collaborative_code_editor.repository.DocumentVersionRepository;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import com.example.collaborative_code_editor.repository.ProjectMemberRepository;
import com.example.collaborative_code_editor.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentVersionRepository VerRepo;
    private final DocumentRepository DocRepo;
    private final ProjectRepository ProRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectMemberRepository memberRepo;

    public DocumentService(ProjectRepository ProRepo,
                           DocumentRepository DocRepo,
                           DocumentVersionRepository VerRepo) {
        this.ProRepo = ProRepo;
        this.DocRepo = DocRepo;
        this.VerRepo = VerRepo;
    }

    private Project getProjectWithAccess(Long projectId, Long userId) {

        Project project = ProRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // owner
        if (project.getOwner().getId().equals(userId)) {
            return project;
        }

        // collaborator
        if (memberRepo.existsByProjectIdAndUserId(projectId, userId)) {
            return project;
        }

        throw new ForbiddenException("You cannot access this project");
    }

    public List<Document> getDocuments(Long projectId, Long userId) {

        getProjectWithAccess(projectId, userId);

        return DocRepo.findByProjectId(projectId);
    }

    public Document createDocument(Long projectId, Long userId, String name, String language) {

        Project project = ProRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only the project owner can create documents");
        }

        Document doc = new Document();
        doc.setContent("");
        doc.setName(name);
        doc.setProject(project);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLanguage(language);
        return DocRepo.save(doc);
    }

    public Document updateDocument(Long projectId,
                                   Long documentId,
                                   Long userId,
                                   String content) {

        getProjectWithAccess(projectId, userId);

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        if (!document.getContent().equals(content)) {

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            DocumentVersion version = new DocumentVersion();
            version.setContent(document.getContent());
            version.setDocument(document);
            version.setCreatedBy(user);
            version.setCreatedAt(LocalDateTime.now());

            VerRepo.save(version);

            document.setContent(content);
        }

        return DocRepo.save(document);
    }

    public void deleteDocument(Long projectId, Long documentId, Long userId) {

        Project project = ProRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can delete documents");
        }

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        DocRepo.delete(document);
    }

    public List<DocumentVersion> getDocumentVersions(Long projectId,
                                                     Long documentId,
                                                     Long userId) {

        getProjectWithAccess(projectId, userId);

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        return VerRepo.findByDocumentIdOrderByCreatedAtDesc(documentId);
    }

    public Document restoreVersion(Long projectId,
                                   Long documentId,
                                   Long versionId,
                                   Long userId) {

        Project project = ProRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can restore versions");
        }

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        DocumentVersion version = VerRepo.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version not found"));

        if (!version.getDocument().getId().equals(documentId)) {
            throw new ForbiddenException("Version does not belong to this document");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DocumentVersion newVersion = new DocumentVersion();
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setContent(document.getContent());
        newVersion.setCreatedBy(user);
        newVersion.setDocument(document);

        VerRepo.save(newVersion);

        document.setContent(version.getContent());

        return DocRepo.save(document);
    }

    public Document getDocument(Long projectId, Long documentId, Long userId) {

        getProjectWithAccess(projectId, userId);

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        return document;
    }
}