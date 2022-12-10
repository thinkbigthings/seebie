import React, {useState} from 'react';

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

const ErrorContext = React.createContext([noErrors, (error) => {}]);
ErrorContext.displayName = 'ErrorContext';

const ErrorProvider = (props) => {

    const [error, setError] = useState(noErrors);

    return (
        <ErrorContext.Provider value={[error, setError]}>
            {props.children}
        </ErrorContext.Provider>
    );
}

export {ErrorContext, ErrorProvider, noErrors, recoveryActions};
