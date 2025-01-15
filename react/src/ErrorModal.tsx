import React from 'react';

import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";

import useError from "./hooks/useError";
import {RecoveryActions} from "./utility/ErrorContext";
import useCurrentUser from "./hooks/useCurrentUser";

function ErrorModal() {

    const { error, clearError } = useError();
    const {onLogout} = useCurrentUser();

    function refreshLogin() {
        onLogout();
        clearError();
        window.location.replace('/#/login');
        window.location.reload()
    }

    const displayLogin = error.errorStatus.recoveryAction === RecoveryActions.LOGIN;
    const displayReload = error.errorStatus.recoveryAction === RecoveryActions.RELOAD;
    const displayLoginStyle = displayLogin ? '' : 'd-none';
    const displayReloadStyle = displayReload ? '' : 'd-none';
    const displayCancelStyle = displayLogin ? 'd-none' : '';

    return (
        <Modal show={error.errorStatus.hasError} onHide={clearError} backdrop="static" centered>
            <Modal.Header closeButton>
                <Modal.Title>
                    Warning
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Alert variant='warning'>
                    {error.errorStatus.message}
                </Alert></Modal.Body>
            <Modal.Footer>
                <Button className={displayCancelStyle} variant="secondary" onClick={clearError}>
                    Cancel
                </Button>
                <Button className={displayLoginStyle} variant="primary" onClick={ refreshLogin }>
                    Login
                </Button>
                <Button className={displayReloadStyle} variant="primary" onClick={ () => window.location.reload()}>
                    Reload
                </Button>
            </Modal.Footer>
        </Modal>
    );

}

export default ErrorModal;
