drop table if exists users_tbl;
create table users_tbl (
    user_id bigint not null auto_increment,
    user_email varchar(30) not null,
    user_name varchar(10) not null,
    user_role enum ('ADMIN','GUEST','USER') not null,
    user_grade tinyint default 0 not null,
    user_piece_cnt integer default 0 not null,
    user_persona char(1) default 'P' not null,
    user_bg_image varchar(300),
    user_status char(1) default 'Y' not null,
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    primary key (user_id)
);

drop table if exists diary_tbl;
create table diary_tbl (
    diary_id bigint not null auto_increment,
    user_id bigint not null,
    diary_content varchar(500),
    diary_list_key varchar(30) not null,
    diary_delete_yn char(1) default 'N' not null,
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    primary key (diary_id)
);

drop table if exists daily_ai_response_tbl;
create table daily_ai_response_tbl (
    ai_id bigint not null auto_increment,
    user_id bigint not null,
    ai_target_date date not null,
    ai_reply_summary varchar(30),
    ai_reply_content varchar(500),
    ai_analyze_emotions varchar(20),
    ai_analyze_factors varchar(300),
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    primary key (ai_id)
);