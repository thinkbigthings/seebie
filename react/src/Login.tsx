import React, {KeyboardEvent, useState} from 'react';

import Button from "react-bootstrap/Button";

import useCurrentUser from "./hooks/useCurrentUser";
import useHttpError from "./hooks/useHttpError";
import {useNavigate} from 'react-router-dom';
import {GET} from "./utility/BasicHeaders";
import Container from "react-bootstrap/Container";

interface Credentials {
    email: string;
    password: string;
}

function getWithCreds(url: string, credentials: Credentials) {

    const encoded = btoa(credentials.email + ":" + credentials.password);

    let authGet = structuredClone(GET);
    authGet.headers['Authorization'] = 'Basic ' + encoded;

    return fetch(url, authGet);
}

function Login() {

    const navigate = useNavigate();

    // local form state
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [rememberMe, setRememberMe] = useState(true);

    const {onLogin} = useCurrentUser();
    const {throwOnHttpError} = useHttpError();

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event: KeyboardEvent) {
        if(event.key === 'Enter') {
            onClickLogin();
        }
    }

    const loginUrl = '/api/login?remember-me=' + (rememberMe ? 'true' : 'false');

    const onClickLogin = () => {

        getWithCreds(loginUrl, { email: email, password })
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

            })
            .catch(error => {
                console.log(error);
            });
    }

    return (

        <Container className="login container-sm mt-5">


            <Container className="ps-0 mb-3">
                    <label htmlFor="inputEmail" className="form-label">Email</label>
                    <input type="email" className="form-control" id="inputEmail" aria-describedby="emailHelp"
                           autoFocus={true}
                           placeholder="Email"
                           value={email}
                           onChange={e => setEmail(e.target.value)}
                    />
            </Container>
            <Container className="ps-0 mb-3">
                    <label htmlFor="inputPassword" className="form-label">Password</label>
                    <input type="password" className="form-control" id="inputPassword" placeholder="Password"
                               value={password}
                               onChange={e => setPassword(e.target.value)}
                               onKeyPress={e => callOnEnter(e)}
                    />
            </Container>
            <Container className="ps-0 mb-3">
                    <label htmlFor="rememberMe" className="form-label">Remember Me</label>
                    <input className="form-check-input mx-3 p-2" type="checkbox"  id="rememberMe"
                           placeholder="Remember Me"
                           checked={rememberMe}
                           onChange={e => setRememberMe(e.target.checked)}
                           onKeyUp={e => callOnEnter(e)}
                    />
            </Container>

            <Button variant="primary" onClick={() => onClickLogin() }>Login</Button>


        </Container>
    );
}

export default Login;
