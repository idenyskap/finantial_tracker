CREATE TABLE t_recurring_transaction (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
                                       amount DECIMAL(10,2) NOT NULL,
                                       type VARCHAR(50) NOT NULL,
                                       category_id BIGINT NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       description TEXT,
                                       frequency VARCHAR(50) NOT NULL,
                                       start_date DATE NOT NULL,
                                       end_date DATE,
                                       next_execution_date DATE NOT NULL,
                                       last_execution_date DATE,
                                       active BOOLEAN NOT NULL DEFAULT TRUE,
                                       day_of_month INTEGER,
                                       day_of_week INTEGER,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_recurring_transaction_category
                                         FOREIGN KEY (category_id) REFERENCES t_category(id),
                                       CONSTRAINT fk_recurring_transaction_user
                                         FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
                                       CONSTRAINT check_day_of_month
                                         CHECK (day_of_month IS NULL OR (day_of_month >= 1 AND day_of_month <= 31)),
                                       CONSTRAINT check_day_of_week
                                         CHECK (day_of_week IS NULL OR (day_of_week >= 1 AND day_of_week <= 7))
);

CREATE INDEX idx_recurring_transaction_user ON t_recurring_transaction(user_id);
CREATE INDEX idx_recurring_transaction_next_execution ON t_recurring_transaction(next_execution_date);
CREATE INDEX idx_recurring_transaction_active ON t_recurring_transaction(active);
