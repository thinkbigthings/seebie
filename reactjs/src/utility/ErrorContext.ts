import React from 'react';

enum RecoveryActions {
    NONE,
    LOGIN,
    RELOAD
}

interface ErrorStatus {
    message: string;
    hasError: boolean;
    recoveryAction: RecoveryActions;
}

const NO_ERROR: ErrorStatus = {
    message: '',
    hasError: false,
    recoveryAction: RecoveryActions.NONE
}

interface ErrorStateHookData {
    errorStatus: ErrorStatus;
    setErrorStatus: (status: ErrorStatus) => void;
}

const NO_ERROR_STATUS: ErrorStateHookData = {
    errorStatus: NO_ERROR,
    setErrorStatus: (status: ErrorStatus) => {}
}

const ErrorContext = React.createContext(NO_ERROR_STATUS);
ErrorContext.displayName = 'ErrorContext';

export {ErrorContext, NO_ERROR, NO_ERROR_STATUS, RecoveryActions};
export type { ErrorStatus, ErrorStateHookData };

