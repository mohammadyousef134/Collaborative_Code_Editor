package com.example.collaborative_code_editor.controller;

import com.example.collaborative_code_editor.DTO.CreateDocumentRequest;
import com.example.collaborative_code_editor.DTO.UpdateDocumentRequest;
import com.example.collaborative_code_editor.model.Document;
import com.example.collaborative_code_editor.model.DocumentVersion;
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

    @GetMapping
    public List<Document> getDocuments(@PathVariable Long projectId) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return service.getDocuments(projectId, userId);
    }

    @PostMapping
    public Document createNewDocument(@PathVariable Long projectId, @RequestBody CreateDocumentRequest request) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return service.createDocument(projectId, userId, request.getName());
    }
    @PutMapping("/{documentId}")
    public Document UpdateDocument(@PathVariable Long projectId, @PathVariable Long documentId, @RequestBody UpdateDocumentRequest request) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return service.updateDocument(projectId, documentId, userId, request.getContent());
    }

    @DeleteMapping("/{documentId}")
    public void deleteDocument(@PathVariable Long projectId, @PathVariable Long documentId) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();


        service.deleteDocument(projectId, documentId, userId);
    }

    @GetMapping ("/{documentId}/versions")
    public List<DocumentVersion> getDocumentVersions(@PathVariable Long projectId
    , @PathVariable Long documentId) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return service.getDocumentVersions(projectId, documentId, userId);
    }

    @PostMapping("/{documentId}/versions/{versionId}/restore")
    public Document restoreVersion(
            @PathVariable Long projectId,
            @PathVariable Long documentId,
            @PathVariable Long versionId
    ) {
        System.out.println("RESTORE VERSION HIT");

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.restoreVersion(projectId, documentId, versionId, userId);
    }

}
