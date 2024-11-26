
set search_path TO public;

ALTER TABLE sleep_session DROP CONSTRAINT IF EXISTS sleep_session_zone_id_fkey;
ALTER TABLE sleep_session ALTER COLUMN zone_id TYPE VARCHAR(255);

DROP TABLE timezone_identifier;
