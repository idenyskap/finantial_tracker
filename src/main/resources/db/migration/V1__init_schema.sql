DO $$
  BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'transactiontype') THEN
      CREATE TYPE transactiontype AS ENUM ('INCOME', 'EXPENSE');
    END IF;
  END;
$$;

CREATE TABLE IF NOT EXISTS t_user (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_category (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        color VARCHAR(20),
                                        user_id BIGINT,
                                        CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_transaction (
                                           id BIGSERIAL PRIMARY KEY,
                                           amount NUMERIC(19,2) NOT NULL,
                                           transaction_type transactiontype NOT NULL,
                                           category_id BIGINT REFERENCES t_category(id) ON DELETE SET NULL,
                                           date DATE NOT NULL,
                                           description TEXT
);
