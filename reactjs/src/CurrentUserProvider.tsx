import {blankUser} from "./utility/CurrentUserContext";
import React, {useState} from "react";
import {CurrentUserContext} from "./utility/CurrentUserContext";
import {User} from "./types/user.types";

const CurrentUserProvider = (props: {children:React.ReactNode}) => {

    // code for preloading the user's information if we have their token in
    // localStorage goes here
    const currentUserStr = localStorage.getItem('currentUser');
    const user: User = currentUserStr !== null
        ? JSON.parse(currentUserStr)
        : blankUser;

    const [currentUser, setCurrentUser] = useState(user);

    return (
        <CurrentUserContext.Provider value={[currentUser, setCurrentUser]}>
            {props.children}
        </CurrentUserContext.Provider>
    );
}
export {CurrentUserProvider};