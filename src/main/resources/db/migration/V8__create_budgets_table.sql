CREATE TABLE budgets (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       amount NUMERIC(19,2) NOT NULL,
                       period_type VARCHAR(50) NOT NULL,
                       category_id BIGINT,
                       user_id BIGINT NOT NULL,
                       is_active BOOLEAN DEFAULT TRUE,
                       notify_threshold INTEGER DEFAULT 80,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_budget_category FOREIGN KEY (category_id)
                         REFERENCES t_category(id) ON DELETE SET NULL,

                       CONSTRAINT fk_budget_user FOREIGN KEY (user_id)
                         REFERENCES t_user(id) ON DELETE CASCADE,

                       CONSTRAINT chk_amount_positive CHECK (amount > 0),
                       CONSTRAINT chk_notify_threshold CHECK (notify_threshold >= 0 AND notify_threshold <= 100)
);

CREATE INDEX idx_budgets_user_active ON budgets(user_id, is_active);
CREATE INDEX idx_budgets_category ON budgets(category_id);
CREATE INDEX idx_budgets_period ON budgets(period_type);
