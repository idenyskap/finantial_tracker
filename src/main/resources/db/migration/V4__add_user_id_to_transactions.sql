ALTER TABLE t_transaction ADD COLUMN user_id BIGINT;

ALTER TABLE t_transaction ADD CONSTRAINT fk_transaction_user
  FOREIGN KEY (user_id) REFERENCES t_user(id);
