![deployment pic](https://github.com/thinkbigthings/seebie/actions/workflows/trigger-cd.yml/badge.svg)

![Java](https://img.shields.io/badge/java-23-blue?style=for-the-badge&logo=openjdk&logoColor=orange)
![Tech](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Tech](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)

# Seebie - Sleep Hygiene Tool

## Overview

This is a sleep diary app that lets you track your sleep but also
correlates it with other factors in your life.
Seeing how your sleep is affected by things like caffeine, alcohol,
sleeping conditions, etc., can help you make better decisions about your sleep habits.


## Getting started

1. Do the Quickstart below to get the app running locally.
2. From there, the [wiki](https://github.com/thinkbigthings/seebie/wiki) 
has all the developer documentation.
3. Finally, see the [project page](https://thinkbigthings.github.io/seebie) for build information
like test reports, code coverage, and more.

## Quickstart

### Prerequisites

Software that needs to be installed and available from the command line:

- Java 23 (tested on Zulu distribution)
- Node 20 (tested on Node 20.6.1)
- Docker (Try `sudo apt install docker.io` on Linux or  [Docker Desktop](https://hub.docker.com/editions/community/docker-ce-desktop-mac) or brew on Mac)

### Running

- From the project folder, start the back end with `gradlew server:bootTestRun`
- From the react folder, start the front end with `npm start`
which will serve the front end files and open a browser for you.
log in with username `admin` and password `admin` or `test` and `test`

### Troubleshooting

- If this is the very first time running, we need a keystore file to exist.
  Use this command: `keytool -genkeypair -alias mykey -keyalg RSA -keysize 2048 -validity 365 -keystore ~/.keystore`
  (this is for local development only)
- If you ran gradle before setting the PATH, the daemon will have an old path and might not pick up npm, for example. 
  To restart the daemon, use `gradlew --stop`

