
set search_path TO public;

ALTER TABLE sleep_session ALTER COLUMN start_time TYPE TIMESTAMP;
ALTER TABLE sleep_session ALTER COLUMN stop_time TYPE TIMESTAMP;

ALTER TABLE sleep_session DROP CONSTRAINT IF EXISTS correct_calculation;
