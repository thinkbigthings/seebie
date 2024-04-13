import { useContext } from 'react';
import {ErrorContext, ErrorStatus, NO_ERROR, RecoveryActions} from "../utility/ErrorContext";

const useError = () => {

    const error = useContext(ErrorContext);

    function addError(message: string, action: RecoveryActions) {
        error.setErrorStatus({message, hasError: true, recoveryAction: action});
    }

    function clearError() {
        error.setErrorStatus(NO_ERROR);
    }

    return {
        error,
        addError,
        clearError,
    }
};

export default useError;