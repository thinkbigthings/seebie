import React from 'react';

import Form from 'react-bootstrap/Form';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

import { HashRouter, Route } from 'react-router-dom';

import UserList from './UserList.js';
import EditUser from './EditUser.js';
import Login from './Login.js';
import ErrorBoundary from './ErrorBoundary.js';

import {ErrorProvider} from './ErrorContext.js';
import ErrorModal from "./ErrorModal";
import useCurrentUser from "./useCurrentUser";
import {CurrentUserContext, CurrentUserProvider} from "./CurrentUserContext";
import {GET} from "./BasicHeaders";

// This only has to be imported on one page, and it works for the whole site
// import custom css after bootstrap so that customizations takes precedence
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import Container from "react-bootstrap/Container";
import EditSleep from "./EditSleep";
import SleepData from "./SleepData";
import Home from "./Home";
import {NavDropdown} from "react-bootstrap";

function App() {

    return (
        <div>
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
                    <Navbar.Brand href="/">Seebie<img className="mb-1 px-1" src="favicon.ico" alt="Seebie icon" width="30" height="20"/></Navbar.Brand>
                    <Nav className="mr-auto" />
                    <Form inline="true">
                        <Nav.Link href="#login">Login</Nav.Link>
                    </Form>
                </Container>
            </Navbar>
            <Route exact path="/" render={() => <Home />}/>
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
                    <Navbar.Brand href="/">Seebie<img className="mb-1 px-1" src="favicon.ico" alt="Seebie icon" width="30" height="20"/></Navbar.Brand>
                    <Nav>
                        {usersLink}
                    </Nav>
                    <NavDropdown title={currentUser.personalInfo.displayName} id="userDropdown">
                        <NavDropdown.Item href={userUrl}>Profile</NavDropdown.Item>
                        <NavDropdown.Divider />
                        <NavDropdown.Item onClick={onClickLogout}>Logout</NavDropdown.Item>
                    </NavDropdown>
                </Container>
            </Navbar>
            <Route exact path="/" render={() => <SleepData/>}/>
            <Route exact path="/users" render={() => <UserList/>}/>
            <Route exact path="/users/:username/edit" component={EditUser}/>
            <Route exact path="/users/:username/sleep/:sleepId/edit" component={EditSleep}/>
        </HashRouter>
    );
}

export default App;
