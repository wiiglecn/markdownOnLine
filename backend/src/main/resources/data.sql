-- ============================================================
-- File Online - Test Data Script
-- Creates default test users for development
-- ============================================================

USE file_online;

-- Test User 1: Admin
-- Email: admin@fileonline.com
-- Password: admin123
INSERT INTO users (email, password_hash, nickname, created_at)
VALUES (
    'admin@fileonline.com',
    '$2b$10$Yhbfqc.9JUKY3x9ULNfPxuXBPE2kBfKwR5ds3F5v3JuFmlUVSxR/e',
    'Admin',
    NOW()
)
ON DUPLICATE KEY UPDATE email = email;

-- Test User 2: Regular User
-- Email: user@fileonline.com
-- Password: user123
INSERT INTO users (email, password_hash, nickname, created_at)
VALUES (
    'user@fileonline.com',
    '$2b$10$pY36CKg/W4euSjvs5rRaq.a5/A.GL5058mfzfegtlk5IyuhbRVby6',
    'TestUser',
    NOW()
)
ON DUPLICATE KEY UPDATE email = email;
