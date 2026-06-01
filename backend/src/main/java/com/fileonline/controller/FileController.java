package com.fileonline.controller;

import com.fileonline.controller.dto.file.CreateFileRequest;
import com.fileonline.controller.dto.file.FileResponse;
import com.fileonline.controller.dto.file.UpdateFileRequest;
import com.fileonline.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileResponse> createFile(
            @Valid @RequestBody CreateFileRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        FileResponse response = fileService.createFile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> listFiles(
            @RequestParam(required = false) Long folderId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<FileResponse> files = fileService.listFiles(userId, folderId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFile(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        FileResponse response = fileService.getFile(userId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileResponse> updateFile(
            @PathVariable Long id,
            @RequestBody UpdateFileRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        FileResponse response = fileService.renameFile(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        fileService.deleteFile(userId, id);
        return ResponseEntity.noContent().build();
    }
}
