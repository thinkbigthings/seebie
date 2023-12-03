# Seebie - Sleep Analysis Tool

This is a project to record and analyze your sleep.


## Setup

Before anything else: set up the prerequisite software for each of the subprojects.
Software that needs to be installed and available from the command line:

### Java

Any major distribution of Java 21 should work,
but this project has been tested with the distribution from Zulu.
Try sdkman to install and manage Java versions.

### Docker

On Linux: `sudo apt install docker.io`
Note: On Linux, needed to run docker as sudo.
docker daemon must run as root, but you can specify that a group other than docker should own the Unix socket with the -G option.

On Mac: can install [Docker Desktop](https://hub.docker.com/editions/community/docker-ce-desktop-mac) or use brew.

### Node

* Node 20.6.1


## Quickstart Commands

From the server folder, start the server with `../gradlew bootTestRun`

From the reactjs folder, run `npm start`
which will serve the front end files and open a browser for you.


## Project Structure

### Web

The Web project is in `reactjs`.
From that folder we can run all `npm` commands like normal.

The IDE can set up a run configuration to run npm as well.

Note that we use vite to server front end resources and proxy to the back end.

### Server

The backend project is in `server`, it is a web server for a typical web application.

from the base project folder, we can run `gradlew` commands like normal.

`gradlew :server:bootRun` is equivalent to `gradlew -p server bootRun`

Alternatively, from the server folder, start the server with
`../gradlew bootRun` or `../gradlew bootTestRun`


### Do a full build

To build a JAR file that can be deployed and run,
use the command `gradlew build` from the base project folder.

To run the fully built application,
cd to the server folder (so you can access properties)
and run e.g. `java --enable-preview -Dapp.security.rememberMe.key="none" -jar build/libs/server-1.0-SNAPSHOT.jar`
Then in the browser go to `https://localhost:9000`


### Running from IDE

Run configuration may need to be set to the appropriate version of Java.
Also: IntelliJ > Preferences > Build > Build Tools > Gradle > Gradle JVM may need to be set to the appropriate version.


## Dev Procedures

Trunk based development is helpful to implement continuous delivery and continuous deployment.
Find out more at https://trunkbaseddevelopment.com/

Generally there will be multiple commits to a feature branch
which when ready is merged directly to master.


### Branch 

- [x] Define acceptance criteria for clearly defined scope
- [x] Create branch locally and push to remote

### Develop

- [x] Favor incremental builds and fast feedback cycle
- [x] Must satisfy acceptance criteria
- [x] Must be covered by automated functional tests
- [x] Must be covered by security and validation tests, both positive and negative
- [x] Must verify zero downtime transition
- [x] Must pass a manual test locally

### Protect the Process

- [x] Can run unit/integration tests in IDE
- [x] Review test coverage, coverage > 40%
- [x] Can debug front/back end in IDE
- [x] Docs (like README's) are up-to-date

### Merge

- [x] Create PR
- [x] Squash merge to master
- [x] Delete remote and local branch

### Stage

- [x] Master is automatically deployed to stage
- [x] UI test on stage


## Deployments

### CI/CD

We use Github Actions to build and deploy. 
See the `.github/workflows` folder.

Right now only build and deploy on merge to master. 
So workflow is develop, merge to master, verify change in stage.
This can become more refined once we have a production environment.
Eventually would want to verify in a cloud environment before merging to master.


### Bootstrapping

When installing the software into a new environment with a new database, 
an admin user is automatically created when the database is first initialized with a schema
**and the password should be changed before the environment is exposed to the public**. 

With the admin in place more regular users can be created.

### Deployment to Production

This app set up to deploy to Heroku.
We use the gradle heroku plugin to deploy from CI
instead of using the Procfile or other Heroku integrations.
See the GitHub workflow files for the actual command and parameters.

### Rolling Back

To redeploy a specific older version,
use the [rollback feature from Heroku](https://blog.heroku.com/releases-and-rollbacks).
This is on the command line only and can not be done from the Heroku UI.
As mentioned in the article: note that this doesn't handle database migrations.


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

