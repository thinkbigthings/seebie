Seebie Project
====

Sleep tracking and analysis for use with Cognitive Behavioral Therapy for Insomnia (CBTI).

## Build Requirements

This system has been tested with
Vagrant 1.6.2
Java 8 (1.8.0_05)
Gradle 2.1

## Building

to build an executable jar file: "gradle build"

## Running

Running the application requires a running database.
You can run the database distributed with this project with "vagrant up"
See the database section for more details.

Recommended way of running this application during development is with "gradle bootRun" after running "vagrant up"

Recommended way of running this application in production and live testing is with the scripts provided in the scripts folder.
Run scripts/app.sh to see all the options.
You can start with: scripts/app.sh start
You can stop with:  scripts/app.sh stop


manually test with curl

curl -k -u user@app.com:password https://localhost:9000/user/10

curl -X POST -H "Content-Type: application/json" -k -d '{"username": "j@y.com", "displayName":"jay", "password":"password"}' https://localhost:9000/register

Alternatively: To run the webapp on the command line without spring boot: java -jar build/libs/seebie-server-[version].jar

After running the application as described above, you should be able to hit the url at
https://localhost:9000/

the sample database lets you log in with username "user@app.com" and password of "password"
or admin@app.com and password of "password"

## Testing

unit tests run with "gradle test".

generate coverage report with "gradle test jacocoTestReport"
(tests need to have been run before jacocoTestReport can run)
the report is then available in build/jacocoHtml/index.html

integration tests run with "gradle intTest"
The task automatically launches the database and web servers
The jar has to have already been built for the test to work
so running with "gradle clean build intTest" should work well.

## Database

integration tests require a running database. the database server is a vagrant vm
(for more information visit http://vagrantup.com)

Can save the current state of the database with scripts/save.sh
Conversely, you can load the database.sql file into the database with scripts/load.sh

launch vagrant with "vagrant up" from the vagrant folder
stop it with "vagrant halt" from the vagrant folder

There's a gradle task to launch it too:
from the project home folder just use "gradle vagrantUp"
and the task returns when it's ready.

Can connect from command line outside vagrant with this
mysql --user=dbuser --password=dbuserpassword --host=127.0.0.1 --protocol=TCP --port=13306



## Debugging

Intellij IDEA has native support for gradle, can start/debug with keyboard shortcuts.
You can debug the application, then debug the integration test to step into test and code.
IDEA is recommended for use with this application, as it has the best gradle support.

You can run the application and connect with a remote debugger.
Make sure you have a newly built jar so your changes are used.
gradle clean build
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n -jar build/libs/seebie-server-0.0.1-SNAPSHOT.jar
To debug integration tests with Netbeans:
- build project jar "gradle build" 
- run app with debugger using above java command
- connect to the running app with Netbeans remote debugger
- put the integration test file in the unit test folder (I know, weird)
- right click integration test file and click on "debug test file" 
- you can breakpoint into application code and test code


## Properties

To add a property:
add it to the application.properties file, and @Inject it as a @Value("${prop.name}") where needed.

To override a property, can override directly with the command line like so

java -jar build/libs/my-application-1.0.jar --app.some.property=newstuff


## Setting up HTTPS

The web app is designed to only work over https. To turn it off, right now
you have to remove the code in HttpsConfiguration.java

If you want to make changes to the certificate, here are directions for what to do:

You can create a keystore with this:
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12

You can view your keystore like this:
keytool -list -v -keystore keystore.p12 -storetype pkcs12

You can test with curl like so (the -k option lets you use a self-signed cert)
curl -u user:password -k https://127.0.0.1:9000/user/4


## Web Development

We put static resources (html/css/js) in src/main/resources/static.
There are a few standard places you can put it (static, public, resources)
(http://spring.io/blog/2013/12/19/serving-static-web-content-with-spring-boot)
Content that is placed there will be served relative to the url base path /

If you run with gradle bootRun, content should automatically be reloaded
so you can just refresh the browser to see changes to static content.

By default, the document /index.html is served on a request to /

JQuery 2.x is used, so only IE9 and higher is supported.



