ALTER TABLE t_user
  ADD COLUMN email VARCHAR(255) UNIQUE,
  ADD COLUMN password VARCHAR(255),
  ADD COLUMN role VARCHAR(50);

UPDATE t_user SET role = 'USER';
