package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.entity.Repo;
import com.example.collaborative_code_editor.exception.ForbiddenException;
import com.example.collaborative_code_editor.exception.ResourceNotFoundException;
import com.example.collaborative_code_editor.entity.Document;
import com.example.collaborative_code_editor.entity.DocumentVersion;
import com.example.collaborative_code_editor.entity.User;
import com.example.collaborative_code_editor.repository.DocumentRepository;
import com.example.collaborative_code_editor.repository.DocumentVersionRepository;
import com.example.collaborative_code_editor.repository.ReopRepository;
import com.example.collaborative_code_editor.repository.RepoMemberRepository;
import com.example.collaborative_code_editor.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentVersionRepository VerRepo;
    private final DocumentRepository DocRepo;
    private final ReopRepository ProRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RepoMemberRepository memberRepo;

    public DocumentService(ReopRepository ProRepo,
                           DocumentRepository DocRepo,
                           DocumentVersionRepository VerRepo) {
        this.ProRepo = ProRepo;
        this.DocRepo = DocRepo;
        this.VerRepo = VerRepo;
    }

    private Repo getRepoWithAccess(Long repoId, Long userId) {

        Repo repo = ProRepo.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        // owner
        if (repo.getOwner().getId().equals(userId)) {
            return repo;
        }

        // collaborator
        if (memberRepo.existsByRepoIdAndUserId(repoId, userId)) {
            return repo;
        }

        throw new ForbiddenException("You cannot access this repo");
    }

    public List<Document> getDocuments(Long repoId, Long userId) {

        getRepoWithAccess(repoId, userId);

        return DocRepo.findByRepoId(repoId);
    }

    public Document createDocument(Long repoId, Long userId, String name, String language) {

        Repo repo = ProRepo.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        if (!repo.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only the repo owner can create documents");
        }

        Document doc = new Document();
        doc.setContent("");
        doc.setName(name);
        doc.setRepo(repo);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLanguage(language);
        return DocRepo.save(doc);
    }

    public Document updateDocument(Long repoId,
                                   Long documentId,
                                   Long userId,
                                   String content) {

        getRepoWithAccess(repoId, userId);

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getRepo().getId().equals(repoId)) {
            throw new ForbiddenException("Document does not belong to this repo");
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

    public void deleteDocument(Long repoId, Long documentId, Long userId) {

        Repo repo = ProRepo.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        if (!repo.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can delete documents");
        }

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getRepo().getId().equals(repoId)) {
            throw new ForbiddenException("Document does not belong to this repo");
        }

        DocRepo.delete(document);
    }

    public List<DocumentVersion> getDocumentVersions(Long repoId,
                                                     Long documentId,
                                                     Long userId) {

        getRepoWithAccess(repoId, userId);

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getRepo().getId().equals(repoId)) {
            throw new ForbiddenException("Document does not belong to this repo");
        }

        return VerRepo.findByDocumentIdOrderByCreatedAtDesc(documentId);
    }

    public Document restoreVersion(Long repoId,
                                   Long documentId,
                                   Long versionId,
                                   Long userId) {

        Repo repo = ProRepo.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repo not found"));

        if (!repo.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can restore versions");
        }

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getRepo().getId().equals(repoId)) {
            throw new ForbiddenException("Document does not belong to this repo");
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

    public Document getDocument(Long repoId, Long documentId, Long userId) {

        getRepoWithAccess(repoId, userId);

        Document document = DocRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getRepo().getId().equals(repoId)) {
            throw new ForbiddenException("Document does not belong to this repo");
        }

        return document;
    }
}