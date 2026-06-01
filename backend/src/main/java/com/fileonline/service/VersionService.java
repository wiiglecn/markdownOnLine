package com.fileonline.service;

import com.fileonline.controller.dto.version.VersionResponse;
import com.fileonline.exception.ResourceNotFoundException;
import com.fileonline.model.entity.DocumentSnapshot;
import com.fileonline.model.entity.DocumentVersion;
import com.fileonline.model.entity.User;
import com.fileonline.repository.DocumentSnapshotRepository;
import com.fileonline.repository.DocumentVersionRepository;
import com.fileonline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VersionService {

    private final DocumentVersionRepository versionRepository;
    private final DocumentSnapshotRepository snapshotRepository;
    private final UserRepository userRepository;

    @Transactional
    public VersionResponse createVersion(Long fileId, Long userId) {
        DocumentSnapshot snapshot = snapshotRepository.findByFileId(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("No snapshot found for this file"));

        // Get next version number
        int nextVersion = versionRepository.findTopByFileIdOrderByVersionNumberDesc(fileId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        DocumentVersion version = new DocumentVersion();
        version.setFileId(fileId);
        version.setSnapshotData(snapshot.getSnapshotData());
        version.setVersionNumber(nextVersion);
        version.setCreatedBy(userId);
        versionRepository.save(version);

        User user = userRepository.findById(userId).orElse(null);
        String nickname = user != null ? user.getNickname() : "Unknown";

        return new VersionResponse(version.getId(), fileId, nextVersion, nickname, version.getCreatedAt());
    }

    public List<VersionResponse> listVersions(Long fileId) {
        List<DocumentVersion> versions = versionRepository.findByFileIdOrderByVersionNumberDesc(fileId);
        return versions.stream().map(v -> {
            User user = userRepository.findById(v.getCreatedBy()).orElse(null);
            String nickname = user != null ? user.getNickname() : "Unknown";
            return new VersionResponse(v.getId(), v.getFileId(), v.getVersionNumber(), nickname, v.getCreatedAt());
        }).toList();
    }

    @Transactional
    public VersionResponse restoreVersion(Long fileId, Long versionId, Long userId) {
        DocumentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version not found"));

        if (!version.getFileId().equals(fileId)) {
            throw new RuntimeException("Version does not belong to this file");
        }

        // Update current snapshot
        DocumentSnapshot snapshot = snapshotRepository.findByFileId(fileId)
                .orElse(new DocumentSnapshot());
        snapshot.setFileId(fileId);
        snapshot.setSnapshotData(version.getSnapshotData());
        snapshotRepository.save(snapshot);

        // Create a new version entry for the restore
        return createVersion(fileId, userId);
    }

    public DocumentVersion getVersionData(Long versionId) {
        return versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version not found"));
    }
}
