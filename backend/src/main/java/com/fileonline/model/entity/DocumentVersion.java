package com.fileonline.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_versions", indexes = {
    @Index(name = "idx_doc_ver_file_version", columnList = "file_id, version_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Lob
    @Column(name = "snapshot_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] snapshotData;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
