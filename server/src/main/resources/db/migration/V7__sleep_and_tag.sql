
set search_path TO public;

CREATE TABLE sleep_session (
    id              INT8        GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         INT8        NOT NULL REFERENCES app_user (id),
    minutes         INT4        NOT NULL DEFAULT 0,
    out_of_bed      BOOLEAN     NOT NULL DEFAULT FALSE,
    ignored         BOOLEAN     NOT NULL DEFAULT FALSE,
    date_awakened   DATE        NOT NULL DEFAULT now(),
    notes           VARCHAR     NOT NULL DEFAULT '',
    UNIQUE(user_id, date_awakened)
);

CREATE INDEX index_sleep_session_user ON sleep_session(user_id);
CREATE INDEX index_sleep_session_date ON sleep_session(date_awakened);

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

