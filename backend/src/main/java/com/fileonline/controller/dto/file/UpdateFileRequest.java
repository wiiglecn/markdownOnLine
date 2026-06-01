package com.fileonline.controller.dto.file;

import lombok.Data;

@Data
public class UpdateFileRequest {
    private String name;
    private Long folderId;
}
