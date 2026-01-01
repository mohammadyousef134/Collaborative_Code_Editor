package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.model.Document;
import com.example.collaborative_code_editor.model.Project;
import com.example.collaborative_code_editor.repository.DocumentRepository;
import com.example.collaborative_code_editor.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository DocRepo;
    private final ProjectRepository ProRepo;
    public DocumentService(ProjectRepository ProRepo, DocumentRepository DocRepo) {
        this.ProRepo = ProRepo;
        this.DocRepo = DocRepo;
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

    public Document updateDocument(Long projectId, Long documentId, Long userId, String content) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to update this document");
        }
        Document document = DocRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!document.getProject().getId().equals(projectId)) {
            System.out.println("Doc id " + documentId + " document.getId " + document.getId());
            throw new ForbiddenException("Document dose not belong to this project");
        }

        document.setContent(content);
        return DocRepo.save(document);
    }

    public void deleteDocument(Long projectId, Long documentId, Long userId) {
        Project project = ProRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Document document = DocRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Document dose not belong to this project");
        }

        if (!project.getOwnerId().equals(userId)) {
            throw new ForbiddenException("You arr not allowed to delete this document");
        }

        DocRepo.delete(document);
    }


}
