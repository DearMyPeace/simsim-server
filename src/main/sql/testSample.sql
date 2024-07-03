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



INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(2, 3, '2024-07-01', '오늘은 가족들과 마라탕을 맛있게 먹고, 비 오는 날씨에 기분이 좋았지만 잠이 부족해 졸리고 운동의 필요성을 느꼈다.', '오늘 정말 다양한 경험을 하셨네요! 마라탕을 가족들과 함께 먹으면서 즐거운 시간을 보냈다니 정말 좋았겠어요. 비록 조금 매웠지만, 가족들과 함께라면 그 매운 맛도 즐거운 추억이 되었을 것 같아요. 비가 와서 밖에 나가지 못했지만, 시원한 날씨 덕분에 기분이 좋았다니 다행이에요. 비 오는 날의 시원함은 정말 특별하죠.

잠을 많이 못 자서 졸리셨다니 조금 걱정이 되네요. 충분한 휴식이 정말 중요하니까요. 하지만 운동을 열심히 해야겠다는 결심을 하신 걸 보니 정말 대단하세요! 운동은 몸과 마음 모두에 좋은 영향을 주니까요. 오늘의 경험들을 통해 느낀 감정들을 계속해서 기록해보세요. 앞으로도 좋은 일들이 많이 생기길 바라요!', '[2, 0, 0, 1, 0]', NULL, 'N', '2024-07-03 04:07:05.823269', '2024-07-03 04:07:05.823269');
INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(3, 4, '2024-07-01', '오늘 작성한 일기가 사라져서 당황스러웠다.', '아이고, 오늘의 기록이 사라져서 많이 당황하셨겠어요! 정말 아쉬운 일이네요. 기록을 작성하는 데 시간을 들였을 텐데, 그 노력이 헛되지 않았으면 좋았을 텐데 말이에요. 그래도 이렇게 다시 시도해보시는 모습이 정말 멋지세요.

오늘 하루는 어떤 일이 있었나요? 혹시 특별한 일이나 재미있는 일이 있었나요? 아니면 그냥 평범한 하루였나요? 어떤 하루였든지, 그 속에서 느낀 감정이나 경험을 다시 한번 떠올려보는 것도 좋을 것 같아요. 때로는 기록을 잃어버리는 일이 새로운 시각으로 하루를 되돌아보게 하는 기회가 될 수도 있답니다.

기록을 다시 작성하면서 오늘의 소중한 순간들을 다시 한번 떠올려보세요. 그리고 그 순간들이 얼마나 소중하고 의미 있었는지 다시 한번 느껴보셨으면 좋겠어요. 오늘의 기록을 다시 작성하면서 느낀 감정이나 생각들을 자유롭게 표현해보세요. 언제나 응원하고 있습니다!', '[0, 0, 1, 1, 0]', NULL, 'N', '2024-07-03 04:07:11.894166', '2024-07-03 04:07:11.894166');
INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(4, 7, '2024-07-01', '오늘은 피곤하고 지친 하루였다.', '오늘은 조금 지루하고 심심한 하루였나 봐요. 때로는 그런 날도 있는 법이죠. 하지만 이런 날들이 오히려 새로운 취미나 활동을 찾는 기회가 될 수도 있어요. 혹시 평소에 해보고 싶었던 일이 있나요? 예를 들어, 책을 읽거나, 새로운 요리를 시도해보거나, 산책을 나가보는 것도 좋은 방법일 수 있어요.

또는 좋아하는 음악을 들으면서 잠시 휴식을 취하는 것도 좋겠죠. 작은 변화가 큰 차이를 만들 수 있답니다. 오늘 느꼈던 감정들을 글로 적어보는 것도 좋은 방법일 수 있어요. 이렇게 기록을 남기면 나중에 돌아보았을 때 그때의 감정을 더 잘 이해할 수 있게 되거든요.

오늘 하루를 어떻게 보냈는지, 그리고 어떤 생각을 했는지 더 이야기해주시면 좋겠어요. 언제든지 이야기를 나누고 싶을 때 여기에 적어주세요.', '[0, 0, 0, 1, 0]', NULL, 'N', '2024-07-03 04:07:19.013065', '2024-07-03 04:07:19.013065');
INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(5, 9, '2024-07-01', '오늘은 비가 많이 오고 날씨가 흐리다.', '오늘 비가 많이 오고 날씨가 흐려서 기분이 조금 가라앉을 수도 있겠어요. 비 오는 날은 종종 마음을 차분하게 만들기도 하지만, 때로는 우울하게 느껴질 수도 있죠. 그래도 비가 내리는 소리를 들으며 따뜻한 차 한 잔을 마시거나, 좋아하는 책을 읽는 것도 좋은 방법일 수 있어요. 비 오는 날의 특별한 분위기를 즐기면서, 잠시나마 일상에서 벗어나 마음의 여유를 찾는 시간이 되길 바랍니다.

비가 오는 날에는 집 안에서 할 수 있는 다양한 활동들도 많아요. 예를 들어, 좋아하는 영화를 보거나, 음악을 들으며 편안한 시간을 보내는 것도 좋겠죠. 혹은 창밖을 바라보며 비가 내리는 모습을 감상하는 것도 마음을 편안하게 해줄 수 있어요. 비가 오는 날의 소소한 즐거움을 찾아보는 것도 좋은 방법일 거예요.

오늘의 기록을 보니, 날씨가 기분에 영향을 미치는 것 같아요. 하지만 이런 날씨에도 긍정적인 면을 찾아보려는 노력이 참 멋지다고 생각해요. 앞으로도 날씨와 상관없이 하루하루를 소중하게 보내기를 바라요. 오늘 느꼈던 감정이나 생각들을 더 많이 나눠주시면 좋겠어요. 계속해서 이야기를 들려주세요.', '[0, 0, 0, 0, 1]', NULL, 'N', '2024-07-03 04:07:24.880954', '2024-07-03 04:07:24.880954');
INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(6, 10, '2024-07-01', '오늘은 화요일로 비가 내리는 날, 고척돔에서 즐거운 경기를 보았지만 역전패를 경험하고, 일기 작성 후 수정해야 할 사항들과 해야 할 일들을 정리했다.', '오늘은 정말 특별한 날이었네요! 비가 내리는 화요일에 고척돔을 방문하다니, 정말 멋진 경험이었을 것 같아요. 8회까지 즐거운 시간을 보내셨다니 다행이에요. 비록 역전과 병살로 인해 아쉬움이 남았지만, 그럼에도 불구하고 경기를 즐기셨다니 정말 긍정적인 마음가짐이 돋보입니다. 고척돔의 쾌적한 환경과 은호 덕분에 테이블석에서 경기를 관람한 것도 큰 즐거움이었겠어요.

또한, 일기 쓰기와 관련된 개선 사항들을 꼼꼼히 체크하고 계시네요. 일기 창 유지, 로그인 요청 수정, 설정창 페르소나 문구 수정 등 세세한 부분까지 신경 쓰시는 모습이 정말 인상적입니다. 안드로이드 작업도 차근차근 진행하시면 분명 좋은 결과가 있을 거예요.

오늘의 경험과 느낀 감정들을 이렇게 기록으로 남기신 것도 정말 좋은 습관이에요. 앞으로도 계속해서 일기를 쓰며 하루하루의 소중한 순간들을 기록해보세요. 오늘의 감정과 경험을 더 많이 나누어 주시면 좋겠어요. 언제나 응원합니다!', '[2, 1, 0, 0, 1]', NULL, 'N', '2024-07-03 04:07:34.438228', '2024-07-03 04:07:34.438228');
INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(7, 4, '2024-07-01', '오늘 작성한 일기가 사라져서 당황스러워하고 있습니다.', '아이고, 오늘의 기록이 사라져서 정말 당황스러우셨겠어요! 분명히 열심히 작성하셨을 텐데, 그 노력이 한순간에 사라진 것 같아 속상하셨을 것 같아요. 그래도 이렇게 다시 기록을 남기려는 의지가 정말 멋지세요.

오늘 하루가 어땠는지 다시 한번 생각해보는 것도 좋을 것 같아요. 혹시 오늘 특별히 기억에 남는 일이 있었나요? 아니면 평소와는 다른 작은 변화가 있었나요? 그런 것들을 다시 떠올리면서 기록해보는 것도 좋은 방법일 것 같아요.

기록이 사라졌을 때의 당황스러움과 아쉬움을 느끼셨겠지만, 그 감정도 소중한 경험이에요. 그만큼 오늘 하루가 중요했다는 증거니까요. 앞으로도 계속해서 일기를 쓰면서 하루하루를 소중하게 기록해보세요.

오늘의 감정을 다시 한번 떠올리며 기록해보는 것도 좋은 방법일 것 같아요. 어떤 감정이든지 솔직하게 적어보세요. 그렇게 하면 마음이 조금 더 가벼워질 거예요. 계속해서 일기를 쓰는 습관을 유지하시길 응원합니다!', '[0, 0, 1, 1, 0]', NULL, 'N', '2024-07-03 17:00:24.850388', '2024-07-03 17:00:24.850388');
INSERT INTO simsim.daily_ai_response_tbl
(ai_id, user_id, ai_target_date, ai_diary_summary, ai_reply_content, ai_analyze_emotions, ai_analyze_factors, ai_reply_status, created_date, modified_date)
VALUES(8, 4, '2024-07-01', '오늘 작성한 일기가 사라져서 당황스러웠다.', '아이고, 정말 당황스러웠겠어요! 오늘의 기록을 열심히 작성했는데 사라져버리다니, 그럴 때는 정말 속상하죠. 기록을 작성하는 데에 많은 시간과 노력이 들어갔을 텐데, 그 노력이 헛되이 된 것 같아 아쉬울 것 같아요.

하지만 긍정적으로 생각해보면, 다시 작성할 때 더 좋은 아이디어가 떠오를 수도 있고, 처음보다 더 나은 기록을 만들 수 있을 거예요. 때로는 이런 작은 실수가 더 나은 결과를 가져다주기도 하니까요.

오늘의 기록을 다시 작성하면서 느꼈던 감정이나 생각을 다시 떠올려보세요. 그리고 그 과정에서 새롭게 떠오르는 아이디어나 감정을 기록해보는 것도 좋은 방법일 것 같아요.

기록을 잃어버린 상황에서도 긍정적인 마음을 유지하려는 모습이 정말 멋져요. 앞으로도 계속해서 기록을 작성하면서 느낀 감정들을 공유해주시면 좋겠어요. 오늘의 기록을 다시 작성하면서 느꼈던 감정이나 생각을 더 자세히 이야기해주시면, 함께 나눌 수 있을 것 같아요.', '[0, 0, 1, 1, 0]', NULL, 'N', '2024-07-03 17:00:34.326012', '2024-07-03 17:00:34.326012');
