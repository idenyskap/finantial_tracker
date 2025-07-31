ALTER TABLE t_user
  ADD COLUMN email_verified BOOLEAN DEFAULT false,
  ADD COLUMN verification_token VARCHAR(255),
  ADD COLUMN verification_token_expires_at TIMESTAMP,
  ADD COLUMN reset_password_token VARCHAR(255),
  ADD COLUMN reset_password_token_expires_at TIMESTAMP,
  ADD COLUMN new_email VARCHAR(255),
  ADD COLUMN new_email_token VARCHAR(255),
  ADD COLUMN new_email_token_expires_at TIMESTAMP;

-- Помечаем всех существующих пользователей как верифицированных
UPDATE t_user SET email_verified = true WHERE id > 0;
