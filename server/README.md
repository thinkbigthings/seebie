# Seebie - Sleep Analysis Tool

This is a project to record and analyze your sleep.

## Prerequisites

Software that needs to be installed and available from the command line:

* Java 17
* Docker


### Install Java

Any major distribution of Java 17 should work. 
This project has been tested with openjdk 17.0.2 downloaded from https://adoptium.net/

### Install Docker

On Linux: `sudo apt install docker.io`
Note: On Linux, needed to run docker as sudo.
docker daemon must run as root, but you can specify that a group other than docker should own the Unix socket with the -G option.

On Mac: can install [Docker Desktop](https://hub.docker.com/editions/community/docker-ce-desktop-mac) or use brew.



# Database Migrations


## Docker Postgres for Development

This project uses [testcontainers](https://www.testcontainers.org) 
for integration tests as [recommended by Docker](https://www.docker.com/blog/maintainable-integration-tests-with-docker/).
"By spinning it up from inside the test itself, you have a lot more control over the orchestration and provisioning, 
and the test is more stable. You can even check when a container is ready before you start a test"

It turns out to be convenient to use test containers for general docker setup as well.
Running the full build with the server integration tests will create and populate
a docker container with a postgres database which can be left up and running after the build.

After a full build, the database should be in a state consistent with the data populated from the integration tests.
This can be useful for bringing up the application and exploring the data, e.g. for UI development.

Once running, connect to the db through docker directly: `docker exec -it [container_name] bash`
then : `psql -U test`

or more succinctly:
`docker exec -it $(docker ps -q) psql -U test`

Handy Commands:

See running images with `docker ps`
Stop a container with `docker container stop container_name`

stop and remove all docker containers
`docker stop $(docker ps -q); docker rm $(docker ps -a -q)`



## Migrations

We use [Flyway](https://flywaydb.org) and run the migration standalone (not on default startup of the server)
so that we have more control over the migration process.

To perform a database migration, a server is run in a "migration only" mode that does the migrations and then shuts down.

e.g.

use `migrate` script that runs the migration profile with gradle,

or 

    cd server
    java --enable-preview -Dspring.profiles.active=migration -jar build/libs/server-1.0-SNAPSHOT.jar


## Heroku database

Can get a postgres command prompt with

    heroku pg:psql --app stage-zdd-full


## Environment variables

Heroku automatically creates environment variables for you. To see all of them, run

    heroku run env --app zdd-full

e.g.

    JAVA_OPTS=-XX:+UseContainerSupport -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8
    PORT=38476


## Fitting in with Heroku

There are a number of database connection environment variables generated automatically by Heroku.
They overlap, so you can use them with different technologies (i.e. straight Java vs Spring)

SPRING_DATASOURCE_URL to Spring is the same as spring.datasource.url
given the [properties rules](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config-relaxed-binding-from-environment-variables).

command line properties take top priority, so even though SPRING_DATASOURCE_URL is defined as an environment variable
automatically by Heroku, we can still override it in the command in the Procfile
to add custom database properties to the URL

    DATABASE_URL=postgres://pafei...:23782e...@ec2-34-236-215-156.compute-1.amazonaws.com:5432/d5oqne55s6np1v

    JDBC_DATABASE_URL=jdbc:postgresql://ec2...compute-1.amazonaws.com:5432/d5oqne55s6np1v?password=23782e...&sslmode=require&user=pafei...
    JDBC_DATABASE_USERNAME=pafei...
    JDBC_DATABASE_PASSWORD=23782e2da93a7a8f987949613942f9ff30a530afc640e6e05294a4cd6658c3b4

    SPRING_DATASOURCE_URL=jdbc:postgresql://ec2...compute-1.amazonaws.com:5432/d5oqne55s6np1v?password=23782e...&sslmode=require&user=pafei...
    SPRING_DATASOURCE_USERNAME=pafei...
    SPRING_DATASOURCE_PASSWORD=23782e...



## Heroku Database Migrations

See [Heroku Migrations](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps)

Heroku's [release phase](https://devcenter.heroku.com/articles/release-phase)
is one intended mechanism for migrations.

Besides the release phase, database migrations can also be run in a
[one-off dyno](https://devcenter.heroku.com/articles/one-off-dynos)

Heroku requires apps to bind a port in 60s or it's considered crashed.
Migrations can eat into that time, so do that separately from deployment.
The release phase has a 1h timeout and a release can be
monitored and [stopped](https://help.heroku.com/Z44Q4WW4/how-do-i-stop-a-release-phase).

Running from a [flyway caller](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps#using-flyway)
is the best way to do a migration without doing the source code deployment.


## Threads

The Logging filter's ScheduledThreadPoolExecutor has a core pool size

The server defines a number of standard spring boot threads:
server.tomcat.max-threads
server.tomcat.min-spare-threads 
server.tomcat.accept-count


## Security

## HTTPS

To make self-signed keys for dev:
`keytool -genkeypair -alias app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore app.dev.p12 -validity 3650`

To update HTTPS related files and properties, see the `server.ssl.*` properties used by Spring Boot

We don't include the p12 file when deploying to heroku, 
but get https by virtue of being a subdomain of herokuapps.com which has a CA cert.
Http automatically redirects to https on heroku. Locally it always requires https.


## Running

Doing a full build and leaving the docker container for postgres running
will allow us to run standalone.

## Debugging

Right click the main class and "Debug Application (main)"

## Testing



### Unit test
 
    gradlew test

### Integration Test

    gradlew integrationTest
    
### Both tests

    gradlew check
    
### Code Coverage

We can get code coverage metrics with Jacoco, and can do so for either unit tests, integration tests, or both.

    Generate coverage for just unit tests: `gradlew test`

    Generate coverage for just integration tests: `gradlew integrationTest`

    Generate coverage for all tests: `gradlew build`

Then see output in build/reports/jacoco/html/index.html


### Manual test

curl quick guide: https://gist.github.com/subfuzion/08c5d85437d5d4f00e58

WITH SECURITY

(this one should fail)
curl -kv --user user:password "https://localhost:9000/user/admin"

(this one should pass)
curl -kv --user admin:admin "https://localhost:9000/user/admin"

rm cookies.txt
curl -kv -b cookies.txt -c cookies.txt --user admin:admin "https://localhost:9000/login"
cat cookies.txt
curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
cat cookies.txt
curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/logout"
cat cookies.txt
curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
cat cookies.txt
rm cookies.txt

Run the server, then from another command line run `curl -k https://localhost:9000/user`

See most recent users:
`curl -k "https://localhost:9000/user?page=0&size=2&sort=registrationTime,desc"`

post:
`curl -k -X POST -H "Content-Type: application/json" -d '{"username":"user1", "displayName":"user1", "email":"us@r.com"}' https://localhost:9000/user`
or if the json is in a file:
`curl -k -X POST -H "Content-Type: application/json" -d @data-file.json https://localhost:9000/user`

Actuator (admin/management endpoints) enpoints are listed at
`https://localhost:9000/actuator`

For example, try /actuator/health

### Web

Base URL is at https://localhost:9000/index.html

Static content (built JS, etc) should go into src/main/resources/static


## Update Dependencies

From this project, use `../gradlew dependencies`

Also see [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin)
To discover what dependencies are out of date.

To upgrade versions of Java in general:

- Note that Gradle's java toolchain feature allows us to run Gradle with one version and build with another version
  (useful if the current version of Gradle doesn't support the latest version of Java)
- Set the project base build.gradle's sourceCompatibility
- Update the server README that references Java version
- The heroku plugin also references the jdkVersion


To upgrade versions of Java in IntelliJ:

- I think you need to add the SDK in Module Settings -> Platform Settings -> SDK
  But see if updating Build Tools below works first
- Click "IntelliJ IDEA" -> Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle
  and set Gradle JVM to the new version
- Might need to right click the project and go to module settings to set it there too?
- You'll also need to edit the version in any Run Configurations
