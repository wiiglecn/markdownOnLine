package com.fileonline.service;

import com.fileonline.controller.dto.file.CreateFolderRequest;
import com.fileonline.controller.dto.file.FolderResponse;
import com.fileonline.exception.DuplicateResourceException;
import com.fileonline.exception.ResourceNotFoundException;
import com.fileonline.model.entity.Folder;
import com.fileonline.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public FolderResponse createFolder(Long userId, CreateFolderRequest request) {
        if (folderRepository.existsByOwnerIdAndParentIdAndName(userId, request.getParentId(), request.getName())) {
            throw new DuplicateResourceException("Folder with this name already exists");
        }

        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setParentId(request.getParentId());
        folder.setOwnerId(userId);
        folderRepository.save(folder);

        return new FolderResponse(folder.getId(), folder.getName(), folder.getParentId(), folder.getCreatedAt());
    }

    public List<FolderResponse> listFolders(Long userId, Long parentId) {
        List<Folder> folders;
        if (parentId == null) {
            folders = folderRepository.findByOwnerIdAndParentIdIsNullOrderByCreatedAtDesc(userId);
        } else {
            folders = folderRepository.findByOwnerIdAndParentIdOrderByCreatedAtDesc(userId, parentId);
        }
        return folders.stream()
                .map(f -> new FolderResponse(f.getId(), f.getName(), f.getParentId(), f.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void deleteFolder(Long userId, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        if (!folder.getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // Check if folder has sub-folders
        List<Folder> subFolders = folderRepository.findByOwnerIdAndParentIdOrderByCreatedAtDesc(userId, folderId);
        if (!subFolders.isEmpty()) {
            throw new RuntimeException("Cannot delete folder with sub-folders");
        }

        folderRepository.deleteById(folderId);
    }
}
