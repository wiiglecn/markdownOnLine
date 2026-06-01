package com.fileonline.repository;

import com.fileonline.model.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    List<DocumentVersion> findByFileIdOrderByVersionNumberDesc(Long fileId);
    Optional<DocumentVersion> findByFileIdAndVersionNumber(Long fileId, Integer versionNumber);
    Optional<DocumentVersion> findTopByFileIdOrderByVersionNumberDesc(Long fileId);
}
