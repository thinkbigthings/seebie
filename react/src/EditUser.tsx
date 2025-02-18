import React, {useEffect, useState} from 'react';

import {UserForm} from './UserForm.jsx';
import ResetPasswordModal from "./ResetPasswordModal.jsx";

import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import useCurrentUser from "./hooks/useCurrentUser";
import useApiPost from "./hooks/useApiPost";
import useApiPut from "./hooks/useApiPut";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faKey} from "@fortawesome/free-solid-svg-icons";
import {GET} from "./utility/BasicHeaders";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";
import {blankUser} from "./utility/CurrentUserContext";
import {PersonalInfo, PasswordResetRequest} from "./types/user.types";


function EditUser() {

    const navigate = useNavigate();

    const {publicId} = useParams();

    const userEndpoint = `/api/user/${publicId}`;
    const userInfoEndpoint = `${userEndpoint}/personalInfo`;
    const updatePasswordEndpoint = `${userEndpoint}/password/update`;

    const [data, setData] = useState(blankUser);

    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        fetch(userEndpoint, GET)
            .then(response => response.json())
            .then(setData)
            .then(() => setLoaded(true))
    }, [setData, userEndpoint, publicId]);

    const put = useApiPut();
    const onSave = (personalInfo: PersonalInfo) => {
        put(userInfoEndpoint, personalInfo).then(() => navigate(-1));
    }

    const [showResetPassword, setShowResetPassword] = useState(false);
    const {currentUser, onLogin} = useCurrentUser();
    const post = useApiPost();

    const onResetPassword = (plainTextPassword: string) => {
        post(updatePasswordEndpoint, {plainTextPassword})
            .then(result => {
                if(currentUser.publicId === publicId) {
                    onLogin({...currentUser});
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
