INSERT INTO foods (name, category, quantity, created_at, updated_at)
VALUES ('사과', 'FRUIT', 1000, now(), now());

INSERT INTO foods (name, category, quantity, created_at, updated_at)
VALUES ('바나나', 'FRUIT', 1000, now(), now());

-----

INSERT INTO food_histories(food_id, time_period, user_id, consumed_at)
VALUES (1, 'EVENING', 100, now());

INSERT INTO food_histories(food_id, time_period, user_id, consumed_at)
VALUES (1, 'EVENING', 100, now());

INSERT INTO food_histories(food_id, time_period, user_id, consumed_at)
VALUES (1, 'EVENING', 100, now());
