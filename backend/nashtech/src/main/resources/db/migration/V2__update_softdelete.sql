ALTER TABLE accounts
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE users
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
