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
@RequestMapping("/projects/{projectId}/documents")
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
    public List<Document> getDocuments(@PathVariable Long projectId) {
        Long userId = getUserId();
        return service.getDocuments(projectId, userId);
    }

    @PostMapping
    public Document createNewDocument(@PathVariable Long projectId, @RequestBody CreateDocumentRequest request) {
        Long userId = getUserId();
        return service.createDocument(projectId, userId, request.getName(), request.getLanguage());
    }

    @PutMapping("/{documentId}")
    public Document updateDocument(@PathVariable Long projectId, @PathVariable Long documentId, @RequestBody UpdateDocumentRequest request) {
        Long userId = getUserId();
        return service.updateDocument(projectId, documentId, userId, request.getContent());
    }

    @DeleteMapping("/{documentId}")
    public void deleteDocument(@PathVariable Long projectId, @PathVariable Long documentId) {
        Long userId = getUserId();

        service.deleteDocument(projectId, documentId, userId);
    }

    @GetMapping("/{documentId}/versions")
    public List<DocumentVersion> getDocumentVersions(@PathVariable Long projectId
            , @PathVariable Long documentId) {
        Long userId = getUserId();
        return service.getDocumentVersions(projectId, documentId, userId);
    }

    @PostMapping("/{documentId}/versions/{versionId}/restore")
    public Document restoreVersion(
            @PathVariable Long projectId,
            @PathVariable Long documentId,
            @PathVariable Long versionId
    ) {
        Long userId = getUserId();

        return service.restoreVersion(projectId, documentId, versionId, userId);
    }

    @GetMapping("/{documentId}")
    public Document getDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId
    ) {
        Long userId = getUserId();
        return service.getDocument(projectId, documentId, userId);
    }

}
