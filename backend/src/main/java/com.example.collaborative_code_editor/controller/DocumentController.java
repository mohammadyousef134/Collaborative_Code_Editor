package com.example.collaborative_code_editor.controller;

import com.example.collaborative_code_editor.DTO.CreateDocumentRequest;
import com.example.collaborative_code_editor.DTO.UpdateDocumentRequest;
import com.example.collaborative_code_editor.entity.Document;
import com.example.collaborative_code_editor.entity.DocumentVersion;
import com.example.collaborative_code_editor.service.DocumentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repo/{repoId}/documents")
public class DocumentController {
    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    private Long getUserId() {
        return Long.parseLong(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString()
        );
    }

    @GetMapping
    public List<Document> getDocuments(@PathVariable Long repoId) {
        Long userId = getUserId();
        return service.getDocuments(repoId, userId);
    }

    @PostMapping
    public Document createNewDocument(@PathVariable Long repoId, @RequestBody CreateDocumentRequest request) {
        Long userId = getUserId();
        return service.createDocument(repoId, userId, request.getName(), request.getLanguage());
    }

    @PutMapping("/{documentId}")
    public Document updateDocument(@PathVariable Long repoId, @PathVariable Long documentId, @RequestBody UpdateDocumentRequest request) {
        Long userId = getUserId();
        return service.updateDocument(repoId, documentId, userId, request.getContent());
    }

    @DeleteMapping("/{documentId}")
    public void deleteDocument(@PathVariable Long repoId, @PathVariable Long documentId) {
        Long userId = getUserId();

        service.deleteDocument(repoId, documentId, userId);
    }

    @GetMapping("/{documentId}/versions")
    public List<DocumentVersion> getDocumentVersions(@PathVariable Long repoId
            , @PathVariable Long documentId) {
        Long userId = getUserId();
        return service.getDocumentVersions(repoId, documentId, userId);
    }

    @PostMapping("/{documentId}/versions/{versionId}/restore")
    public Document restoreVersion(
            @PathVariable Long repoId,
            @PathVariable Long documentId,
            @PathVariable Long versionId
    ) {
        Long userId = getUserId();

        return service.restoreVersion(repoId, documentId, versionId, userId);
    }

    @GetMapping("/{documentId}")
    public Document getDocument(
            @PathVariable Long repoId,
            @PathVariable Long documentId
    ) {
        Long userId = getUserId();
        return service.getDocument(repoId, documentId, userId);
    }

}
