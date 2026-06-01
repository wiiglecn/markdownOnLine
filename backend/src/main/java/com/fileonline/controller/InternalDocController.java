package com.fileonline.controller;

import com.fileonline.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/internal/docs")
@RequiredArgsConstructor
public class InternalDocController {

    private final DocumentService documentService;

    @GetMapping("/{fileId}/snapshot")
    public ResponseEntity<byte[]> getSnapshot(@PathVariable Long fileId) {
        byte[] data = documentService.getSnapshot(fileId);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(data);
    }

    @PostMapping("/{fileId}/snapshot")
    public ResponseEntity<Void> saveSnapshot(
            @PathVariable Long fileId,
            InputStream inputStream) throws IOException {
        byte[] data = inputStream.readAllBytes();
        log.info("Saving snapshot for file {}: {} bytes", fileId, data.length);
        documentService.saveSnapshot(fileId, data);
        return ResponseEntity.ok().build();
    }
}
