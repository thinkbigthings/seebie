import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {App} from './App';

// IMPORTANT: use correct ID of your root element
// this is the ID of the div in your index.html file
const rootElement: HTMLElement = document.getElementById('root') !;
const root = ReactDOM.createRoot(rootElement);

// if you use TypeScript, add non-null (!) assertion operator
// const root = createRoot(rootElement!);

root.render(
        <App />
);

// ReactDOM.render(<App />, document.getElementById('root'));

