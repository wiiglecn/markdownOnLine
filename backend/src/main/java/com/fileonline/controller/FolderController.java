package com.fileonline.controller;

import com.fileonline.controller.dto.file.CreateFolderRequest;
import com.fileonline.controller.dto.file.FolderResponse;
import com.fileonline.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        FolderResponse response = folderService.createFolder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FolderResponse>> listFolders(
            @RequestParam(required = false) Long parentId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<FolderResponse> folders = folderService.listFolders(userId, parentId);
        return ResponseEntity.ok(folders);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        folderService.deleteFolder(userId, id);
        return ResponseEntity.noContent().build();
    }
}
