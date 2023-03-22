import React, {useState} from 'react';

import Form from 'react-bootstrap/Form';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

import {HashRouter, Route} from 'react-router-dom';

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
import Home from "./Home";
import {NavDropdown} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faChartLine, faCog, faList, faServer, faSignOut, faUser, faUsers} from "@fortawesome/free-solid-svg-icons";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {CreateSleepSession} from "./CreateSleepSession";
import SleepList from "./SleepList";
import SleepChart from "./SleepChart";
import Button from "react-bootstrap/Button";

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
            <Navbar className="border-bottom mb-3">
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

    // Refresh data view if a sleep session is logged that affects the view
    let [createdCount, setCreatedCount] = useState(0);

    function onClickLogout() {
        fetch("/logout", GET)
            .then(response => onLogout());
    }

    const loggedIn = <span>
        <FontAwesomeIcon className="me-2" icon={faUser} ></FontAwesomeIcon>
        {currentUser.personalInfo.displayName}
    </span> ;

    return (
        <HashRouter>
            <Navbar className="border-bottom mb-3">
                <Container>
                    <Navbar.Brand href="/">Seebie<img className="mb-1 px-1" src="favicon.ico" alt="Seebie icon" width="30" height="20"/></Navbar.Brand>
                    <CreateSleepSession onSave={() => setCreatedCount(createdCount + 1)} />
                    <NavDropdown title={loggedIn } align="end" flip id="userDropdown">
                        <NavDropdown.Item href={userUrl}>{<FontAwesomeIcon className="me-2" icon={faCog} />}Profile</NavDropdown.Item>
                        <NavDropdown.Divider />
                        <NavDropdown.Item onClick={onClickLogout}>{<FontAwesomeIcon className="me-2" icon={faSignOut} />}Logout</NavDropdown.Item>
                    </NavDropdown>
                </Container>
            </Navbar>
            <Container>
                <Row>
                    <Col className="col-md-auto col-sm-3">
                        <SideBar hasAdmin={hasAdmin()} />
                    </Col>
                    <Col>
                        <Route exact path="/list" render={()  => <SleepList reloadCount = {createdCount} />}/>
                        <Route exact path="/chart" render={() => <SleepChart reloadCount = {createdCount} />}/>
                        <Route exact path="/users" render={() => <UserList/>}/>
                        <Route exact path="/users/:username/edit" component={EditUser}/>
                        <Route exact path="/users/:username/sleep/:sleepId/edit" component={EditSleep}/>
                    </Col>
                </Row>
            </Container>


        </HashRouter>
    );
}

function SideBar(props) {

    const {hasAdmin} = props;

    let usersLink = hasAdmin
        ?   <li className="nav-item">
                <a className="nav-link active" aria-current="page" href="#/users">
                    <FontAwesomeIcon className="me-2" icon={faUsers} />Users
                </a>
            </li>
        :  "";

    let systemLink = hasAdmin
        ?   <li className="nav-item">
                <a className="nav-link" href="#/system">
                    <FontAwesomeIcon className="me-2" icon={faServer} />System
                </a>
            </li>
        :  "";

    return (

            <ul className="nav flex-column d-inline-block">
                <li className="nav-item">
                    <a className="nav-link" href="#/list">
                        <FontAwesomeIcon className="me-2" icon={faList} />List
                    </a>
                </li>
                <li className="nav-item">
                    <a className="nav-link" href="#/chart">
                        <FontAwesomeIcon className="me-2" icon={faChartLine} />Chart
                    </a>
                </li>
                {usersLink}
                {systemLink}
            </ul>

    );
}

export default App;
