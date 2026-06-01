package com.fileonline.controller.dto.version;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VersionResponse {
    private Long id;
    private Long fileId;
    private Integer versionNumber;
    private String createdBy;
    private LocalDateTime createdAt;
}
