package com.fileonline.controller.dto.file;

import com.fileonline.model.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String name;
    private ContentType contentType;
    private Long folderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
