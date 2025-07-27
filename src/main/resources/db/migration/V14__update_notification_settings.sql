ALTER TABLE t_notification_settings
  DROP COLUMN IF EXISTS budget_alerts,
  DROP COLUMN IF EXISTS budget_alert_threshold,
  DROP COLUMN IF EXISTS recurring_reminders,
  DROP COLUMN IF EXISTS reminder_days_before,
  DROP COLUMN IF EXISTS goal_updates,
  DROP COLUMN IF EXISTS transaction_receipts;

ALTER TABLE t_notification_settings
  ADD COLUMN IF NOT EXISTS payment_reminders BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS payment_reminder_days INTEGER DEFAULT 1,
  ADD COLUMN IF NOT EXISTS daily_reminder BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS daily_reminder_time TIME DEFAULT '21:00:00',
  ADD COLUMN IF NOT EXISTS weekly_report BOOLEAN DEFAULT FALSE;

ALTER TABLE t_email_history
  DROP CONSTRAINT IF EXISTS t_email_history_type_check;
