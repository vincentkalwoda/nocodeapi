ALTER TABLE users DROP COLUMN IF EXISTS session_token;

ALTER TABLE users ADD COLUMN refresh_token TEXT;
ALTER TABLE users ADD COLUMN refresh_token_expires_at TIMESTAMP;