# Seebie - Sleep Analysis Tool

This is a project to record and analyze your sleep.

## Prerequisites

Software that needs to be installed and available from the command line:

* Node 20.6.1


## Intro

This project runs with Vite.



## Available Scripts

In the project directory, you can run:

### `npm run start`

Runs the app in the development mode.
Open the listed url to open in a browser.

The page will reload if you make edits.
You will also see any lint errors in the console.

### `npm run build`

Builds the app for production to the `build` folder.
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.
Your app is ready to be deployed!


## Icons

This project uses [Font Awesome](https://fontawesome.com) for icons.

To [set it up for React](https://fontawesome.com/docs/web/use-with/react/)

    npm install @fortawesome/fontawesome-svg-core
    npm install @fortawesome/free-solid-svg-icons
    npm install @fortawesome/free-regular-svg-icons
    npm install @fortawesome/react-fontawesome

There are a few ways to use it, but to start out we can [use icons individually](https://fontawesome.com/docs/web/use-with/react/add-icons#add-individual-icons-explicitly)


## Themes and Colors

We use Bootstrap for style and layout, etc. There is a mixture of Bootstrap styles and React-Bootstrap components.
Generally this mixture is ok, but if you use Bootstrap Javascript, it can cause problems with DOM manipulation
unless you exclusively use React-Bootstrap. See Bootstrap docs for details.

The bootstrap dark theme is applied like so

    <html lang="en" data-bs-theme="dark">

Further customization is done in App.css

NOTE: This is about Bootstrap Javascript, not Bootstrap in general:
While the Bootstrap CSS can be used with any framework, 
the Bootstrap JavaScript is not fully compatible with JavaScript frameworks like 
React, Vue, and Angular which assume full knowledge of the DOM. Both Bootstrap 
and the framework may attempt to mutate the same DOM element, 
resulting in bugs like dropdowns that are stuck in the "open" position.



## Error Handling

The Error Boundary is for catching general exceptions and errors that the app doesn't know about
such as calling an undefined method at render time. It doesn't catch everything (e.g. inside callbacks)

In general we should favor catching errors at the point we know where an error could occur
such as catching non-200 http responses when fetching data, and displaying an appropriate notification there.

## Debugging

IntelliJ supports debugging JS in the IDE:
- Start the server from services section, you can Run or Debug
- Run `npm run start` (do not use a debug configuration)
- Use a "JavaScript Debug" configuration with the URL set to the url of the JS dev server
  (or the host/port configured by npm start)
- Set breakpoints in JS files in IntelliJ and enjoy your debugging!

## Update Dependencies

To upgrade NPM dependencies:

    npm update
    npm audit fix

To upgrade React: 

    npm install react@18.2.0 react-dom@18.2.0

To upgrade Bootstrap:

> Bootstrap and fontawesome can be served by CDN
but this project includes them via npm.
So they can be upgraded by the npm mechanism like other libs
and can be detected by security scans.
**NOTE** If bootstrap is upgraded, react-bootstrap needs to be upgraded to a compatible version.

To upgrade Node:

    npm install -g n
    n latest

Alternatively if you manage node versions by downloading a .tar.gz 
and managing the path yourself, you can do that too 
but need to update the security exceptions for Mac.
See [how to do that here](https://support.apple.com/guide/mac-help/apple-cant-check-app-for-malicious-software-mchleab3a043/mac)

Don't forget to update 
- The README with the new version numbers
- The github actions yaml files
- The build.gradle and the node gradle plugin
