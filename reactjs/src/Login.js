import React, {useState} from 'react';

import Button from "react-bootstrap/Button";

import {REACT_APP_API_VERSION, VERSION_HEADER} from "./Constants";
import useCurrentUser from "./useCurrentUser";
import useHttpError from "./useHttpError";
import {GET} from "./BasicHeaders";
import copy from "./Copier";


function getWithCreds(url, credentials) {

    const encoded = btoa(credentials.username + ":" + credentials.password);

    let authGet = copy(GET);
    authGet.headers['Authorization'] = 'Basic ' + encoded;

    return fetch(url, authGet);
}

// login needs to be a component in the router for history to be passed here
function Login({history}) {

    // local form state
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const {onLogin} = useCurrentUser();
    const {throwOnHttpError} = useHttpError();

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event, callback) {
        if(event.key === 'Enter') {
            callback();
        }
    }

    const loginUrl = '/login';

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
                history.push("/");

                // Check current client version and if out of date do a hard refresh.
                // If someone logs out and attempts to log in later, this gives us a good boundary to update the client.
                const clientApiVersion = REACT_APP_API_VERSION;
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

        <div className="login container-sm mt-5">

            <form>
                <div className="mb-3">
                    <label htmlFor="inputUsername" className="form-label">Username</label>
                    <input type="email" className="form-control" id="inputUsername" aria-describedby="emailHelp"
                           placeholder="Username"
                           value={username}
                           onChange={e => setUsername(e.target.value)}
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="inputPassword" className="form-label">Password</label>
                    <input type="password" className="form-control" id="inputPassword" placeholder="Password"
                               value={password}
                               onChange={e => setPassword(e.target.value)}
                               onKeyPress={e => callOnEnter(e, onClickLogin)}
                    />
                </div>
                <Button variant="primary" onClick={() => onClickLogin() }>Login</Button>
            </form>

        </div>
    );
}

export default Login;
