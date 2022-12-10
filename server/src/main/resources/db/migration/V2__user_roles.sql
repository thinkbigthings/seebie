
set search_path TO public;

CREATE TABLE role (
    id   INT4           NOT NULL PRIMARY KEY,
    name VARCHAR(255)   NOT NULL
);

INSERT INTO role (id, name) VALUES (0, 'ADMIN');
INSERT INTO role (id, name) VALUES (1, 'USER');

CREATE TABLE user_role (
    user_id     INT8        NOT NULL    REFERENCES app_user (id),
    role_id     INT4        NOT NULL    REFERENCES role (id),
    PRIMARY KEY (user_id, role_id)
);


-- -- bootstrap admin user role

INSERT INTO user_role (user_id, role_id)
     SELECT id, 0 FROM app_user WHERE username = 'admin';

INSERT INTO user_role (user_id, role_id)
     SELECT id, 1 FROM app_user WHERE username = 'admin';
