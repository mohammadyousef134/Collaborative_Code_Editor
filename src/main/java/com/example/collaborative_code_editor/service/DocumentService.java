package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.model.Document;
import com.example.collaborative_code_editor.model.DocumentVersion;
import com.example.collaborative_code_editor.model.Project;
import com.example.collaborative_code_editor.repository.DocumentRepository;
import com.example.collaborative_code_editor.repository.DocumentVersionRepository;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {
    private final DocumentVersionRepository VerRepo;
    private final DocumentRepository DocRepo;
    private final ProjectRepository ProRepo;
    public DocumentService(ProjectRepository ProRepo,
                           DocumentRepository DocRepo,
                           DocumentVersionRepository VerRepo) {
        this.ProRepo = ProRepo;
        this.DocRepo = DocRepo;
        this.VerRepo = VerRepo;
    }

    public List<Document> getDocuments(Long projectId, Long userId) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to view the documents of this project");
        }
        return DocRepo.findByProjectId(projectId);
    }

    public Document createDocument(Long projectId, Long userId, String name) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to add document to this project");
        }

        Document doc = new Document();
        doc.setContent("");
        doc.setName(name);
        doc.setProject(project);
        return DocRepo.save(doc);
    }

    public Document updateDocument(Long projectId,
                                   Long documentId,
                                   Long userId,
                                   String content) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to update this document");
        }
        Document document = DocRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!document.getProject().getId().equals(projectId)) {
            System.out.println("Doc id " + documentId + " document.getId " + document.getId());
            throw new ForbiddenException("Document does not belong to this project");
        }
        DocumentVersion version = new DocumentVersion();
        version.setContent(document.getContent());
        version.setDocument(document);
        version.setCreatedBy(userId);
        version.setCreatedAt(LocalDateTime.now());

        VerRepo.save(version);

        document.setContent(content);
        return DocRepo.save(document);
    }

    public void deleteDocument(Long projectId, Long documentId, Long userId) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Document document = DocRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to delete this document");
        }

        DocRepo.delete(document);
    }

    // get versions
    public List<DocumentVersion> getDocumentVersions(Long projectId,
                                                     Long documentId,
                                                     Long userId) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Document document = DocRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to see the versions of this document");
        }
        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        return VerRepo.findByDocumentIdOrderByCreatedAtDesc(documentId);
    }

    // restore
    public Document restoreVersion(Long projectId,
                                   Long documentId,
                                   Long versionId,
                                   Long userId) {
        System.out.println("service");
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to restore this document");
        }
        Document document = DocRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document does not belong to this project");
        }

        DocumentVersion version = VerRepo.findById(versionId).orElseThrow(() -> new ResourceNotFoundException("Versoin not found"));
        if (!version.getDocument().getId().equals(documentId)) {
            throw new ForbiddenException("Version does not belong to this document");
        }

        DocumentVersion newVersion = new DocumentVersion();
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setContent(document.getContent());
        newVersion.setCreatedBy(userId);
        newVersion.setDocument(document);

        VerRepo.save(newVersion);

        document.setContent(version.getContent());
        return DocRepo.save(document);

    }



}
