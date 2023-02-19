import React, {useEffect, useState} from 'react';

import {UserForm} from './UserForm.js';
import ResetPasswordModal from "./ResetPasswordModal.js";

import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import useCurrentUser from "./useCurrentUser";
import useApiPost from "./useApiPost";
import useApiPut from "./useApiPut";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faKey} from "@fortawesome/free-solid-svg-icons";
import {GET} from "./BasicHeaders";

const blankUser = {
    username: '',
    roles: [],
    registrationTime: '',
    personalInfo: {
        displayName: '',
        email: '',
        addresses: [],
    }
}

function EditUser({history, match}) {

    const { params: { username } } = match;

    const userEndpoint = '/user/' + username;
    const userInfoEndpoint = userEndpoint + '/personalInfo';
    const updatePasswordEndpoint = userEndpoint + '/password/update'

    const [data, setData] = useState(blankUser);

    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        fetch(userEndpoint, GET)
            .then(response => response.json())
            .then(setData)
            .then(() => setLoaded(true))
    }, [setData, userEndpoint, username]);


    // update user info stuff

    const put = useApiPut();
    const onSave = (personalInfo) => {
        put(userInfoEndpoint, personalInfo).then(history.goBack);
    }


    // password reset stuff

    const [showResetPassword, setShowResetPassword] = useState(false);
    const {currentUser, onLogin} = useCurrentUser();
    const post = useApiPost();

    const onResetPassword = (plainTextPassword) => {
        post(updatePasswordEndpoint, plainTextPassword)
            .then(result => {
                if(currentUser.username === username) {
                    onLogin({...currentUser, password: plainTextPassword});
                }
                setShowResetPassword(false);
            });
    }

    return (
        <div className="container mt-3">
            <h1>User Profile</h1>

            <Button variant="warning" onClick={() => setShowResetPassword(true)}>
                <FontAwesomeIcon className="me-2" icon={faKey} />
                Reset Password
            </Button>

            <ResetPasswordModal show={showResetPassword} onConfirm={onResetPassword} onHide={() => setShowResetPassword(false)} />

            <Container id="userFormWrapper" className="pl-0 pr-0">
                {loaded ? <UserForm onCancel={history.goBack} onSave={onSave} initData={data}/> : <div />}
            </Container>
        </div>
    );
}

export default EditUser;
