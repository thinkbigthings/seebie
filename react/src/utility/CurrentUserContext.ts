import React from 'react';
import {User} from "../types/user.types";

type CurrentUserContextType = [User, (user: User) => void];

const blankUser: User = {
    publicId: '',
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
export type { User };