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
import {
    faBook,
    faChartLine,
    faCog,
    faList,
    faServer,
    faSignOut,
    faTag,
    faTools,
    faUser,
    faUsers
} from "@fortawesome/free-solid-svg-icons";
import {CreateSleepSession} from "./CreateSleepSession";
import SleepList from "./SleepList";
import SleepChart from "./SleepChart";
import System from "./System";

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
            <Container className="d-flex">

                <SideNav hasAdmin={hasAdmin()} />

                <Route exact path="/" render={()  => <SleepList createdCount = {createdCount} />}/>
                <Route exact path="/list" render={()  => <SleepList createdCount = {createdCount} />}/>
                <Route exact path="/chart" render={() => <SleepChart createdCount = {createdCount} />}/>
                <Route exact path="/users" render={() => <UserList/>}/>
                <Route exact path="/users/:username/edit" component={EditUser}/>
                <Route exact path="/users/:username/sleep/:sleepId/edit" component={EditSleep}/>
                <Route exact path="/system" component={System}/>

            </Container>


        </HashRouter>
    );
}

function SideNav(props) {

    const {hasAdmin} = props;
    let usersNav = hasAdmin ? <NavItem name="Users" icon={faUsers} href="#/users" /> : "";
    let systemNav = hasAdmin ? <NavItem name="System" icon={faServer} href="#/system" /> : "";

    return (
        <Nav defaultActiveKey="/home" className="flex-column col-sm-2">
            <NavItem name="List" icon={faList} href="#/list" />
            <NavItem name="Chart" icon={faChartLine} href="#/chart" />
            <NavItem name="Diary" icon={faBook} href="#/diary" />
            <NavItem name="Tags" icon={faTag} href="#/tags" />
            <NavItem name="Tools" icon={faTools} href="#/tools" />
            {usersNav}
            {systemNav}
        </Nav>
    );
}

function NavItem(props) {

    const {name, href, icon} = props;

    // Hidden only on xs 	.d-none .d-sm-block
    // Visible only on xs 	.d-block .d-sm-none

    return (
        <Nav.Link  eventKey={name+"Link"} href={href} className="ps-0 pt-0 pb-4">
            <FontAwesomeIcon  className="me-2 fa-lg" icon={icon} /><div className="d-none d-sm-inline-block">{name}</div>
        </Nav.Link>
    );
}

function NavHeader(props) {

    const {title} = props;

    return(
        <Container className="px-0 pb-3 d-flex" >
            <h1 className="mb-0 flex-grow-1">{title}</h1>
            {props.children}
        </Container>
    );
}

export {App, NavHeader};
