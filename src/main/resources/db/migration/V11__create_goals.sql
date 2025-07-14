CREATE TABLE t_goal (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      description TEXT,
                      target_amount DECIMAL(10,2) NOT NULL,
                      current_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                      target_date DATE NOT NULL,
                      user_id BIGINT NOT NULL,
                      category_id BIGINT,
                      status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                      priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
                      color VARCHAR(50),
                      icon VARCHAR(50),
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      completed_at TIMESTAMP,

                      CONSTRAINT fk_goal_user
                        FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
                      CONSTRAINT fk_goal_category
                        FOREIGN KEY (category_id) REFERENCES t_category(id) ON DELETE SET NULL,
                      CONSTRAINT check_amounts
                        CHECK (current_amount >= 0 AND target_amount > 0)
);

CREATE INDEX idx_goal_user ON t_goal(user_id);
CREATE INDEX idx_goal_status ON t_goal(status);
CREATE INDEX idx_goal_target_date ON t_goal(target_date);
