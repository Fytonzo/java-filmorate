# java-filmorate
Template repository for Filmorate project.

Link for DB scheme:
https://app.quickdatabasediagrams.com/#/d/3OTnzO

Query samples:

All films liked by user with id 1:

SELECT name, description
FROM films AS f
LEFT OUTER JOIN film_likes AS fl ON f.film_id=fl.film_id
WHERE fl.user_id=1

All users who liked film with id 1:

SELECT * 
FROM users AS u
LEFT OUTER JOIN film_Likes as fl ON u.user_id=fl.user_id
WHERE fl.film_id=1

Top 10 films:

SELECT name, COUNT(fl.user_id)
FROM films as f
LEFT OUTER JOIN film_likes as fl on f.film_id=fl.film_id
GROUR BY f.film_id DESC
LIMIT 10



