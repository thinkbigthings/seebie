import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {App} from './App';
import * as serviceWorker from './serviceWorker';

// IMPORTANT: use correct ID of your root element
// this is the ID of the div in your index.html file
const rootElement = document.getElementById('root');
const root = ReactDOM.createRoot(rootElement);

// if you use TypeScript, add non-null (!) assertion operator
// const root = createRoot(rootElement!);

root.render(
        <App />
);

// ReactDOM.render(<App />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
