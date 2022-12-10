
set search_path TO public;

CREATE TABLE app_user (
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    username       VARCHAR(255) NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL DEFAULT '',
    display_name   VARCHAR(255) NOT NULL DEFAULT '',
    enabled             BOOLEAN      NOT NULL DEFAULT FALSE,
    registration_time   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    password            VARCHAR NOT NULL DEFAULT ''
);

CREATE INDEX index_app_user_registration_time ON app_user(registration_time);

CREATE INDEX index_app_user_username ON app_user(username);

INSERT INTO app_user (username, email, display_name, password, enabled)
    VALUES ('admin', 'admin@example.com', 'admin', '{bcrypt}$2a$10$WhdPCuRTRbiQlwLj6x3Z7em2SEIVmbOSYmd8uch4NjQ3FagH4zI5C', TRUE);