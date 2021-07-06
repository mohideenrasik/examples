--changeset Rasik:1

CREATE TABLE author (
	id INTEGER PRIMARY KEY,
	name VARCHAR(1000),
	thumbnail VARCHAR(1000) 
);

CREATE TABLE post (
	id INTEGER PRIMARY KEY,
	title VARCHAR(1000),
	text CLOB,
	category VARCHAR(1000),
	author_id INTEGER REFERENCES author(id) 
);

INSERT INTO author (id, name, thumbnail) VALUES (1, 'Author 1', 'Thumbnail 1');
INSERT INTO author (id, name, thumbnail) VALUES (2, 'Author 2', 'Thumbnail 2');
INSERT INTO author (id, name, thumbnail) VALUES (3, 'Author 3', 'Thumbnail 3');

INSERT INTO post (id, title, text, category, author_id) VALUES (1, 'Post 1', 'Text 1', 'Category 1', 1);
INSERT INTO post (id, title, text, category, author_id) VALUES (2, 'Post 2', 'Text 2', 'Category 2', 2);
INSERT INTO post (id, title, text, category, author_id) VALUES (3, 'Post 3', 'Text 3', 'Category 3', 3);
INSERT INTO post (id, title, text, category, author_id) VALUES (4, 'Post 4', 'Text 4', 'Category 1', 1);
INSERT INTO post (id, title, text, category, author_id) VALUES (5, 'Post 5', 'Text 5', 'Category 2', 2);
INSERT INTO post (id, title, text, category, author_id) VALUES (6, 'Post 6', 'Text 6', 'Category 3', 3);
INSERT INTO post (id, title, text, category, author_id) VALUES (7, 'Post 7', 'Text 7', 'Category 1', 1);
INSERT INTO post (id, title, text, category, author_id) VALUES (8, 'Post 8', 'Text 8', 'Category 2', 2);
INSERT INTO post (id, title, text, category, author_id) VALUES (9, 'Post 9', 'Text 9', 'Category 3', 3);
INSERT INTO post (id, title, text, category, author_id) VALUES (10, 'Post 10', 'Text 10', 'Category 1', 1);
INSERT INTO post (id, title, text, category, author_id) VALUES (11, 'Post 11', 'Text 11', 'Category 2', 2);
INSERT INTO post (id, title, text, category, author_id) VALUES (12, 'Post 12', 'Text 12', 'Category 3', 3);
INSERT INTO post (id, title, text, category, author_id) VALUES (13, 'Post 13', 'Text 13', 'Category 1', 1);
INSERT INTO post (id, title, text, category, author_id) VALUES (14, 'Post 14', 'Text 14', 'Category 2', 2);
INSERT INTO post (id, title, text, category, author_id) VALUES (15, 'Post 15', 'Text 15', 'Category 3', 3);
