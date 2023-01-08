CREATE TABLE IF NOT EXISTS films (
    id INTEGER PRIMARY KEY,
    name varchar(200),
    description varchar,
    release_date date,
    duration varchar,
    rating_id int
);
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    email varchar,
    login varchar,
    name varchar,
    birth_date date
);
CREATE TABLE IF NOT EXISTS film_genre (
    film_id int,
    genre_id int
);
CREATE TABLE IF NOT EXISTS genre (
    id int,
    genre_name varchar
);
CREATE TABLE IF NOT EXISTS film_likes (
    film_id int,
    user_id int
);
CREATE TABLE IF NOT EXISTS friendship (
    user_id int,
    friend_id int,
    status_id int
);
CREATE TABLE IF NOT EXISTS rating (
    id int,
    name varchar
);
CREATE TABLE IF NOT EXISTS status (
    id int,
    description varchar
);