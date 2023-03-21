import React, {useReducer} from 'react';

import Button from "react-bootstrap/Button";

import copy from './Copier.js';
import Container from "react-bootstrap/Container";
import useCurrentUser from "./useCurrentUser";



function UserForm(props) {

    const {onCancel, onSave, initData} = props;

    const {hasAdmin} = useCurrentUser();

    // return new state based on current state and action
    // reducer itself should not cause side effects, it should be called FROM a side effect
    // possibly combined into a custom hook
    const formReducer = (formState, action) => {

        let newState = copy(formState);
        switch(action.type) {
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

    const [formState, dispatch] = useReducer(formReducer, {user: initData}, arg => arg);

    // the context hook retains its value over re-renders,
    // so need to trigger the update here once the data has been loaded for real
    if(initData.username !== '' && formState.user.username === '') {
        dispatch({type:'LOAD_USER', payload: initData});
    }

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
                    <label htmlFor="registrationTime" className="form-label">Member Since</label>
                    <input type="text" className="form-control" id="inputRegistrationTime" placeholder="Registration Time"
                           disabled
                           value={new Date(Date.parse(formState.user.registrationTime)).toDateString()}/>
                </Container>

                {hasAdmin()
                    ? <Container className="ps-0 mb-3">
                        <label htmlFor="roles" className="form-label">Roles</label>
                        <input type="text" className="form-control" id="roles" placeholder="Roles"
                               disabled
                               value={formState.user.roles}/>
                      </Container>
                    : <div />
                }

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputDisplayName" className="form-label">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={formState.user.personalInfo.displayName}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {displayName: e.target.value }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputEmail" className="form-label">Email address</label>
                    <input type="email" className="form-control" id="inputEmail" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           value={formState.user.personalInfo.email}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {email: e.target.value }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="notificationsEnabled" className="form-label">Enabled Reminder Emails</label>
                    <input className="form-check-input mx-3 p-2" type="checkbox" id="notificationsEnabled"
                           placeholder="Enable Reminder Emails"
                           checked={formState.user.personalInfo.notificationsEnabled }
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {notificationsEnabled: e.target.checked }})}/>
                </Container>

                <div className="d-flex">
                    <Button className="m-1" variant="primary" onClick={() => { onSave(formState.user.personalInfo); }} >Save</Button>
                    <Button className="m-1" variant="secondary" onClick={onCancel}>Cancel</Button>
                </div>
            </form>
        </Container>

    );
}

export {UserForm};
