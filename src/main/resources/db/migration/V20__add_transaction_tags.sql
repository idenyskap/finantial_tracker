CREATE TABLE t_tag (
                     id BIGSERIAL PRIMARY KEY,
                     name VARCHAR(50) NOT NULL,
                     color VARCHAR(7),
                     user_id BIGINT NOT NULL,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                     CONSTRAINT fk_tag_user
                       FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
                     CONSTRAINT uk_tag_name_user
                       UNIQUE (name, user_id)
);

CREATE TABLE t_transaction_tag (
                                 transaction_id BIGINT NOT NULL,
                                 tag_id BIGINT NOT NULL,

                                 PRIMARY KEY (transaction_id, tag_id),
                                 CONSTRAINT fk_transaction_tag_transaction
                                   FOREIGN KEY (transaction_id) REFERENCES t_transaction(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_transaction_tag_tag
                                   FOREIGN KEY (tag_id) REFERENCES t_tag(id) ON DELETE CASCADE
);

CREATE INDEX idx_transaction_tag_transaction ON t_transaction_tag(transaction_id);
CREATE INDEX idx_transaction_tag_tag ON t_transaction_tag(tag_id);
