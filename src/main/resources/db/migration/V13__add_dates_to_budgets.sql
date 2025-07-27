ALTER TABLE budgets ADD COLUMN IF NOT EXISTS start_date DATE;
ALTER TABLE budgets ADD COLUMN IF NOT EXISTS end_date DATE;

UPDATE budgets
SET start_date = CASE
                   WHEN period_type = 'WEEKLY' THEN date_trunc('week', CURRENT_DATE)::date
                   WHEN period_type = 'MONTHLY' THEN date_trunc('month', CURRENT_DATE)::date
                   WHEN period_type = 'QUARTERLY' THEN date_trunc('quarter', CURRENT_DATE)::date
                   WHEN period_type = 'YEARLY' THEN date_trunc('year', CURRENT_DATE)::date
  END,
    end_date = CASE
                 WHEN period_type = 'WEEKLY' THEN (date_trunc('week', CURRENT_DATE) + interval '6 days')::date
                 WHEN period_type = 'MONTHLY' THEN (date_trunc('month', CURRENT_DATE) + interval '1 month' - interval '1 day')::date
                 WHEN period_type = 'QUARTERLY' THEN (date_trunc('quarter', CURRENT_DATE) + interval '3 months' - interval '1 day')::date
                 WHEN period_type = 'YEARLY' THEN (date_trunc('year', CURRENT_DATE) + interval '1 year' - interval '1 day')::date
      END
WHERE start_date IS NULL;
