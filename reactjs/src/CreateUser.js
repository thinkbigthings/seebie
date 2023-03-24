import React, {useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

import useApiPost from "./useApiPost";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";

const blankData = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
}

function CreateUser(props) {

    const post = useApiPost();
    const [showCreateUser, setShowCreateUser] = useState(false);
    const [user, setUser] = useState(blankData);

    const saveData = (userData) => {

        const registrationRequest = {
            username: userData.username,
            plainTextPassword: userData.password,
            email: userData.email
        }

        const requestBody = typeof registrationRequest === 'string' ? registrationRequest : JSON.stringify(registrationRequest);

        post('/registration', requestBody)
            .then(result => setShowCreateUser(false))
            .then(props.onSave);
    }


    function updateUser(updateValues) {
        let updatedUser = {...user, ...updateValues};

        // username should not need to be url encoded
        let valid = encodeURIComponent(updatedUser.username) === updatedUser.username;

        if(valid) {
            setUser( updatedUser );
        }
    }

    function onHide() {
        setUser(blankData);
        setShowCreateUser(false);
    }

    function onConfirm() {
        setUser(blankData);
        saveData({...user, displayName: user.username});
    }

    const passwordReady = (user.password === user.confirmPassword && user.password !== '');

    return (
        <>
            <Button variant="secondary" onClick={() => setShowCreateUser(true)}>
                <FontAwesomeIcon className="me-2" icon={faPlus} />
                Add
            </Button>

            <Modal show={showCreateUser} onHide={onHide} >
                <Modal.Header closeButton>
                    <Modal.Title>Create User</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">Username</label>
                        <input type="text" className="form-control" id="username" placeholder="Username"
                               value={user.username}
                               onChange={e => updateUser({username : e.target.value })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input type="text" className="form-control" id="email" placeholder="Email"
                               value={user.email}
                               onChange={e => updateUser({email : e.target.value })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input type="password"  className="form-control" id="password" placeholder="Password"
                               value={user.password}
                               onChange={e => updateUser({password : e.target.value })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
                        <input type="password"  className="form-control" id="confirmPassword" placeholder="Confirm Password"
                               value={user.confirmPassword}
                               onChange={e => updateUser({confirmPassword : e.target.value })} />
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onHide}>Cancel</Button>
                    <Button variant="primary" onClick={onConfirm} disabled={!passwordReady}>Save</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default CreateUser;
