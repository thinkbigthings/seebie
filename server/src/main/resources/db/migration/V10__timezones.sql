
set search_path TO public;

set search_path TO public;

-- IANA time zone identifiers, these are all parsable by Java's ZoneId
CREATE TABLE timezone_identifier (
    identifier     VARCHAR(255) PRIMARY KEY
);

INSERT INTO timezone_identifier (identifier) VALUES ('America/New_York');
INSERT INTO timezone_identifier (identifier) VALUES ('America/Phoenix');
INSERT INTO timezone_identifier (identifier) VALUES ('Asia/Taipei');

ALTER TABLE sleep_session ADD COLUMN zone_id VARCHAR(255) REFERENCES timezone_identifier (identifier);

-- any existing data can be set to EST
UPDATE sleep_session SET zone_id = 'America/New_York';

ALTER TABLE sleep_session ALTER COLUMN zone_id SET NOT NULL;
