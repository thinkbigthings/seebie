// @ts-nocheck
import React from 'react';

import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";

import useError from "./hooks/useError";
import {recoveryActions} from "./utility/ErrorContext";
import useCurrentUser from "./hooks/useCurrentUser";

function ErrorModal(props) {

    const { error, clearError } = useError();
    const {onLogout} = useCurrentUser();

    function refreshLogin() {
        onLogout();
        clearError();
        window.location.replace('/#/login');
        window.location.reload(true)
    }

    const displayLogin = error.recoveryAction === recoveryActions.LOGIN;
    const displayReload = error.recoveryAction === recoveryActions.RELOAD;
    const displayLoginStyle = displayLogin ? '' : 'd-none';
    const displayReloadStyle = displayReload ? '' : 'd-none';

    return (
        <Modal show={error.hasError} onHide={clearError} backdrop="static" centered>
            <Modal.Header closeButton>
                <Modal.Title>
                    Warning
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Alert variant='warning'>
                    {error.message}
                </Alert></Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={clearError}>
                    Cancel
                </Button>
                <Button className={displayLoginStyle} variant="primary" onClick={ refreshLogin }>
                    Login
                </Button>
                <Button className={displayReloadStyle} variant="primary" onClick={ () => window.location.reload(true)}>
                    Reload
                </Button>
            </Modal.Footer>
        </Modal>
    );

}

export default ErrorModal;
