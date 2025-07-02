ALTER TABLE t_transaction ALTER COLUMN transaction_type TYPE VARCHAR(50);

DROP TYPE IF EXISTS transactiontype;
