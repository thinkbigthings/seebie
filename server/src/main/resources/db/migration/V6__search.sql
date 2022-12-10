
set search_path TO public;

CREATE TABLE operator (
    id   INT4           NOT NULL PRIMARY KEY,
    name VARCHAR(255)   NOT NULL
);

INSERT INTO operator (id, name) VALUES (0, 'LT');
INSERT INTO operator (id, name) VALUES (1, 'LTE');
INSERT INTO operator (id, name) VALUES (2, 'EQ');
INSERT INTO operator (id, name) VALUES (3, 'GTE');
INSERT INTO operator (id, name) VALUES (4, 'GT');


CREATE TABLE search_config (
    user_id        INT8         NOT NULL PRIMARY KEY REFERENCES app_user,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    last_search    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE search_config_store (
     search_config_id    INT8       NOT NULL    REFERENCES search_config (user_id),
     store_id            INT8       NOT NULL    REFERENCES store (id),
     PRIMARY KEY (search_config_id, store_id)
);

CREATE TABLE saved_search (
    id               BIGSERIAL       NOT NULL    PRIMARY KEY,
    search_config_id INT8            NOT NULL    REFERENCES search_config (user_id)
);


CREATE TABLE search_parameter (
    id              BIGSERIAL       NOT NULL    PRIMARY KEY,
    field           VARCHAR(255)    NOT NULL,
    operator_id     INT4            NOT NULL    REFERENCES operator (id),
    value           VARCHAR(255)    NOT NULL,
    saved_search_id INT8            NOT NULL    REFERENCES saved_search (id)
);

CREATE INDEX search_parameter_saved_search_index ON search_parameter (saved_search_id);
CREATE INDEX saved_search_search_config_index ON saved_search (search_config_id);
CREATE INDEX search_config_active_last_search_index ON search_config (active, last_search);

INSERT INTO search_config (user_id) SELECT app_user.id FROM app_user;
