
set search_path TO public;

ALTER TABLE app_user DROP COLUMN username;

ALTER TABLE app_user ADD COLUMN public_id UUID UNIQUE;
UPDATE app_user SET public_id = gen_random_uuid() WHERE public_id IS NULL;
ALTER TABLE app_user ALTER COLUMN public_id SET NOT NULL;

