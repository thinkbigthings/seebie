![deployment pic](https://github.com/thinkbigthings/seebie/actions/workflows/deploy-on-push-to-master.yml/badge.svg)

# Seebie - Sleep Hygiene Tool

## Overview

This is an app that helps you to get better sleep.
The core functionality is a sleep diary that allows you to track your sleep 
and see how it correlates with other factors in your life.
Seeing how your sleep is affected by things like exercise, caffeine, alcohol, and
sleeping conditions can help you make better decisions about your sleep.


## Getting started as a developer

Do the Quickstart below to get the app running locally. 
From there, the [wiki](https://github.com/thinkbigthings/seebie/wiki) 
is the best place to continue as a developer.


## Quickstart

### Prerequisites

Software that needs to be installed and available from the command line:

- Java 21 (tested on Zulu distribution)
- Node 20 (tested on Node 20.6.1)
- Docker (Try `sudo apt install docker.io` on Linux or  [Docker Desktop](https://hub.docker.com/editions/community/docker-ce-desktop-mac) or brew on Mac)

### Running

- From the project folder, start the back end with `gradlew server:bootTestRun`
- From the reactjs folder, start the front end with `npm start`
which will serve the front end files and open a browser for you.
log in with username `admin` and password `admin` or `asdf` and `asdf`

