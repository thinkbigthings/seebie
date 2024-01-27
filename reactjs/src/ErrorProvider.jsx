import React, {useState} from "react";
import {ErrorContext, noErrors} from "./ErrorContext";

const ErrorProvider = (props) => {

    const [error, setError] = useState(noErrors);

    return (
        <ErrorContext.Provider value={[error, setError]}>
            {props.children}
        </ErrorContext.Provider>
    );
}
export {ErrorProvider};