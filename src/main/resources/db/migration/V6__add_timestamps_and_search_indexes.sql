-- Добавление временных меток к транзакциям
ALTER TABLE t_transaction
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Обновляем существующие записи (если есть NULL значения)
UPDATE t_transaction SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE t_transaction SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Создаем индексы для оптимизации поиска
-- Проверяем, что user_id существует перед созданием индексов
DO $$
  BEGIN
    -- Индекс по user_id и date
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 't_transaction' AND column_name = 'user_id') THEN
      CREATE INDEX IF NOT EXISTS idx_transactions_user_date ON t_transaction(user_id, date DESC);
      CREATE INDEX IF NOT EXISTS idx_transactions_user_type_date ON t_transaction(user_id, transaction_type, date DESC);
    END IF;
  END $$;

-- Остальные индексы, которые не зависят от user_id
CREATE INDEX IF NOT EXISTS idx_transactions_description ON t_transaction(description);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON t_transaction(transaction_type);
CREATE INDEX IF NOT EXISTS idx_transactions_category ON t_transaction(category_id);
CREATE INDEX IF NOT EXISTS idx_transactions_amount ON t_transaction(amount);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON t_transaction(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON t_transaction(date DESC);
