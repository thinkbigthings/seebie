import React from 'react';

import Form from 'react-bootstrap/Form';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

import { HashRouter, Route } from 'react-router-dom';

import SleepList from './SleepList.js';
import UserList from './UserList.js';
import About from './About.js';
import EditUser from './EditUser.js';
import Login from './Login.js';
import ErrorBoundary from './ErrorBoundary.js';

import {ErrorProvider} from './ErrorContext.js';
import ErrorModal from "./ErrorModal";
import useCurrentUser from "./useCurrentUser";
import {CurrentUserContext, CurrentUserProvider} from "./CurrentUserContext";
import {GET} from "./BasicHeaders";

// This only has to be imported on any one page, and it works for the whole site
// import custom css after so that it takes precedence
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import Container from "react-bootstrap/Container";
import EditSleep from "./EditSleep";

function App() {

    return (
        <div className="App">
            <ErrorBoundary>
                <ErrorProvider>
                    <ErrorModal />
                    <CurrentUserProvider>
                        <CurrentUserContext.Consumer>
                            { value => value[0].isLoggedIn ? <AuthenticatedApp /> : <UnauthenticatedApp /> }
                        </CurrentUserContext.Consumer>
                    </CurrentUserProvider>
                </ErrorProvider>
            </ErrorBoundary>
        </div>
    );
}

function UnauthenticatedApp() {
    return (
        <HashRouter>
            <Navbar className="border-bottom">
                <Container>
                    <Navbar.Brand>Seebie<img className="mb-1 px-1" src="favicon.ico" alt="Seebie icon" width="30" height="20"/></Navbar.Brand>
                    <Nav className="mr-auto" />
                    <Form inline="true">
                        <Nav.Link href="#login">Login</Nav.Link>
                    </Form>
                </Container>
            </Navbar>
            <Route exact path="/login" component={Login} />
        </HashRouter>
    );
}


function AuthenticatedApp() {

    const {currentUser, hasAdmin, onLogout} = useCurrentUser();

    const userUrl = "#/users/"+currentUser.username+"/edit";

    function onClickLogout() {
        fetch("/logout", GET)
            .then(response => onLogout());
    }

    let usersLink = hasAdmin() ? <Nav.Link href="#users">Users</Nav.Link> : "";

    return (
        <HashRouter>
            <Navbar className="border-bottom">
                <Container>
                    <Navbar.Brand>Seebie<img className="mb-1 px-1" src="favicon.ico" alt="Seebie icon" width="30" height="20"/></Navbar.Brand>
                    <Nav>
                        <Nav.Link href="/">Sleep Log</Nav.Link>
                        {usersLink}
                        <Nav.Link href={userUrl}>Profile</Nav.Link>
                        <Nav.Link href="#about">About</Nav.Link>
                    </Nav>
                    <Form inline="true">
                        <Nav.Link onClick={onClickLogout}>Logout</Nav.Link>
                    </Form>
                </Container>
            </Navbar>
            <Route exact path="/" render={() => <SleepList/>}/>
            <Route exact path="/users" render={() => <UserList/>}/>
            <Route exact path="/about" render={() => <About/>}/>
            <Route exact path="/users/:username/edit" component={EditUser}/>
            <Route exact path="/users/:username/sleep/:sleepId/edit" component={EditSleep}/>
        </HashRouter>
    );


}

export default App;
