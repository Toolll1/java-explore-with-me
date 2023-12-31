drop table if exists users cascade;
drop table if exists categories cascade;
drop table if exists events cascade;
drop table if exists requests cascade;
drop table if exists compilations cascade;
drop table if exists comments cascade;
drop table if exists ban cascade;

create table if not exists users (
user_id bigint not null generated by default as identity primary key,
name varchar(320) not null,
email varchar(320) UNIQUE not null
);

create table if not exists categories (
category_id bigint not null generated by default as identity primary key,
name varchar(320) UNIQUE not null
);

create table if not exists events (
event_id bigint not null generated by default as identity primary key,
annotation varchar(2000) not null,
category_id bigint not null references categories(category_id),
description varchar(7000),
event_date timestamp without time zone not null,
lon float not null,
lat float not null,
paid boolean not null,
participant_limit int not null,
request_moderation boolean not null,
title varchar(120) not null,
status varchar(9) not null,
created_on timestamp without time zone not null,
published_on timestamp without time zone,
views int,
confirmed_requests int,
user_id bigint not null references users(user_id)
);

create table if not exists requests (
request_id bigint not null generated by default as identity primary key,
request_date timestamp without time zone not null,
event_id bigint not null references events(event_id),
user_id bigint not null references users(user_id),
status varchar(9) not null
);

create table if not exists compilations (
compilation_id bigint not null generated by default as identity primary key,
pinned boolean not null,
title varchar(50) not null
);

create table if not exists compilations_events (
compilation_id int not null references compilations(compilation_id),
event_id int not null references events(event_id)
);

create table if not exists comments (
comment_id bigint not null generated by default as identity primary key,
created_on timestamp without time zone not null,
update_on timestamp without time zone,
comment_text varchar(7000) not null
CHECK (length(comment_text) > 0),
event_id bigint references events(event_id),
user_id bigint not null references users(user_id),
parent_comment_id bigint
);

create table if not exists ban (
ban_id bigint not null generated by default as identity primary key,
created_on timestamp without time zone not null,
end_of_ban timestamp without time zone not null,
user_id bigint not null references users(user_id)
);


