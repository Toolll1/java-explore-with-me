insert into users (name, email) values ('user1', 'user1@email.com');
insert into users (name, email) values ('user2', 'user2@email.com');
insert into users (name, email) values ('user3', 'user3@email.com');
insert into users (name, email) values ('user4', 'user4@email.com');
insert into categories (name) values ('categori1');
INSERT INTO events (annotation, category_id, description, event_date, lon, lat, paid, participant_limit, request_moderation, title, status, created_on, published_on, views, confirmed_requests, user_id) VALUES ('annotation1', 1, 'description1', '2023-10-07 22:43:02', 55.754167, 37.62, 'true', 100, 'false', 'title1', 'PUBLISHED', '2023-07-07 22:43:02', '2023-07-10 22:43:02', 0, 0, 1);
INSERT INTO events (annotation, category_id, description, event_date, lon, lat, paid, participant_limit, request_moderation, title, status, created_on, published_on, views, confirmed_requests, user_id) VALUES ('annotation2', 1, 'description2', '2023-10-07 22:43:02', 55.754167, 37.62, 'true', 100, 'false', 'title2', 'PUBLISHED', '2023-07-07 22:43:02', '2023-07-10 22:43:02', 0, 0, 1);
insert into ban (created_on, end_of_ban, user_id) values ('2023-07-07 22:43:02', '2023-07-08 22:43:02', 3);
insert into ban (created_on, end_of_ban, user_id) values ('2023-07-07 22:43:02', '2023-07-08 22:43:02', 4);
---------------------------------------------------------------------------------