package com.fileonline.controller.dto.file;

import com.fileonline.model.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFileRequest {
    @NotBlank(message = "File name is required")
    private String name;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    private Long folderId;
}
