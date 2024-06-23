INSERT INTO daily_ai_response_tbl (user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, created_date, modified_date)
VALUES
    (1, '2024-04-25 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-05-21 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-01 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-02 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-03 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-04 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-10 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-11 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-15 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-16 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-17 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-18 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-19 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-20 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-21 00:00:00', 'Today was great!', 'Today was great because I went hiking and enjoyed the outdoors.', NULL, NULL, NOW(), NOW()),
    (1, '2024-06-22 00:00:00', 'Challenging day at work', 'Work was tough today, but I managed to get through it.', NULL, NULL, NOW(), NOW());

INSERT INTO diary_tbl (user_id, diary_content, diary_list_key, created_date, modified_date)
VALUES
    (1, 'Hello World 1', 'list_key_1', '2024-06-01', NOW(6)),
    (1, 'Hello World 2', 'list_key_2', '2024-06-01', NOW(6)),
    (1, 'Hello World 3', 'list_key_3', '2024-06-01', NOW(6)),
    (1, 'Hello World 4', 'list_key_4', '2024-06-11', NOW(6)),
    (1, 'Hello World 5', 'list_key_5', '2024-06-13', NOW(6)),
    (1, 'Hello World 6', 'list_key_6', '2024-06-18', NOW(6)),
    (1, 'Hello World 7', 'list_key_7', '2024-06-20', NOW(6)),
    (1, 'Hello World 8', 'list_key_8', '2024-06-21', NOW(6)),
    (1, 'Hello World 9', 'list_key_9', '2024-06-22', NOW(6)),
    (1, 'Hello World 10', 'list_key_10', '2024-06-22', NOW(6));