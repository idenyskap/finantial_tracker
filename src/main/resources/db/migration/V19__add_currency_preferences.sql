CREATE TABLE t_user_currency_preference (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL,
                                          currency VARCHAR(3) NOT NULL,
                                          display_order INTEGER NOT NULL DEFAULT 0,
                                          is_active BOOLEAN DEFAULT TRUE,
                                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          CONSTRAINT fk_currency_pref_user
                                            FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
                                          CONSTRAINT uk_user_currency
                                            UNIQUE (user_id, currency)
);

CREATE INDEX idx_currency_pref_user ON t_user_currency_preference(user_id);
