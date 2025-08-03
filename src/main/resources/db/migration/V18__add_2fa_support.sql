ALTER TABLE t_user
  ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
  ADD COLUMN two_factor_secret VARCHAR(255),
  ADD COLUMN recovery_codes TEXT,
  ADD COLUMN last_2fa_timestamp TIMESTAMP;

CREATE TABLE t_2fa_backup_codes (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL,
                                  code VARCHAR(10) NOT NULL,
                                  used BOOLEAN DEFAULT FALSE,
                                  used_at TIMESTAMP,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_2fa_backup_user
                                    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
                                  CONSTRAINT uk_2fa_backup_code
                                    UNIQUE (user_id, code)
);

CREATE INDEX idx_2fa_backup_user ON t_2fa_backup_codes(user_id);
