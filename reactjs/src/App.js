import React from 'react';

import './App.css';
import NavBar from 'react-bootstrap/NavBar';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';

import { HashRouter, Route } from 'react-router-dom';

import Home from './Home.js';
import ItemList from './ItemList.js';
import StoreList from './StoreList.js';
import UserList from './UserList.js';
import About from './About.js';
import EditUser from './EditUser.js';
import Login from './Login.js';
import ErrorBoundary from './ErrorBoundary.js';

import {ErrorProvider} from './ErrorContext.js';
import ErrorModal from "./ErrorModal";
import useCurrentUser from "./useCurrentUser";
import {CurrentUserContext, CurrentUserProvider} from "./CurrentUserContext";
import {basicHeader} from "./BasicAuth";

// I think this only has to be imported on any one page, and it works for the whole site
import 'bootstrap/dist/css/bootstrap.min.css';

import Container from "react-bootstrap/Container";

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
            <NavBar bg="dark" variant="dark">
                <Container>
                    <NavBar.Brand>ZDD Demo</NavBar.Brand>
                    <Nav className="mr-auto" />
                    <Form inline>
                        <Nav.Link href="#login">Login</Nav.Link>
                    </Form>
                </Container>
            </NavBar>
            <Route exact path="/login" component={Login} />
        </HashRouter>
    );
}


function AuthenticatedApp() {

    const {currentUser, hasAdmin, onLogout} = useCurrentUser();

    const userUrl = "#/users/"+currentUser.username+"/edit";

    function onClickLogout() {

        const request = {
            headers: basicHeader(),
            method: 'GET'
        };

        fetch("/logout", request)
            .then(response => onLogout());
    }

    if(hasAdmin()) {
        return (
            <HashRouter>
                <NavBar bg="dark" variant="dark">
                    <Container>
                        <NavBar.Brand>ZDD Demo</NavBar.Brand>
                        <Nav>
                            <Nav.Link href="/">Home</Nav.Link>
                            <Nav.Link href="#users">Users</Nav.Link>
                            <Nav.Link href="#stores">Stores</Nav.Link>
                            <Nav.Link href="#items">Items</Nav.Link>
                            <Nav.Link href={userUrl}>Profile</Nav.Link>
                            <Nav.Link href="#about">About</Nav.Link>
                        </Nav>
                        <Form inline>
                            <Nav.Link onClick={onClickLogout}>Logout</Nav.Link>
                        </Form>
                    </Container>
                </NavBar>
                <Route exact path="/" render={() => <Home/>}/>
                <Route exact path="/users" render={() => <UserList/>}/>
                <Route exact path="/stores" render={() => <StoreList/>}/>
                <Route exact path="/items" render={() => <ItemList/>}/>
                <Route exact path="/about" render={() => <About/>}/>
                <Route exact path="/users/:username/edit" component={EditUser}/>
            </HashRouter>
        );
    }
    else {
        return (
            <HashRouter>
                <NavBar bg="dark" variant="dark">
                    <Container>
                        <NavBar.Brand>ZDD Demo</NavBar.Brand>
                        <Nav className="mr-auto">
                            <Nav.Link href="/">Home</Nav.Link>
                            <Nav.Link href="#items">Items</Nav.Link>
                            <Nav.Link href="#about">About</Nav.Link>
                            <Nav.Link href={userUrl}>Profile</Nav.Link>
                        </Nav>
                        <Form inline>
                            <Nav.Link onClick={onLogout}>Logout</Nav.Link>
                        </Form>
                    </Container>
                </NavBar>
                <Route exact path="/" render={() => <Home/>}/>
                <Route exact path="/items" render={() => <ItemList/>}/>
                <Route exact path="/about" render={() => <About/>}/>
                <Route exact path="/users/:username/edit" component={EditUser}/>
            </HashRouter>
        );
    }

}

export default App;
