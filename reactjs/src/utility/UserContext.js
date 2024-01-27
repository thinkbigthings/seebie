import React from 'react';

const defaultUser = {
    displayName: '',
    username: '',
    password: '',
    roles: [],
    isLoggedIn: false,
}

const UserContext = React.createContext(defaultUser);
UserContext.displayName = 'UserContext';

export {UserContext, defaultUser};
