
set search_path TO public;

CREATE SEQUENCE challenge_sequence AS INT8 START 101 INCREMENT BY 50;

CREATE TABLE challenge (
    id              INT8            DEFAULT nextval('challenge_sequence') PRIMARY KEY,
    user_id         INT8            NOT NULL REFERENCES app_user (id),
    start           DATE            NOT NULL,
    finish          DATE            NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     VARCHAR(255)    NOT NULL,
    CONSTRAINT stop_after_start CHECK (finish >= start),
    UNIQUE(user_id, name)
);

CREATE INDEX index_challenge_user ON challenge(user_id);
