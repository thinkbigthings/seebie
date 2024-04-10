import React from 'react';
import {blankUser, User} from "./Constants";

type CurrentUserContextType = [User, (user: User) => void];


const CurrentUserContext = React.createContext<CurrentUserContextType>([blankUser, () => {}]);
CurrentUserContext.displayName = 'CurrentUserContext';

export { CurrentUserContext };