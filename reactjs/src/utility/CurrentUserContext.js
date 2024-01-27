import React from 'react';
import {blankUser} from "./Constants";


const CurrentUserContext = React.createContext([blankUser, (user) => {}]);
CurrentUserContext.displayName = 'CurrentUserContext';

export {CurrentUserContext};
