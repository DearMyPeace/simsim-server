drop table if exists diary;
create table diary (
       diary_pk bigint not null auto_increment,
       user_pk bigint not null,
       diary_content varchar(500),
       diary_list_key varchar(30) not null,
       diary_delete_yn char(1) default 'N' not null,
       created_date datetime(6) not null,
       modified_date datetime(6) not null,
       primary key (diary_pk)
);