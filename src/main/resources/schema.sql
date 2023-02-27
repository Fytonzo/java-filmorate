CREATE TABLE IF NOT EXISTS films (
                                     id INTEGER AUTO_INCREMENT PRIMARY KEY,
                                     name varchar(200),
                                     description varchar(500),
                                     release_date date,
                                     duration varchar(3),
                                     rating_id int
);
CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER AUTO_INCREMENT PRIMARY KEY,
                                     email varchar(64),
                                     login varchar(40),
                                     name varchar(40),
                                     birth_date date
);
CREATE TABLE IF NOT EXISTS film_genre (
                                          film_id int,
                                          genre_id int
);
CREATE TABLE IF NOT EXISTS genre (
                                     id int PRIMARY KEY,
                                     genre_name varchar(40)
);
CREATE TABLE IF NOT EXISTS film_likes (
                                          film_id int,
                                          user_id int
);
CREATE TABLE IF NOT EXISTS friendship (
                                          user_id int,
                                          friend_id int,
                                          status varchar(13)
);
CREATE TABLE IF NOT EXISTS rating (
                                      id int PRIMARY KEY,
                                      rating_name varchar(6)
);