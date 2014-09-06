
vagrant ssh -c "mysql --user=root --password=root --host=localhost --port=13306 information_schema < /vagrant/database/empty_schema.sql"
vagrant ssh -c "mysql --user=root --password=root --host=localhost --port=13306 appdb < /vagrant/database/database.sql"

