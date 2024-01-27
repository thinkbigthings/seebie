import React, {useState} from 'react';
import {blankUser} from "./utility/Constants";


const CurrentUserContext = React.createContext([blankUser, (user) => {}]);
CurrentUserContext.displayName = 'CurrentUserContext';

const CurrentUserProvider = (props) => {

    // code for pre-loading the user's information if we have their token in
    // localStorage goes here
    const currentUserStr = localStorage.getItem('currentUser');
    const user = currentUserStr !== null
        ? JSON.parse(currentUserStr)
        : blankUser;

    const [currentUser, setCurrentUser] = useState(user);

    return (
        <CurrentUserContext.Provider value={[currentUser, setCurrentUser]}>
            {props.children}
        </CurrentUserContext.Provider>
    );
}

export {CurrentUserContext, CurrentUserProvider};
