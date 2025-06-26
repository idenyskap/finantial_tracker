SELECT setval('t_user_id_seq', GREATEST((SELECT MAX(id) FROM t_user), 1), true);
SELECT setval('t_category_id_seq', GREATEST((SELECT MAX(id) FROM t_category), 1), true);
SELECT setval('t_transaction_id_seq', GREATEST((SELECT MAX(id) FROM t_transaction), 1), true);
