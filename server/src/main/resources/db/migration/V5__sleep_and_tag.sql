
set search_path TO public;

CREATE SEQUENCE sleep_session_sequence AS INT8 START 101;

CREATE TABLE sleep_session (
    id              INT8        DEFAULT nextval('sleep_session_sequence') PRIMARY KEY,
    user_id         INT8        NOT NULL REFERENCES app_user (id),
    start_time      TIMESTAMPTZ NOT NULL DEFAULT now(),
    stop_time       TIMESTAMPTZ NOT NULL DEFAULT now(),
    minutes_awake   INTEGER     NOT NULL DEFAULT 0,
    minutes_asleep  INTEGER     NOT NULL DEFAULT 0,
    notes           VARCHAR     NOT NULL DEFAULT '',
    CONSTRAINT stop_after_start CHECK (stop_time >= start_time),
    CONSTRAINT correct_calculation CHECK ( ABS((EXTRACT(EPOCH FROM (stop_time - start_time)) / 60) - minutes_awake) = minutes_asleep)
);

CREATE INDEX index_sleep_session_user ON sleep_session(user_id);

CREATE TABLE tag (
    id              INT8        GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         INT8        NOT NULL REFERENCES app_user (id),
    text           VARCHAR      NOT NULL DEFAULT '',
    UNIQUE(user_id, text)
);

CREATE INDEX index_tag_user ON tag(user_id);

CREATE TABLE sleep_session_tag (
    sleep_id   INT8        NOT NULL    REFERENCES sleep_session (id),
    tag_id     INT8        NOT NULL    REFERENCES tag (id),
    PRIMARY KEY (sleep_id, tag_id)
);

