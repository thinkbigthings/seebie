
set search_path TO public;

CREATE TABLE operator (
    id   SMALLINT           NOT NULL PRIMARY KEY,
    name VARCHAR(255)   NOT NULL
);

INSERT INTO operator (id, name) VALUES (0, 'LT');
INSERT INTO operator (id, name) VALUES (1, 'LTE');
INSERT INTO operator (id, name) VALUES (2, 'EQ');
INSERT INTO operator (id, name) VALUES (3, 'GTE');
INSERT INTO operator (id, name) VALUES (4, 'GT');

