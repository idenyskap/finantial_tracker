CREATE TABLE saved_searches (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              search_criteria TEXT NOT NULL,
                              user_id BIGINT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

                              CONSTRAINT fk_saved_search_user FOREIGN KEY (user_id)
                                REFERENCES t_user(id) ON DELETE CASCADE,

                              CONSTRAINT uk_saved_search_name_user UNIQUE (name, user_id)
);

CREATE INDEX idx_saved_searches_user ON saved_searches(user_id);
