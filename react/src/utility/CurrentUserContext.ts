import React from 'react';
import {User} from "../types/user.types";

type CurrentUserContextType = [User, (user: User) => void];

const blankUser: User = {
    publicId: '',
    email: '',
    roles: [],
    registrationTime: '',
    personalInfo: {
        displayName: '',
        notificationsEnabled: false
    },
    isLoggedIn: false,
}

const CurrentUserContext = React.createContext<CurrentUserContextType>([blankUser, () => {}]);
CurrentUserContext.displayName = 'CurrentUserContext';

export { blankUser, CurrentUserContext };
export type { User };