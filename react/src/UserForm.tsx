import React, {useReducer} from 'react';

import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import useCurrentUser from "./hooks/useCurrentUser";
import {Action, ActionType, PersonalInfo, User} from "./types/user.types";

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
    if(initData.publicId !== '' && formState.publicId === '') {
        dispatch({type:ActionType.LOAD_USER, payload: initData});
    }

    return (
        <Container id="userFormId" className="ps-0 " >

            <form>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputDisplayName" className="form-label">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={formState.personalInfo.displayName}
                           onChange={e => dispatch({type:ActionType.UPDATE_USER, payload: {displayName: e.target.value }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="notificationsEnabled" className="form-label">Enable Reminder Emails</label>
                    <input className="form-check-input mx-3 p-2" type="checkbox" id="notificationsEnabled"
                           placeholder="Enable Reminder Emails"
                           checked={formState.personalInfo.notificationsEnabled }
                           onChange={e => dispatch({type:ActionType.UPDATE_USER, payload: {notificationsEnabled: e.target.checked }})}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="inputEmail" className="form-label">Email address</label>
                    <input type="email" className="form-control" id="inputEmail" aria-describedby="emailHelp"
                           disabled
                           placeholder="Enter email"
                           value={formState.email} />
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="registrationTime" className="form-label">Member Since</label>
                    <input type="text" className="form-control" id="inputRegistrationTime" placeholder="Registration Time"
                           disabled
                           value={new Date(Date.parse(formState.registrationTime)).toDateString()}/>
                </Container>

                <Container className="ps-0 mb-3">
                    <label htmlFor="publicId" className="form-label">User ID</label>
                    <input type="text" className="form-control" id="publicId" placeholder="User ID"
                           disabled
                           value={formState.publicId}/>
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

                <div className="d-flex flex-row">
                    <Button className="me-3" variant="primary" onClick={() => { onSave(formState.personalInfo); }} >Save</Button>
                    <Button className="" variant="secondary" onClick={onCancel}>Cancel</Button>
                </div>

            </form>
        </Container>

    );
}

export {UserForm};
