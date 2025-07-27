CREATE TABLE t_notification_settings (
                                       id BIGSERIAL PRIMARY KEY,
                                       user_id BIGINT NOT NULL UNIQUE,
                                       budget_alerts BOOLEAN NOT NULL DEFAULT TRUE,
                                       budget_alert_threshold INTEGER NOT NULL DEFAULT 80,
                                       recurring_reminders BOOLEAN NOT NULL DEFAULT TRUE,
                                       monthly_report BOOLEAN NOT NULL DEFAULT TRUE,
                                       email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_notification_settings_user
                                         FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE t_email_quota (
                             id BIGSERIAL PRIMARY KEY,
                             date DATE NOT NULL UNIQUE,
                             daily_count INTEGER NOT NULL DEFAULT 0,
                             monthly_count INTEGER NOT NULL DEFAULT 0,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_email_history (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               recipient VARCHAR(255) NOT NULL,
                               subject VARCHAR(255) NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               status VARCHAR(50) NOT NULL,
                               error_message TEXT,
                               sent_at TIMESTAMP,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_email_history_user
                                 FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_settings_user ON t_notification_settings(user_id);
CREATE INDEX idx_email_quota_date ON t_email_quota(date);
CREATE INDEX idx_email_history_user ON t_email_history(user_id);
CREATE INDEX idx_email_history_created ON t_email_history(created_at);

INSERT INTO t_notification_settings (user_id)
SELECT id FROM t_user
WHERE NOT EXISTS (
  SELECT 1 FROM t_notification_settings ns WHERE ns.user_id = t_user.id
  );
