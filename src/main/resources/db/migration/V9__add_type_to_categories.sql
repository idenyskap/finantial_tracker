ALTER TABLE t_category
  ADD COLUMN category_type VARCHAR(50);

UPDATE t_category
SET category_type = 'EXPENSE'
WHERE category_type IS NULL;

ALTER TABLE t_category
  ALTER COLUMN category_type SET NOT NULL;
