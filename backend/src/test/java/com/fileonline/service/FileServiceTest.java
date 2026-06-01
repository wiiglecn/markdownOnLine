package com.fileonline.service;

import com.fileonline.controller.dto.file.CreateFileRequest;
import com.fileonline.controller.dto.file.FileResponse;
import com.fileonline.controller.dto.file.UpdateFileRequest;
import com.fileonline.exception.ResourceNotFoundException;
import com.fileonline.model.entity.FileEntity;
import com.fileonline.model.enums.ContentType;
import com.fileonline.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private CreateFileRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateFileRequest();
        createRequest.setName("test.md");
        createRequest.setContentType(ContentType.MARKDOWN);
    }

    @Test
    void createFile_shouldCreateAndReturnFileResponse() {
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(invocation -> {
            FileEntity file = invocation.getArgument(0);
            file.setId(1L);
            return file;
        });

        FileResponse response = fileService.createFile(1L, createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test.md", response.getName());
        assertEquals(ContentType.MARKDOWN, response.getContentType());
        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    void getFile_shouldReturnFile_forOwner() {
        FileEntity file = new FileEntity();
        file.setId(1L);
        file.setName("test.md");
        file.setOwnerId(1L);
        file.setContentType(ContentType.MARKDOWN);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        FileResponse response = fileService.getFile(1L, 1L);

        assertNotNull(response);
        assertEquals("test.md", response.getName());
    }

    @Test
    void getFile_shouldThrowException_forNonOwner() {
        FileEntity file = new FileEntity();
        file.setId(1L);
        file.setOwnerId(2L);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        assertThrows(RuntimeException.class, () -> fileService.getFile(1L, 1L));
    }

    @Test
    void renameFile_shouldUpdateName() {
        FileEntity file = new FileEntity();
        file.setId(1L);
        file.setName("old.md");
        file.setOwnerId(1L);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateFileRequest request = new UpdateFileRequest();
        request.setName("new.md");

        FileResponse response = fileService.renameFile(1L, 1L, request);

        assertEquals("new.md", response.getName());
    }

    @Test
    void deleteFile_shouldDelete_forOwner() {
        FileEntity file = new FileEntity();
        file.setId(1L);
        file.setOwnerId(1L);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        fileService.deleteFile(1L, 1L);

        verify(fileRepository).deleteById(1L);
    }
}
