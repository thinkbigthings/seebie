
import React, {useState} from "react";
import {ErrorContext, NO_ERROR_STATUS, ErrorStateHookData} from "./utility/ErrorContext";

const ErrorProvider = (props: {children:React.ReactNode}) => {

    const [error, setError] = useState(NO_ERROR_STATUS.errorStatus);

    let errorStatusState: ErrorStateHookData = {
        errorStatus: error,
        setErrorStatus: setError
    };

    return (
        <ErrorContext.Provider value={errorStatusState}>
            {props.children}
        </ErrorContext.Provider>
    );
}
export {ErrorProvider};