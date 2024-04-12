import React from 'react';

type CurrentUserContextType = [User, (user: User) => void];

export interface PersonalInfo {
    displayName: string;
    email: string;
    notificationsEnabled: boolean;
}

export interface User {
    username: string;
    roles: string[];
    registrationTime: string;
    personalInfo: PersonalInfo;
    isLoggedIn: boolean;
}

const blankUser: User = {
    username: '',
    roles: [],
    registrationTime: '',
    personalInfo: {
        displayName: '',
        email: '',
        notificationsEnabled: false
    },
    isLoggedIn: false,
}

const CurrentUserContext = React.createContext<CurrentUserContextType>([blankUser, () => {}]);
CurrentUserContext.displayName = 'CurrentUserContext';

export { blankUser, CurrentUserContext };