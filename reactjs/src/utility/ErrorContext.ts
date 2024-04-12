import React from 'react';

const recoveryActions = {
    NONE: 'none',
    LOGIN: 'login',
    RELOAD: 'reload'
}

const noErrors = {
    message: '',
    hasError: false,
    recoveryAction: recoveryActions.NONE
}

const ErrorContext = React.createContext([noErrors, () => {}]);
ErrorContext.displayName = 'ErrorContext';

export {ErrorContext, noErrors, recoveryActions};
