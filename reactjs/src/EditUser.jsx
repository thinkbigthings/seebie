import React, {useEffect, useState} from 'react';

import {UserForm} from './UserForm.jsx';
import ResetPasswordModal from "./ResetPasswordModal.jsx";

import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import useCurrentUser from "./useCurrentUser";
import useApiPost from "./useApiPost";
import useApiPut from "./useApiPut";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faKey} from "@fortawesome/free-solid-svg-icons";
import {GET} from "./utility/BasicHeaders";
import {blankUser} from "./CurrentUserContext";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";


function EditUser() {

    const navigate = useNavigate();

    const {username} = useParams();

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
        put(userInfoEndpoint, personalInfo).then(() => navigate(-1));
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
        <Container>

            <NavHeader title="User">
                <Button variant="warning" onClick={() => setShowResetPassword(true)}>
                    <FontAwesomeIcon className="me-2" icon={faKey} />
                    Reset Password
                </Button>
            </NavHeader>

            <ResetPasswordModal show={showResetPassword} onConfirm={onResetPassword} onHide={() => setShowResetPassword(false)} />

            <Container id="userFormWrapper" className="px-0">
                {loaded ? <UserForm onCancel={() => navigate(-1)} onSave={onSave} initData={data}/> : <div />}
            </Container>
        </Container>
    );
}

export default EditUser;
