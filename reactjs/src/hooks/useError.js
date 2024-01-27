import { useContext } from 'react';
import {ErrorContext, noErrors, recoveryActions} from "../utility/ErrorContext";

const useError = () => {

    const [error, setError] = useContext(ErrorContext);

    function addError(message, action) {
        const recovery = action !== undefined ? action : recoveryActions.NONE;
        setError({message, hasError: true, recoveryAction: recovery});
    }

    function clearError() {
        setError(noErrors);
    }

    return {
        error,
        addError,
        clearError,
    }
};

export default useError;