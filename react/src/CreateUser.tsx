import React, {useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

import useApiPost from "./hooks/useApiPost";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {RegistrationRequest, UserFormFields} from "./types/user.types";


const blankData: UserFormFields = {
    displayName: '',
    email: '',
    password: '',
    confirmPassword: ''
}

function CreateUser(props: {onSave: () => void} ){

    const post = useApiPost();
    const [showCreateUser, setShowCreateUser] = useState(false);
    const [user, setUser] = useState(blankData);

    const saveData = (userData:UserFormFields) => {

        const registrationRequest: RegistrationRequest = {
            displayName: userData.displayName,
            plainTextPassword: userData.password,
            email: userData.email
        }

        post('/api/registration', registrationRequest)
            .then(result => setShowCreateUser(false))
            .then(props.onSave);
    }


    function updateUser(updateValues:Partial<UserFormFields>) {

        let updatedUser:UserFormFields = {...user, ...updateValues};
        setUser( updatedUser );
    }

    function onHide() {
        setUser(blankData);
        setShowCreateUser(false);
    }

    function onConfirm() {
        setUser(blankData);
        saveData(user);
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
                        <label htmlFor="displayName" className="form-label">Display Name</label>
                        <input type="text" className="form-control" id="displayName" placeholder="Display Name"
                               value={user.displayName}
                               onChange={e => updateUser({displayName : e.target.value })} />
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
