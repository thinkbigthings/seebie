import React from 'react';
import {blankUser} from "./utility/Constants";


const CurrentUserContext = React.createContext([blankUser, (user) => {}]);
CurrentUserContext.displayName = 'CurrentUserContext';

export {CurrentUserContext};
