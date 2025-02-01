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
    faCog, faHexagonNodes,
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
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import Chat from "./Chat.tsx";

const SHOW_CHAT = true;

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
                    <Form>
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
    const userUrl = "#/users/"+currentUser.publicId+"/edit";
    const loginUrl = "#/login";

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

                    <CreateSleepSession publicId={currentUser.publicId} onSave={() => setCreatedCount(createdCount + 1)} />

                    <NavDropdown className={"nav-link-colored"} title={loggedIn } align="end" id="userDropdown">
                        <NavDropdown.Item className={"nav-link-colored"} href={userUrl}>{<FontAwesomeIcon className="me-2" icon={faCog} />}Profile</NavDropdown.Item>
                        <NavDropdown.Divider />
                        <NavDropdown.Item className={"nav-link-colored"} href={loginUrl} onClick={onClickLogout}>{<FontAwesomeIcon className="me-2" icon={faSignOut} />}Logout</NavDropdown.Item>
                    </NavDropdown>

                </Container>
            </Navbar>
            <Container className="d-flex">

                <SideNav hasAdmin={hasAdmin()} publicId={currentUser.publicId}/>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/users/:publicId/sleep/list" element={<SleepList createdCount={createdCount} />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/users/:publicId/challenge" element={<Challenge />} />
                    <Route path="/users/:publicId/sleep/chart" element={<SleepChart createdCount={createdCount} />} />
                    <Route path="/users/:publicId/histogram" element={<Histogram createdCount = {createdCount} />} />
                    <Route path="/users" element={<UserList/>} />
                    <Route path="/users/:publicId/edit" element={<EditUser />} />
                    <Route path="/users/:publicId/sleep/:sleepId/edit" element={<EditSleep />} />
                    <Route path="/users/:publicId/challenge/:challengeId/edit" element={<EditChallenge />} />
                    <Route path="/users/:publicId/tools" element={<Tools />} />
                    {SHOW_CHAT
                        ? <Route path="/users/:publicId/chat" element={<Chat />} />
                        : null}
                </Routes>
            </Container>

        </HashRouter>
    );
}

function SideNav(props: {hasAdmin:boolean, publicId:string}) {

    const {hasAdmin, publicId} = props;
    let usersNav = hasAdmin ? <NavItem name="Users" icon={faUsers} href="#/users" /> : "";

    return (
        <Nav defaultActiveKey="/home" className="flex-column col-sm-2">
            <NavItem name="Challenge" icon={faTrophy} href={`#/users/${publicId}/challenge`} />
            <NavItem name="List" icon={faList} href={"#/users/"+publicId+"/sleep/list" } />
            <NavItem name="Chart" icon={faChartLine} href={"#/users/"+publicId+"/sleep/chart" } />
            <NavItem name="Analysis" icon={faChartSimple} href={"#/users/"+publicId+"/histogram"} />
            <NavItem name="Tools" icon={faTools} href={"#/users/"+publicId+"/tools"} />
            {SHOW_CHAT
                ? <NavItem name="Chat" icon={faHexagonNodes} href={"#/users/"+publicId+"/chat"} />
                : null}
            {usersNav}
        </Nav>
    );
}

function NavItem(props: {name:string, href:string, icon:IconDefinition}) {

    const {name, href, icon} = props;

    // Hidden only on xs 	.d-none .d-sm-block
    // Visible only on xs 	.d-block .d-sm-none

    return (
        <Nav.Link  eventKey={name+"Link"} href={href} className="ps-0 pt-0 pb-4">
            <FontAwesomeIcon  className="me-2 fa-lg" icon={icon} /><div className="d-none d-sm-inline-block">{name}</div>
        </Nav.Link>
    );
}

function NavHeader(props: {title:string, children?:React.ReactNode}) {

    const {title} = props;

    return(
        <Container className="px-0 pb-3 d-flex" >
            <h1 className="mb-0 flex-grow-1">{title}</h1>
            {props.children}
        </Container>
    );
}

export {App, NavHeader};
