package com.fileonline.repository;

import com.fileonline.model.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwnerIdAndParentIdOrderByCreatedAtDesc(Long ownerId, Long parentId);
    List<Folder> findByOwnerIdAndParentIdIsNullOrderByCreatedAtDesc(Long ownerId);
    boolean existsByOwnerIdAndParentIdAndName(Long ownerId, Long parentId, String name);
}
