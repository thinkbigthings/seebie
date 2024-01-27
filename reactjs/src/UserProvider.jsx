import React, {useState} from "react";
import {defaultUser, UserContext} from "./UserContext";

const UserProvider = (props) => {

    // code for pre-loading the user's information if we have their token in
    // localStorage goes here
    const currentUserStr = localStorage.getItem('currentUser');
    const currentUser = currentUserStr !== null
        ? JSON.parse(currentUserStr)
        : defaultUser;

    // If we start tracking more application state
    // it would make sense to use a reducer here
    const [user, setUser] = useState(currentUser);

    // logout
    function clearCurrentUser() {
        localStorage.removeItem("currentUser");
        setUser(defaultUser);
    }

    // login or refresh
    function setCurrentUser(newUserData) {
        localStorage.setItem('currentUser', JSON.stringify(newUserData));
        setUser(newUserData);
    }

    // current
    function getCurrentUser() {
        return user;
    }

    function hasRole(roleName) {
        return user.roles.find(role => role === roleName) !== undefined;
    }

    const hasAdmin = () => hasRole('ADMIN');

    const userContext = {
        clearCurrentUser,
        setCurrentUser,
        getCurrentUser,
        hasAdmin
    }

    return (
        <UserContext.Provider value={userContext}>
            {props.children}
        </UserContext.Provider>
    );
}
export {UserProvider};