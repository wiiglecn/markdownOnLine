package com.fileonline.service;

import com.fileonline.model.entity.DocumentSnapshot;
import com.fileonline.repository.DocumentSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentSnapshotRepository snapshotRepository;

    @Transactional
    public void saveSnapshot(Long fileId, byte[] data) {
        DocumentSnapshot snapshot = snapshotRepository.findByFileId(fileId)
                .orElse(new DocumentSnapshot());

        snapshot.setFileId(fileId);
        snapshot.setSnapshotData(data);
        snapshotRepository.save(snapshot);
    }

    public byte[] getSnapshot(Long fileId) {
        return snapshotRepository.findByFileId(fileId)
                .map(DocumentSnapshot::getSnapshotData)
                .orElse(null);
    }
}
