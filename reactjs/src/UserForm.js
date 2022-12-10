import React, {useReducer} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

import copy from './Copier.js';
import Container from "react-bootstrap/Container";

import AddressRow from './AddressRow.js';

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

const blankAddress = {
    line1: '',
    city: '',
    state: '',
    zip: ''
}

const blankEditableAddress = {
    isEditing: false,
    address: blankAddress,
    originatingIndex: 0
}

function UserForm(props) {

    const {onCancel, onSave, userData} = props;

    // return new state based on current state and action
    // reducer itself should not cause side effects, it should be called FROM a side effect
    // possibly combined into a custom hook
    const formReducer = (formState, action) => {

        let newState = copy(formState);
        switch(action.type) {
            case 'START_ADDRESS_EDIT': {
                newState.editableAddress.isEditing = true;
                newState.editableAddress.originatingIndex = action.payload;
                newState.editableAddress.address = newState.user.personalInfo.addresses[action.payload];
                return newState;
            }
            case 'UPDATE_ADDRESS_EDIT': {
                newState.editableAddress.address = {...newState.editableAddress.address, ...action.payload};
                return newState;
            }
            case 'KEEP_ADDRESS_EDIT': {
                const index = newState.editableAddress.originatingIndex;
                const updatedAddress = newState.editableAddress.address;
                newState.editableAddress.isEditing = false;
                newState.user.personalInfo.addresses[index] = {...updatedAddress};
                return newState;
            }
            case 'DISCARD_ADDRESS_EDIT': {
                newState.editableAddress.isEditing = false;
                return newState;
            }
            case 'ADD_ADDRESS': {
                newState.editableAddress.isEditing = true;
                newState.editableAddress.originatingIndex = newState.user.personalInfo.addresses.length;
                newState.editableAddress.address = blankAddress;
                return newState;
            }
            case 'DELETE_ADDRESS': {
                newState.user.personalInfo.addresses.splice(action.payload, 1);
                return newState;
            }
            case 'LOAD_USER': {
                newState.user = action.payload
                return newState;
            }
            case 'UPDATE_USER': {
                newState.user.personalInfo = {...newState.user.personalInfo, ...action.payload};
                return newState;
            }
            default: {
                throw new Error('Unhandled action type ' + action.type);
            }
        }
    }

    const [formState, dispatch] = useReducer(formReducer, {user: userData, editableAddress: blankEditableAddress});

    // the context hook retains its value over re-renders,
    // so need to trigger the update here once the data has been loaded for real
    if(userData.username !== '' && formState.user.username === '') {
        dispatch({type:'LOAD_USER', payload: userData});
    }

    const address = formState.editableAddress.address;

    return (
        <Container id="userFormId" className="mt-5 ps-0 " >

            <form>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputUserName" className="form-label">User Name</label>
                    <input type="email" className="form-control" id="inputUserName" placeholder="User Name"
                           disabled
                           value={formState.user.username}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="registrationTime" className="form-label">Registration Time</label>
                    <input type="text" className="form-control" id="inputRegistrationTime" placeholder="Registration Time"
                           disabled
                           value={formState.user.registrationTime}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="roles" className="form-label">Roles</label>
                    <input type="text" className="form-control" id="roles" placeholder="Roles"
                           disabled
                           value={formState.user.roles}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputDisplayName" className="form-label">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={formState.user.personalInfo.displayName}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {displayName: e.target.value }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="exampleInputEmail1" className="form-label">Email address</label>
                    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           value={formState.user.personalInfo.email}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {email: e.target.value }})}/>
                    <small id="emailHelp" className="form-text text-muted">We'll never share your email with anyone
                        else.</small>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputHeight" className="form-label">Height (cm)</label>
                    <input type="number" className="form-control" id="inputHeight" placeholder="Height"
                           value={formState.user.personalInfo.heightCm}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {heightCm: e.target.value }})}/>
                </Container>

                <hr />

                <div className="container mt-3">

                    <span className="fw-bold me-2">Addresses</span>

                    <Button variant="success"  onClick={() => dispatch({type:'ADD_ADDRESS'})}>
                        <FontAwesomeIcon className="me-2" icon={faPlusCircle} />Add Address
                    </Button>

                    <Container className="container mt-3">
                        {formState.user.personalInfo.addresses.map( (address, index) =>
                            <AddressRow key={index} currentAddress={address}
                                        onEdit={() => dispatch({type:'START_ADDRESS_EDIT', payload: index})}
                                        onDelete={() => dispatch({type:'DELETE_ADDRESS', payload: index})} />
                        )}
                    </Container>
                </div>

                    <Modal show={formState.editableAddress.isEditing} onHide={() => dispatch({type:'DISCARD_ADDRESS_EDIT'})}>
                        <Modal.Header closeButton>
                            <Modal.Title>Edit Address</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>

                            <Container className="mb-3">

                                <label htmlFor="line1" className="form-label">Street Address</label>
                                <input type="text" className="form-control" id="line1" placeholder="Street Address"
                                       value={address.line1}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {line1: e.target.value }})}/>
                            </Container>
                            <Container className="mb-3">
                                <label htmlFor="city" className="form-label">City</label>
                                <input type="text" className="form-control" id="city" placeholder="City"
                                       value={address.city}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {city: e.target.value }})}/>
                            </Container>
                            <Container className="mb-3">
                                <label htmlFor="state" className="form-label">State</label>
                                <input type="text" className="form-control" id="state" placeholder="State"
                                       value={address.state}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {state: e.target.value }})}/>
                            </Container>
                            <Container className="mb-3">
                                <label htmlFor="zip" className="form-label">Zip</label>
                                <input type="text" className="form-control" id="zip" placeholder="Zip"
                                       value={address.zip}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {zip: e.target.value }})}/>
                            </Container>

                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={ () =>  dispatch({type:'DISCARD_ADDRESS_EDIT'})}>Cancel</Button>
                            <Button variant="primary" onClick={ () =>  dispatch({type:'KEEP_ADDRESS_EDIT'})}>OK</Button>
                        </Modal.Footer>
                    </Modal>

                <Button variant="success" onClick={() => { onSave(formState.user.personalInfo); }}>Save</Button>
                <Button variant="light" onClick={onCancel}>Cancel</Button>
            </form>
        </Container>

    );
}

export {UserForm};
