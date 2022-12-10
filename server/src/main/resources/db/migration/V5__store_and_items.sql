
set search_path TO public;

CREATE TABLE store (
    id              BIGSERIAL       NOT NULL    PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL    UNIQUE,
    website         VARCHAR(255)    NOT NULL
);

CREATE INDEX store_name_index ON store (name);

CREATE TABLE subspecies (
    id   INT4           NOT NULL PRIMARY KEY,
    name VARCHAR(255)   NOT NULL
);

INSERT INTO subspecies (id, name) VALUES (0, 'SATIVA');
INSERT INTO subspecies (id, name) VALUES (1, 'SATIVA_HYBRID');
INSERT INTO subspecies (id, name) VALUES (2, 'HYBRID');
INSERT INTO subspecies (id, name) VALUES (3, 'INDICA_HYBRID');
INSERT INTO subspecies (id, name) VALUES (4, 'INDICA');
INSERT INTO subspecies (id, name) VALUES (5, 'HIGH_CBD');

-- NUMERIC(precision, scale) the number 23.5141 has a precision of 6 and a scale of 4
CREATE TABLE store_item (
    id              BIGSERIAL       NOT NULL    PRIMARY KEY,
    subspecies_id   INT4            NOT NULL    REFERENCES subspecies (id),
    strain          VARCHAR(255)    NOT NULL,
    thc_percent             NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    cbd_percent             NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    bisabolol_percent       NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    caryophyllene_percent   NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    humulene_percent        NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    limonene_percent        NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    linalool_percent        NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    myrcene_percent         NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    pinene_percent          NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    terpinolene_percent     NUMERIC(5, 3)   NOT NULL DEFAULT 0,
    weight_grams    NUMERIC(3, 1)   NOT NULL,
    price_dollars   INT8            NOT NULL,
    vendor          VARCHAR(255)    NOT NULL,
    store_id        INT8            NOT NULL REFERENCES store (id),
    added           TIMESTAMPTZ     NOT NULL
);
