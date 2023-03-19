
set search_path TO public;

CREATE TABLE notification (
    user_id     INT8        PRIMARY KEY NOT NULL REFERENCES app_user (id),
    last_sent   TIMESTAMPTZ NOT NULL DEFAULT now()
);
