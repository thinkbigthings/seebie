
set search_path TO public;

-- create notification for admin user who was bootstrapped
INSERT INTO notification (user_id, last_sent)
    VALUES (1, NOW());
