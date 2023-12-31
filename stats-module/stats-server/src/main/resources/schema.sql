drop table if exists hits cascade;

create table if not exists hits (
hit_id bigint not null generated by default as identity primary key,
hit_app varchar(320) not null,
hit_uri varchar(320) not null,
hit_ip varchar(320) not null,
hit_date timestamp without time zone not null
);