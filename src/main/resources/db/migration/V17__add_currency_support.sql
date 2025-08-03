ALTER TABLE t_user
  ADD COLUMN default_currency VARCHAR(3) DEFAULT 'USD',
  ADD COLUMN display_secondary_currency BOOLEAN DEFAULT FALSE,
  ADD COLUMN secondary_currency VARCHAR(3);

ALTER TABLE t_transaction
  ADD COLUMN currency VARCHAR(3) DEFAULT 'USD',
  ADD COLUMN original_amount DECIMAL(19,2),
  ADD COLUMN exchange_rate DECIMAL(10,6);

UPDATE t_transaction SET currency = 'USD' WHERE currency IS NULL;
UPDATE t_transaction SET original_amount = amount WHERE original_amount IS NULL;

ALTER TABLE t_category
  ADD COLUMN currency VARCHAR(3);

UPDATE t_category c
SET currency = (SELECT default_currency FROM t_user u WHERE u.id = c.user_id)
WHERE c.currency IS NULL;

ALTER TABLE budgets
  ADD COLUMN currency VARCHAR(3) DEFAULT 'USD';

UPDATE budgets b
SET currency = (SELECT default_currency FROM t_user u WHERE u.id = b.user_id)
WHERE b.currency IS NULL;

ALTER TABLE t_recurring_transaction
  ADD COLUMN currency VARCHAR(3) DEFAULT 'USD';

UPDATE t_recurring_transaction rt
SET currency = (SELECT default_currency FROM t_user u WHERE u.id = rt.user_id)
WHERE rt.currency IS NULL;

ALTER TABLE t_goal
  ADD COLUMN currency VARCHAR(3) DEFAULT 'USD';

UPDATE t_goal g
SET currency = (SELECT default_currency FROM t_user u WHERE u.id = g.user_id)
WHERE g.currency IS NULL;

CREATE TABLE t_exchange_rate (
                               id BIGSERIAL PRIMARY KEY,
                               from_currency VARCHAR(3) NOT NULL,
                               to_currency VARCHAR(3) NOT NULL,
                               rate DECIMAL(10,6) NOT NULL,
                               valid_from TIMESTAMP NOT NULL,
                               valid_to TIMESTAMP,
                               source VARCHAR(50),
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT uk_exchange_rate_currencies_valid
                                 UNIQUE (from_currency, to_currency, valid_from)
);

CREATE INDEX idx_exchange_rate_currencies ON t_exchange_rate(from_currency, to_currency);
CREATE INDEX idx_exchange_rate_valid_from ON t_exchange_rate(valid_from);
CREATE INDEX idx_exchange_rate_valid_to ON t_exchange_rate(valid_to);
