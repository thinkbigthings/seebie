# Seebie Project

This is a sleep diary app.

## Setup

Before anything else: set up the prerequisite software for each of the subprojects

[Setup for server project](server/README.md)

[Setup for web project](reactjs/README.md)


## Quickstart Commands

You'll need to have prerequisite software installed and on the path as described in the Setup section.

Do a full build with `gradlew clean build`
which will create a postgres docker container, and leave it up and running.

From the root folder, start the server with `gradlew :server:bootRun`
which will run the web server.

From the reactjs folder, run `npm start`
which will serve the front end files and open a browser for you.


## Project Structure

### Web

The Web project is in `reactjs`.
From that folder we can run all `npm` commands like normal.

The IDE can set up a run configuration to run npm as well.

### Server

There are server oriented sub-projects: server and perf.

The server project is a web server for a normal web application.
The perf project is a basic load testing application that runs against the server.


### Project Composition with Gradle

All projects can be run with Gradle from the base (current) folder.

For example:
`gradlew :server:bootRun` is equivalent to `gradlew -p server bootRun`


Note that the React app has a proxy set in `package.json` so that we can run
the front end and back end independently. Another advantage of the proxy is that
we can just point it to another address or port to use the local web UI
against any server we want.


### Do a full build

To build a JAR file that can be deployed and run,
use the command `gradlew build` from the base project folder.

To run the fully built application,
cd to the server folder (so you can access properties)
and run e.g. `java --enable-preview -jar build/libs/server-1.0-SNAPSHOT.jar`
Then in the browser go to `https://localhost:9000`


### Showing Blue Green Deployment (Server)

Override the port so we can run multiple servers at once. e.g.
`gradlew :server:bootRun --args='--spring.profiles.active=migration'`
`gradlew :server:bootRun --args='--server.port=9001'`
`gradlew :perf:bootRun --args='--connect.port=9001'`

To make this easier, see the commands file with the aliases.
Use `blueDeploy` and `blueClient` alternating with `greenDeploy` and `greenClient`.
Just put ./commands on your PATH

### Updating the API Version

#### How API Version Works
We assign an API Version to the software at build time, the version should not be a runtime property.
The API version we're talking about is not the `software` version, it is a `compatibility` version 
meaning it indicates whether two pieces of software should expect to be able to interact successfully via API.

If a client makes a request, and the compatibility version does not match what's on the server,
the server will return an error code and the client is expected to refresh itself and try again with the correct version.

#### Server
We assign an API Version from application.properties in the server's source main resources, which we could consider
to be part of the source code. However, the value can be overridden per Spring's property config mechanisms.
To prevent the ability to override it on the command line, we'd have to hard-code the api version in code.
Having a property seems appropriate though because it is a property.

#### Client
To get the client version into the UI, put the value in the project's .env file, then it's accessible from React.
The variable is available in React app from doing a build and also when running from `npm start`
but .env file is not monitored, so updating that file won't trigger a refresh when running from `npm start`.




### Running from IDE

Run configuration may need to be set to the appropriate version of Java.
Also: IntelliJ > Preferences > Build > Build Tools > Gradle > Gradle JVM may need to be set to the appropriate version.

To run the server and perf from IntelliJ IDEA:

- Create a Run Configuration, using the Application class as main, and
  just set the working folder to the `server` folder
- Create a Run Configuration, using the Application class as main, and
  just set the working folder to the `perf` folder
- Run either Configuration from the Run menu
- Without creating a Debug Configuration, can also debug a Run Config.



### Monitoring

[VisualVM](https://visualvm.github.io/) is a handy monitoring tool.
JConsole is included with Java but it doesn't have profiling built in.

To show that a server is under load, open the running application in VisualVM
and click on the threads tab. Note the threads named something like
`https-jsee-nio-9000-exec-1` and `https-jsee-nio-9000-exec-2`. These are 
the request handling threads, if they are green they are running. 
If they are both solid orange, the thread is parked and the server is not
actively handling any requests.

## Update Dependencies

Both the web and server project README files have an "Update Dependencies" section.


## Dev Procedures

### Branch 

- [x] Define acceptance criteria for clearly defined scope
- [x] Create branch locally and push to remote

### Develop

- [x] Must satisfy acceptance criteria
- [x] Must be covered by automated testing
- [x] Must verify security / authorization
- [x] Must verify zero downtime transition
- [x] Perf test locally
- [x] UI test locally from full build (requires online)

### Protect the Process

- [x] Can run unit/integration tests in IDE
- [x] Review test coverage, coverage > 40%
- [x] Can debug front/back end in IDE
- [x] Full clean build takes < 60s
- [x] Docs (like README's) are up to date

### Stage

- [x] Deploy to stage
- [x] UI test on stage
- [x] Perf test directed at stage

### Merge

- [x] Create PR
- [x] Squash merge to master

### Deploy

Relevant Documentation

https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#cloud-deployment-heroku 
https://devcenter.heroku.com/articles/preparing-a-spring-boot-app-for-production-on-heroku 
https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku 
https://devcenter.heroku.com/articles/deploying-gradle-apps-on-heroku 

#### To Deploy JAR

https://devcenter.heroku.com/articles/deploying-executable-jar-files

These are commands you can use directly, but really it's simpler to use the gradle plugin

// if heroku java plugin isn't installed locally yet
heroku plugins:install java

// if you want to blow away an instance and start over
heroku apps:destroy zdd-full

heroku create --no-remote
heroku apps:rename --app generatedname stage-zdd-full
heroku addons:create papertrail --app stage-zdd-full
heroku addons:create heroku-postgresql --app stage-zdd-full

// deploy with gradle, specify app name to ensure the correct target environment

    gradlew -Papp=stage-zdd-full deployHeroku
      
    
// or with command line
heroku deploy:jar server-1.0-SNAPSHOT.jar --app zdd-full --include Procfile system.properties


View logs during deployment
heroku logs --tail --app stage-zdd-full

View logs for the migration (release phase)
can be viewed from dashboard, or with command
heroku releases:output --app zdd-full

// other handy commands
heroku config --app zdd-full
heroku pg --app zdd-full
heroku run ls --app zdd-full
heroku run env --app zdd-full



## Troubleshooting

### Stack trace about a postgres deadlock
This has so far only been on the very last step. Have not done a lot of investigation into this.
MIGHT be able to swap environments again? Or just shutdown and restart server?
Or just stop and restart the client? Do we need to restart postgres?

### Client and server are running but client isn't making requests
At one point a software update for iterm2 on my laptop was messing things up
Can just restart client and it'll work

### Migration hangs
A connection can block another connection for the migration, make sure the IntelliJ DB Browser,
any psql clients, VisualVM JDBC profilers, or previous servers, are disconnected.

If something goes terribly wrong, you may need to even drop the docker instance and rebuild everything.
