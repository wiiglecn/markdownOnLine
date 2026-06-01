-- ============================================================
-- File Online - Database Schema Script
-- Database: MySQL 8.0
-- ============================================================

-- Create database
CREATE DATABASE IF NOT EXISTS file_online
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE file_online;

-- ============================================================
-- 1. users - User accounts table
-- ============================================================
DROP TABLE IF EXISTS document_versions;
DROP TABLE IF EXISTS document_snapshots;
DROP TABLE IF EXISTS files;
DROP TABLE IF EXISTS folders;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname      VARCHAR(255) DEFAULT NULL,
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 2. folders - Folder / directory table
-- ============================================================
CREATE TABLE folders (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    parent_id  BIGINT       DEFAULT NULL,
    owner_id   BIGINT       NOT NULL,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_folders_name_owner_parent (name, owner_id, parent_id),
    KEY idx_folders_owner (owner_id),
    KEY idx_folders_parent (parent_id),
    CONSTRAINT fk_folders_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_folders_parent FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3. files - File metadata table
-- ============================================================
CREATE TABLE files (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    content_type VARCHAR(20)  NOT NULL COMMENT 'TEXT or MARKDOWN',
    owner_id     BIGINT       NOT NULL,
    folder_id    BIGINT       DEFAULT NULL,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_files_owner_folder (owner_id, folder_id),
    KEY idx_files_folder (folder_id),
    CONSTRAINT fk_files_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_files_folder FOREIGN KEY (folder_id) REFERENCES folders (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4. document_snapshots - Latest document state (Yjs binary)
-- ============================================================
CREATE TABLE document_snapshots (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    file_id       BIGINT   NOT NULL,
    snapshot_data LONGBLOB NOT NULL,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_doc_snapshots_file (file_id),
    CONSTRAINT fk_doc_snapshots_file FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5. document_versions - Version history table
-- ============================================================
CREATE TABLE document_versions (
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    file_id        BIGINT   NOT NULL,
    snapshot_data  LONGBLOB NOT NULL,
    version_number INT      NOT NULL,
    created_by     BIGINT   NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_doc_ver_file_version (file_id, version_number),
    CONSTRAINT fk_doc_ver_file FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_ver_creator FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Done. 5 tables created.
-- ============================================================
