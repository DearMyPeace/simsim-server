drop table if exists users_tbl;
create table users_tbl (
    user_id bigint not null auto_increment,
    user_email varchar(50) not null unique ,
    user_name varchar(30) not null,
    user_role enum ('ADMIN','GUEST','USER') not null,
    user_grade tinyint default 0 not null,
    user_piece_cnt integer default 0 not null,
    user_persona char(1) default 'F' not null,
    user_bg_image varchar(300),
    user_status char(1) default 'Y' not null,
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    primary key (user_id)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

drop table if exists diary_tbl;
CREATE TABLE diary_tbl (
    diary_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    diary_content VARCHAR(500),
    diary_list_key VARCHAR(30) NOT NULL,
    diary_delete_yn CHAR(1) DEFAULT 'N' NOT NULL,
    date DATE NOT NULL,
    created_date DATETIME(6) NOT NULL,
    modified_date DATETIME(6) NOT NULL,
    PRIMARY KEY (diary_id)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

drop table if exists daily_ai_response_tbl;
create table daily_ai_response_tbl (
    ai_id bigint not null auto_increment,
    user_id bigint not null,
    ai_target_date date not null,
    ai_diary_summary varchar(30),
    ai_reply_content varchar(500),
    ai_analyze_emotions varchar(20),
    ai_analyze_factors varchar(300),
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    primary key (ai_id)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

drop table if exists persona_info_tbl;
create table persona_info_tbl (
    persona_id bigint not null auto_increment,
    persona_name varchar(10) not null unique,
    persona_code char(1) not null unique,
    primary key (persona_id)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;