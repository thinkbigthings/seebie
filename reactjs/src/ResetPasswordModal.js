import React, {useState} from 'react';

import Alert from "react-bootstrap/Alert";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";


// expected props: show, onHide, onConfirm
// https://react-bootstrap.github.io/components/modal/
function ResetPasswordModal(props) {

    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');


    function clearForm() {
        setPassword("");
        setConfirmPassword("");
    }

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event, callback) {
        if(event.key === 'Enter') {
            callback();
        }
    }

    function onHide() {
        clearForm();
        props.onHide();
    }

    function onConfirm() {
        clearForm()
        props.onConfirm(password);
    }

    const passwordReady = password === confirmPassword && password !== '';

    return (

        <Modal show={props.show} onHide={onHide} >
            <Modal.Header closeButton>
                <Modal.Title>Reset Password</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Alert variant="warning">
                    This resets the password immediately. Use with Caution!
                </Alert>
                <div className="mb-3">
                    <label htmlFor="password" className="form-label">  New Password</label>
                    <input type="password"  className="form-control" id="password" placeholder="Password"
                           value={password}
                           onChange={e => setPassword(e.target.value)} />
                </div>
                <div className="mb-3">
                    <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
                    <input type="password"  className="form-control" id="confirmPassword" placeholder="Confirm Password"
                           value={confirmPassword}
                           onChange={e => setConfirmPassword(e.target.value)}
                           onKeyPress={e => callOnEnter(e, onConfirm) }/>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>Close</Button>
                <Button variant="warning" onClick={onConfirm} disabled={!passwordReady}>Save</Button>
            </Modal.Footer>
        </Modal>

    );
}

export default ResetPasswordModal;
