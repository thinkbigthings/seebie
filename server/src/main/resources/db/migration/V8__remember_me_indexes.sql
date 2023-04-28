
set search_path TO public;

CREATE INDEX index_persistent_logins_series ON persistent_logins (series);
CREATE INDEX index_persistent_logins_username ON SPRING_SESSION (username);
