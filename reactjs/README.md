# Project Argus

This is a project to aggregate inventory across physical dispensaries.

## Prerequisites

Software that needs to be installed and available from the command line:

* Node 16.14.0


## Intro

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Icons

This project uses [Font Awesome](https://fontawesome.com) for icons.

To [set it up for React](https://fontawesome.com/docs/web/use-with/react/)

    npm i --save @fortawesome/fontawesome-svg-core
    npm i --save @fortawesome/free-solid-svg-icons
    npm i --save @fortawesome/free-regular-svg-icons
    npm i --save @fortawesome/react-fontawesome

There are a few ways to use it, but to start out we can [use icons individually](https://fontawesome.com/docs/web/use-with/react/add-icons#add-individual-icons-explicitly)


## Themes and Colors

The bootstrap dark theme is applied like so

    <html lang="en" data-bs-theme="dark">

#5f2b83

https://getbootstrap.com/docs/5.3/customize/color-modes/



## Error Handling

The Error Boundary is for catching general exceptions and errors that the app doesn't know about
such as calling an undefined method at render time. It doesn't catch everything (e.g. inside callbacks)

In general we should favor catching errors at the point we know where an error could occur
such as catching non-200 http responses when fetching data, and displaying an appropriate notification there.

## Debugging

IntelliJ supports debugging JS in the IDE. 

To Debug the UI:

- Start the server from services section
- Run `npm start`
- Launch a JavaScript Debug configuration (not Debug on NPM Start)

See the IDE help for more details.

## Update Dependencies

To upgrade NPM dependencies:

    npm update
    npm audit fix

To upgrade React: 

    npm install react@17.0.0 react-dom@17.0.0

Note: if bootstrap is upgrade, react-bootstrap needs to be upgraded to a compatible verision

Note: that fontawesome and bootstrap can be served by CDN
but this project includes them via npm
So they can be upgraded by the npm mechanism like other libs
and can be detected by security scans


## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.<br />
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.<br />
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.<br />
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br />
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (Webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: https://facebook.github.io/create-react-app/docs/code-splitting

### Analyzing the Bundle Size

This section has moved here: https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size

### Making a Progressive Web App

This section has moved here: https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app

### Advanced Configuration

This section has moved here: https://facebook.github.io/create-react-app/docs/advanced-configuration

### Deployment

This section has moved here: https://facebook.github.io/create-react-app/docs/deployment

### `npm run build` fails to minify

This section has moved here: https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify
