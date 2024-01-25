import React, {useState} from 'react';

import Button from "react-bootstrap/Button";

import {VITE_API_VERSION, VERSION_HEADER} from "./utility/Constants";
import useCurrentUser from "./hooks/useCurrentUser";
import useHttpError from "./hooks/useHttpError";
import {useNavigate} from 'react-router-dom';
import {GET} from "./utility/BasicHeaders";
import Container from "react-bootstrap/Container";


function getWithCreds(url, credentials) {

    const encoded = btoa(credentials.username + ":" + credentials.password);

    let authGet = structuredClone(GET);
    authGet.headers['Authorization'] = 'Basic ' + encoded;

    return fetch(url, authGet);
}

function Login() {

    const navigate = useNavigate();

    // local form state
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [rememberMe, setRememberMe] = useState(true);

    const {onLogin} = useCurrentUser();
    const {throwOnHttpError} = useHttpError();

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event, callback) {
        if(event.key === 'Enter') {
            callback();
        }
    }

    const loginUrl = '/api/login?remember-me=' + (rememberMe ? 'true' : 'false');

    const onClickLogin = () => {

        getWithCreds(loginUrl, { username, password })
            .then(throwOnHttpError)
            .then(response =>
                response.json().then(data => ({
                    user: data,
                    status: response.status,
                    headers: response.headers
                }))
            )
            .then(response => {

                onLogin(response.user);
                navigate("/");

                // Check current client version and if out of date do a hard refresh.
                // If someone logs out and attempts to log in later, this gives us a good boundary to update the client.
                const clientApiVersion = VITE_API_VERSION;
                const serverApiVersion = response.headers.get(VERSION_HEADER);
                if(clientApiVersion !== serverApiVersion) {
                    window.location.reload(true);
                }
            })
            .catch(error => {
                console.log(error);
            });
    }

    return (

        <Container className="login container-sm mt-5">


            <Container className="ps-0 mb-3">
                    <label htmlFor="inputUsername" className="form-label">Username</label>
                    <input type="email" className="form-control" id="inputUsername" aria-describedby="emailHelp"
                           placeholder="Username"
                           value={username}
                           onChange={e => setUsername(e.target.value)}
                    />
            </Container>
            <Container className="ps-0 mb-3">
                    <label htmlFor="inputPassword" className="form-label">Password</label>
                    <input type="password" className="form-control" id="inputPassword" placeholder="Password"
                               value={password}
                               onChange={e => setPassword(e.target.value)}
                               onKeyPress={e => callOnEnter(e, onClickLogin)}
                    />
            </Container>
            <Container className="ps-0 mb-3">
                    <label htmlFor="rememberMe" className="form-label">Remember Me</label>
                    <input className="form-check-input mx-3 p-2" type="checkbox"  id="rememberMe"
                           placeholder="Remember Me"
                           checked={rememberMe}
                           onChange={e => setRememberMe(e.target.checked)}
                           onKeyPress={e => callOnEnter(e, onClickLogin)}
                    />
            </Container>

            <Button variant="primary" onClick={() => onClickLogin() }>Login</Button>


        </Container>
    );
}

export default Login;
