import React, {useReducer} from 'react';

import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import useCurrentUser from "./hooks/useCurrentUser";
import {PersonalInfo, User} from "./types/user.types";

enum ActionType {
    LOAD_USER,
    UPDATE_USER
}

type Action =
    | { type: ActionType.LOAD_USER; payload: User }
    | { type: ActionType.UPDATE_USER; payload: Partial<PersonalInfo> };

function UserForm(props:{onCancel: () => void, onSave: (personalInfo: PersonalInfo) => void, initData: User}) {

    const {onCancel, onSave, initData} = props;

    const {hasAdmin} = useCurrentUser();

    // return new state based on current state and action
    // reducer itself should not cause side effects, it should be called FROM a side effect
    // possibly combined into a custom hook
    const formReducer = (formState: User, action: Action): User => {
        switch (action.type) {
            case ActionType.LOAD_USER:
                return action.payload;
            case ActionType.UPDATE_USER:
                return {
                    ...formState,
                    personalInfo: {
                        ...formState.personalInfo,
                        ...action.payload
                    }
                };
            default:
                throw new Error('Unhandled action in formReducer');
        }
    };


    const [formState, dispatch] = useReducer(formReducer, initData, arg => arg);

    // the context hook retains its value over re-renders,
    // so need to trigger the update here once the data has been loaded for real
    if(initData.username !== '' && formState.username === '') {
        dispatch({type:ActionType.LOAD_USER, payload: initData});
    }

    return (
        <Container id="userFormId" className="ps-0 " >

            <form>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputUserName" className="form-label">User Name</label>
                    <input type="email" className="form-control" id="inputUserName" placeholder="User Name"
                           disabled
                           value={formState.username}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="registrationTime" className="form-label">Member Since</label>
                    <input type="text" className="form-control" id="inputRegistrationTime" placeholder="Registration Time"
                           disabled
                           value={new Date(Date.parse(formState.registrationTime)).toDateString()}/>
                </Container>

                {hasAdmin()
                    ? <Container className="ps-0 mb-3">
                        <label htmlFor="roles" className="form-label">Roles</label>
                        <input type="text" className="form-control" id="roles" placeholder="Roles"
                               disabled
                               value={formState.roles}/>
                      </Container>
                    : <div />
                }

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputDisplayName" className="form-label">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={formState.personalInfo.displayName}
                           onChange={e => dispatch({type:ActionType.UPDATE_USER, payload: {displayName: e.target.value }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputEmail" className="form-label">Email address</label>
                    <input type="email" className="form-control" id="inputEmail" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           value={formState.personalInfo.email}
                           onChange={e => dispatch({type:ActionType.UPDATE_USER, payload: {email: e.target.value }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="notificationsEnabled" className="form-label">Enable Reminder Emails</label>
                    <input className="form-check-input mx-3 p-2" type="checkbox" id="notificationsEnabled"
                           placeholder="Enable Reminder Emails"
                           checked={formState.personalInfo.notificationsEnabled }
                           onChange={e => dispatch({type:ActionType.UPDATE_USER, payload: {notificationsEnabled: e.target.checked }})}/>
                </Container>

                <div className="d-flex flex-row">
                    <Button className="me-3" variant="primary" onClick={() => { onSave(formState.personalInfo); }} >Save</Button>
                    <Button className="" variant="secondary" onClick={onCancel}>Cancel</Button>
                </div>

            </form>
        </Container>

    );
}

export {UserForm};
