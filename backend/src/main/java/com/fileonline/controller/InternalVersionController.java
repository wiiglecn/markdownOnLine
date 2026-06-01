package com.fileonline.controller;

import com.fileonline.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/docs")
@RequiredArgsConstructor
public class InternalVersionController {

    private final VersionService versionService;

    @PostMapping("/{fileId}/versions")
    public ResponseEntity<Map<String, String>> createVersion(
            @PathVariable Long fileId,
            @RequestBody Map<String, Long> body) {
        Long userId = body.getOrDefault("userId", 1L);
        versionService.createVersion(fileId, userId);
        return ResponseEntity.ok(Map.of("status", "created"));
    }
}
