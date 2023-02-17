DELETE FROM USERS;
DELETE FROM FILMS;
DELETE FROM FRIENDSHIP;
DELETE FROM FILM_GENRE;
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH 1;
MERGE INTO genre (id, genre_name) VALUES (1, 'Комедия');
MERGE INTO genre (id, genre_name) VALUES (2, 'Драма');
MERGE INTO genre (id, genre_name) VALUES (3, 'Мультфильм');
MERGE INTO genre (id, genre_name) VALUES (4, 'Триллер');
MERGE INTO genre (id, genre_name) VALUES (5, 'Документальный');
MERGE INTO genre (id, genre_name) VALUES (6, 'Боевик');
MERGE INTO rating (id, rating_name) VALUES(1, 'G');
MERGE INTO rating (id, rating_name) VALUES(2, 'PG');
MERGE INTO rating (id, rating_name) VALUES(3, 'PG-13');
MERGE INTO rating (id, rating_name) VALUES(4, 'R');
MERGE INTO rating (id, rating_name) VALUES(5, 'NC-17');