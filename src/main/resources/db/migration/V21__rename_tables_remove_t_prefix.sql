-- Migration to rename tables: remove t_ prefix
-- This follows modern naming conventions (plural, no prefix)

-- Step 1: Drop foreign key constraints that reference t_user
ALTER TABLE t_category DROP CONSTRAINT IF EXISTS fk_category_user;
ALTER TABLE t_transaction DROP CONSTRAINT IF EXISTS fk_transaction_user;
ALTER TABLE budgets DROP CONSTRAINT IF EXISTS fk_budget_user;
ALTER TABLE budgets DROP CONSTRAINT IF EXISTS fk_budget_category;
ALTER TABLE saved_searches DROP CONSTRAINT IF EXISTS fk_saved_search_user;
ALTER TABLE t_recurring_transaction DROP CONSTRAINT IF EXISTS fk_recurring_transaction_user;
ALTER TABLE t_recurring_transaction DROP CONSTRAINT IF EXISTS fk_recurring_transaction_category;
ALTER TABLE t_goal DROP CONSTRAINT IF EXISTS fk_goal_user;
ALTER TABLE t_goal DROP CONSTRAINT IF EXISTS fk_goal_category;
ALTER TABLE t_notification_settings DROP CONSTRAINT IF EXISTS fk_notification_settings_user;
ALTER TABLE t_email_history DROP CONSTRAINT IF EXISTS fk_email_history_user;

-- Step 2: Drop indexes that will be recreated
DROP INDEX IF EXISTS idx_notification_settings_user;
DROP INDEX IF EXISTS idx_email_history_user;
DROP INDEX IF EXISTS idx_email_history_created;
DROP INDEX IF EXISTS idx_email_quota_date;
DROP INDEX IF EXISTS idx_exchange_rate_currencies;
DROP INDEX IF EXISTS idx_exchange_rate_valid_from;
DROP INDEX IF EXISTS idx_exchange_rate_valid_to;
DROP INDEX IF EXISTS idx_recurring_transaction_user;
DROP INDEX IF EXISTS idx_recurring_transaction_next_execution;
DROP INDEX IF EXISTS idx_recurring_transaction_active;
DROP INDEX IF EXISTS idx_goal_user;
DROP INDEX IF EXISTS idx_goal_status;
DROP INDEX IF EXISTS idx_goal_target_date;

-- Step 3: Rename tables
ALTER TABLE t_user RENAME TO users;
ALTER TABLE t_category RENAME TO categories;
ALTER TABLE t_transaction RENAME TO transactions;
ALTER TABLE t_goal RENAME TO goals;
ALTER TABLE t_recurring_transaction RENAME TO recurring_transactions;
ALTER TABLE t_notification_settings RENAME TO notification_settings;
ALTER TABLE t_exchange_rate RENAME TO exchange_rates;
ALTER TABLE t_email_history RENAME TO email_history;
ALTER TABLE t_email_quota RENAME TO email_quota;

-- Step 4: Recreate foreign key constraints with new table names
ALTER TABLE categories
    ADD CONSTRAINT fk_category_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

ALTER TABLE budgets
    ADD CONSTRAINT fk_budget_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE budgets
    ADD CONSTRAINT fk_budget_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

ALTER TABLE saved_searches
    ADD CONSTRAINT fk_saved_search_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE recurring_transactions
    ADD CONSTRAINT fk_recurring_transaction_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE recurring_transactions
    ADD CONSTRAINT fk_recurring_transaction_category
        FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE goals
    ADD CONSTRAINT fk_goal_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE goals
    ADD CONSTRAINT fk_goal_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

ALTER TABLE notification_settings
    ADD CONSTRAINT fk_notification_settings_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE email_history
    ADD CONSTRAINT fk_email_history_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Step 5: Recreate indexes with consistent naming
CREATE INDEX idx_notification_settings_user ON notification_settings(user_id);
CREATE INDEX idx_email_history_user ON email_history(user_id);
CREATE INDEX idx_email_history_created ON email_history(created_at);
CREATE INDEX idx_email_quota_date ON email_quota(date);
CREATE INDEX idx_exchange_rates_currencies ON exchange_rates(from_currency, to_currency);
CREATE INDEX idx_exchange_rates_valid_from ON exchange_rates(valid_from);
CREATE INDEX idx_exchange_rates_valid_to ON exchange_rates(valid_to);
CREATE INDEX idx_recurring_transactions_user ON recurring_transactions(user_id);
CREATE INDEX idx_recurring_transactions_next_execution ON recurring_transactions(next_execution_date);
CREATE INDEX idx_recurring_transactions_active ON recurring_transactions(active);
CREATE INDEX idx_goals_user ON goals(user_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_target_date ON goals(target_date);

-- Step 6: Rename unique constraint on exchange_rates
ALTER TABLE exchange_rates DROP CONSTRAINT IF EXISTS uk_exchange_rate_currencies_valid;
ALTER TABLE exchange_rates
    ADD CONSTRAINT uk_exchange_rates_currencies_valid
        UNIQUE (from_currency, to_currency, valid_from);