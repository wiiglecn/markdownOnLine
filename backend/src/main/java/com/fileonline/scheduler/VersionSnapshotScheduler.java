package com.fileonline.scheduler;

import com.fileonline.model.entity.DocumentSnapshot;
import com.fileonline.model.entity.DocumentVersion;
import com.fileonline.model.entity.FileEntity;
import com.fileonline.repository.DocumentSnapshotRepository;
import com.fileonline.repository.DocumentVersionRepository;
import com.fileonline.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class VersionSnapshotScheduler {

    private final FileRepository fileRepository;
    private final DocumentSnapshotRepository snapshotRepository;
    private final DocumentVersionRepository versionRepository;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void createPeriodicVersions() {
        log.debug("Running periodic version snapshot scheduler");

        List<FileEntity> files = fileRepository.findAll();
        for (FileEntity file : files) {
            Optional<DocumentSnapshot> snapshotOpt = snapshotRepository.findByFileId(file.getId());
            if (snapshotOpt.isEmpty()) continue;

            Optional<DocumentVersion> lastVersionOpt = versionRepository.findTopByFileIdOrderByVersionNumberDesc(file.getId());

            boolean shouldCreateVersion = false;
            if (lastVersionOpt.isEmpty()) {
                // No versions yet, create one if snapshot exists
                shouldCreateVersion = true;
            } else {
                // Create version if file was modified after last version
                DocumentVersion lastVersion = lastVersionOpt.get();
                if (file.getUpdatedAt() != null && file.getUpdatedAt().isAfter(lastVersion.getCreatedAt())) {
                    shouldCreateVersion = true;
                }
            }

            if (shouldCreateVersion) {
                DocumentSnapshot snapshot = snapshotOpt.get();
                int nextVersion = lastVersionOpt.map(v -> v.getVersionNumber() + 1).orElse(1);

                DocumentVersion version = new DocumentVersion();
                version.setFileId(file.getId());
                version.setSnapshotData(snapshot.getSnapshotData());
                version.setVersionNumber(nextVersion);
                version.setCreatedBy(file.getOwnerId());
                versionRepository.save(version);

                log.info("Created version {} for file {}", nextVersion, file.getId());
            }
        }
    }
}
