package com.fileonline.service;

import com.fileonline.controller.dto.file.CreateFileRequest;
import com.fileonline.controller.dto.file.FileResponse;
import com.fileonline.controller.dto.file.UpdateFileRequest;
import com.fileonline.exception.ResourceNotFoundException;
import com.fileonline.model.entity.FileEntity;
import com.fileonline.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public FileResponse createFile(Long userId, CreateFileRequest request) {
        FileEntity file = new FileEntity();
        file.setName(request.getName());
        file.setContentType(request.getContentType());
        file.setOwnerId(userId);
        file.setFolderId(request.getFolderId());
        fileRepository.save(file);

        return toResponse(file);
    }

    public List<FileResponse> listFiles(Long userId, Long folderId) {
        List<FileEntity> files;
        if (folderId == null) {
            files = fileRepository.findByOwnerIdAndFolderIdIsNullOrderByUpdatedAtDesc(userId);
        } else {
            files = fileRepository.findByOwnerIdAndFolderIdOrderByUpdatedAtDesc(userId, folderId);
        }
        return files.stream().map(this::toResponse).toList();
    }

    public FileResponse getFile(Long userId, Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if (!file.getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return toResponse(file);
    }

    @Transactional
    public FileResponse renameFile(Long userId, Long fileId, UpdateFileRequest request) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if (!file.getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (request.getName() != null) {
            file.setName(request.getName());
        }
        fileRepository.save(file);

        return toResponse(file);
    }

    @Transactional
    public void deleteFile(Long userId, Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if (!file.getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        fileRepository.deleteById(fileId);
    }

    private FileResponse toResponse(FileEntity file) {
        return new FileResponse(
                file.getId(),
                file.getName(),
                file.getContentType(),
                file.getFolderId(),
                file.getCreatedAt(),
                file.getUpdatedAt()
        );
    }
}
