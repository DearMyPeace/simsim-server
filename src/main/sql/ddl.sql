# 유저 정보 테이블
DROP TABLE IF EXISTS users_tbl;
CREATE TABLE `users_tbl` (
    `user_id` bigint NOT NULL AUTO_INCREMENT,
    `user_email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `user_role` enum('ADMIN','GUEST','USER') COLLATE utf8mb4_unicode_ci NOT NULL,
    `user_grade` tinyint NOT NULL DEFAULT '0',
    `user_piece_cnt` int NOT NULL DEFAULT '0',
    `user_provider` enum('GOOGLE','APPLE') COLLATE utf8mb4_unicode_ci NOT NULL,
    `user_persona` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'F',
    `user_bg_image` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `user_status` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Y',
    `created_date` datetime(6) NOT NULL,
    `modified_date` datetime(6) NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `user_email` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE users_tbl AUTO_INCREMENT = 1;


# 일기 테이블
DROP TABLE IF EXISTS diary_tbl;
CREATE TABLE `diary_tbl` (
    `diary_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `diary_content` varchar(3000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `diary_list_key` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
    `diary_delete_yn` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
    `marked_date` date NOT NULL,
    `created_date` datetime(6) NOT NULL,
    `modified_date` datetime(6) NOT NULL,
    PRIMARY KEY (`diary_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE diary_tbl AUTO_INCREMENT = 1;


# AI 분석 및 편지 테이블
DROP TABLE IF EXISTS daily_ai_response_tbl;
CREATE TABLE `daily_ai_response_tbl` (
    `ai_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `ai_target_date` date NOT NULL,
    `ai_diary_summary` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ai_reply_content` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ai_happy_cnt` int NOT NULL DEFAULT '0',
    `ai_appreciation_cnt` int NOT NULL DEFAULT '0',
    `ai_love_cnt` int NOT NULL DEFAULT '0',
    `ai_analyze_positive` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ai_analyze_positive_total` int DEFAULT NULL,
    `ai_tranquility_cnt` int NOT NULL DEFAULT '0',
    `ai_curiosity_cnt` int NOT NULL DEFAULT '0',
    `ai_surprise_cnt` int NOT NULL DEFAULT '0',
    `ai_analyze_neutral` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ai_analyze_neutral_total` int DEFAULT NULL,
    `ai_sad_cnt` int NOT NULL DEFAULT '0',
    `ai_angry_cnt` int NOT NULL DEFAULT '0',
    `ai_fear_cnt` int NOT NULL DEFAULT '0',
    `ai_analyze_negative` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ai_analyze_negative_total` int DEFAULT NULL,
    `ai_analyze_factors` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ai_reply_status` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
    `ai_is_first` tinyint(1) NOT NULL DEFAULT '0',
    `created_date` datetime(6) NOT NULL,
    `modified_date` datetime(6) NOT NULL,
    PRIMARY KEY (`ai_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE daily_ai_response_tbl AUTO_INCREMENT = 1;


# 레포트 테이블
DROP TABLE IF EXISTS monthly_report_tbl;
CREATE TABLE `monthly_report_tbl` (
    `month_report_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `mr_target_date` date NOT NULL,
    `mr_target_year` int NOT NULL,
    `mr_target_month` int NOT NULL,
    `mr_summary` varchar(3000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `mr_keywords_data` JSON,
    `created_date` datetime NOT NULL,
    `modified_date` datetime NOT NULL,
    PRIMARY KEY (`month_report_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE monthly_report_tbl AUTO_INCREMENT = 1;


# 페르소나 테이블
DROP TABLE IF EXISTS persona_info_tbl;
CREATE TABLE persona_info_tbl (
    persona_id bigint not null auto_increment,
    persona_name varchar(10) not null unique,
    persona_code char(1) not null unique,
    primary key (persona_id)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;