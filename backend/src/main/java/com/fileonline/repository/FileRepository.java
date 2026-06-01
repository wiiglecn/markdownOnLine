package com.fileonline.repository;

import com.fileonline.model.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByOwnerIdAndFolderIdOrderByUpdatedAtDesc(Long ownerId, Long folderId);
    List<FileEntity> findByOwnerIdAndFolderIdIsNullOrderByUpdatedAtDesc(Long ownerId);
}
