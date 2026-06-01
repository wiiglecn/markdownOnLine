package com.fileonline.controller;

import com.fileonline.controller.dto.version.VersionResponse;
import com.fileonline.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files/{fileId}/versions")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @GetMapping
    public ResponseEntity<List<VersionResponse>> listVersions(@PathVariable Long fileId) {
        List<VersionResponse> versions = versionService.listVersions(fileId);
        return ResponseEntity.ok(versions);
    }

    @PostMapping
    public ResponseEntity<VersionResponse> createVersion(
            @PathVariable Long fileId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        VersionResponse response = versionService.createVersion(fileId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{versionId}/restore")
    public ResponseEntity<VersionResponse> restoreVersion(
            @PathVariable Long fileId,
            @PathVariable Long versionId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        VersionResponse response = versionService.restoreVersion(fileId, versionId, userId);
        return ResponseEntity.ok(response);
    }
}
