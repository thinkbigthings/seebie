// @ts-nocheck
import React, {useState} from 'react';

import Form from 'react-bootstrap/Form';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

import {HashRouter, Route, Routes} from 'react-router-dom';

import UserList from './UserList.jsx';
import EditUser from './EditUser.jsx';
import Login from './Login.jsx';
import ErrorBoundary from './ErrorBoundary.jsx';

import ErrorModal from "./ErrorModal";
import useCurrentUser from "./hooks/useCurrentUser";
import {CurrentUserContext} from "./utility/CurrentUserContext";
import {GET} from "./utility/BasicHeaders";

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
    faChartLine,
    faChartSimple,
    faCog,
    faList,
    faMoon,
    faServer,
    faSignOut,
    faTools,
    faTrophy,
    faUser,
    faUsers
} from "@fortawesome/free-solid-svg-icons";
import {CreateSleepSession} from "./CreateSleepSession";
import SleepList from "./SleepList";
import {SleepChart} from "./SleepChart";
import Tools from "./Tools";
import Challenge from "./Challenge";
import Histogram from "./Histogram";
import EditChallenge from "./EditChallenge";
import {CurrentUserProvider} from "./CurrentUserProvider";
import {ErrorProvider} from "./ErrorProvider";

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
                    <Navbar.Brand href="/">
                        Seebie
                        <FontAwesomeIcon className="app-highlight ms-2" icon={faMoon} />
                    </Navbar.Brand>
                    <Nav className="mr-auto" />
                    <Form inline="true">
                        <Nav.Link href="#login">Login</Nav.Link>
                    </Form>
                </Container>
            </Navbar>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
            </Routes>
        </HashRouter>
    );
}


function AuthenticatedApp() {

    const {currentUser, hasAdmin, onLogout} = useCurrentUser();

    const userUrl = "#/users/"+currentUser.username+"/edit";

    // Refresh data view if a sleep session is logged that affects the view
    let [createdCount, setCreatedCount] = useState(0);

    function onClickLogout() {
        fetch("/api/logout", GET)
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
                    <Navbar.Brand href="/">
                        Seebie
                        <FontAwesomeIcon className="app-highlight ms-2" icon={faMoon} />
                    </Navbar.Brand>

                    <CreateSleepSession username={currentUser.username} onSave={() => setCreatedCount(createdCount + 1)} />

                    <NavDropdown className={"nav-link-colored"} title={loggedIn } align="end" flip="true" id="userDropdown">
                        <NavDropdown.Item className={"nav-link-colored"} href={userUrl}>{<FontAwesomeIcon className="me-2" icon={faCog} />}Profile</NavDropdown.Item>
                        <NavDropdown.Divider />
                        <NavDropdown.Item className={"nav-link-colored"} onClick={onClickLogout}>{<FontAwesomeIcon className="me-2" icon={faSignOut} />}Logout</NavDropdown.Item>
                    </NavDropdown>

                </Container>
            </Navbar>
            <Container className="d-flex">

                <SideNav hasAdmin={hasAdmin()} username={currentUser.username}/>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/users/:username/sleep/list" element={<SleepList createdCount={createdCount} />} />
                    <Route path="/login" element={<div />} />
                    <Route path="/users/:username/challenge" element={<Challenge createdCount = {createdCount} />} />
                    <Route path="/users/:username/sleep/chart" element={<SleepChart createdCount={createdCount} />} />
                    <Route path="/users/:username/histogram" element={<Histogram createdCount = {createdCount} />} />
                    <Route path="/users" element={<UserList/>} />
                    <Route path="/users/:username/edit" element={<EditUser />} />
                    <Route path="/users/:username/sleep/:sleepId/edit" element={<EditSleep />} />
                    <Route path="/users/:username/challenge/:challengeId/edit" element={<EditChallenge />} />
                    <Route path="/users/:username/tools" element={<Tools />} />
                </Routes>
            </Container>

        </HashRouter>
    );
}

function SideNav(props) {

    const {hasAdmin, username} = props;
    let usersNav = hasAdmin ? <NavItem name="Users" icon={faUsers} href="#/users" /> : "";

    return (
        <Nav defaultActiveKey="/home" className="flex-column col-sm-2">
            <NavItem name="Challenge" icon={faTrophy} href={`#/users/${username}/challenge`} />
            <NavItem name="List" icon={faList} href={"#/users/"+username+"/sleep/list" } />
            <NavItem name="Chart" icon={faChartLine} href={"#/users/"+username+"/sleep/chart" } />
            <NavItem name="Analysis" icon={faChartSimple} href={"#/users/"+username+"/histogram"} />
            <NavItem name="Tools" icon={faTools} href={"#/users/"+username+"/tools"} />
            {usersNav}
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
