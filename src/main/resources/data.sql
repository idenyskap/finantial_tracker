INSERT INTO t_user (id, name) VALUES
                                (1, 'Emma'),
                                (2, 'Bob')
ON CONFLICT DO NOTHING;

INSERT INTO t_category (id, name, color, user_id) VALUES
                                                    (1, 'Food', NULL, 1),
                                                    (2, 'Transport', NULL, 1),
                                                    (3, 'Salary', NULL, 1),
                                                    (4, 'German School', '#00AAFF', 1)
ON CONFLICT DO NOTHING;

INSERT INTO t_transaction (id, amount, transaction_type, category_id, date, description) VALUES
                                                                                           (1, 1000.00, 'INCOME', 3, '2024-06-01', 'Salary for May'),
                                                                                           (2, 999.99, 'EXPENSE', 2, '2024-07-01', 'Updated expense'),
                                                                                           (3, 20.00, 'EXPENSE', 2, '2024-06-03', 'Bus ticket')
ON CONFLICT DO NOTHING;
