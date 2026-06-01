package com.fileonline.repository;

import com.fileonline.model.entity.DocumentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentSnapshotRepository extends JpaRepository<DocumentSnapshot, Long> {
    Optional<DocumentSnapshot> findByFileId(Long fileId);
}
