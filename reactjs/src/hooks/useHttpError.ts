// @ts-nocheck
import {recoveryActions} from "../utility/ErrorContext";
import {VITE_API_VERSION, VERSION_HEADER} from "../utility/Constants";
import useError from "./useError";

const useHttpError = () => {

    const {addError} = useError();

    const throwOnHttpError = httpResponse => {

        if (httpResponse.status === 200) {
            return httpResponse;
        }

        if (httpResponse.status === 400) {
            addError('There was an input error. Try again.', recoveryActions.NONE);
        }
        if (httpResponse.status === 401) {
            addError("Your session expired. Login to continue.", recoveryActions.LOGIN);
        }
        if (httpResponse.status === 403) {
            addError('The action was forbidden. Contact your administrator for additional privileges.', recoveryActions.NONE);
        }
        if (httpResponse.status === 404) {
            addError('Endpoint not found: ' + httpResponse.url, recoveryActions.NONE);
        }
        if (httpResponse.status >= 500) {
            addError('There was a server error. If the error continues contact your administrator.', recoveryActions.NONE);
        }

        // this should come back as a 406, but let's check the versions
        const serverApiVersion = httpResponse.headers.get(VERSION_HEADER);
        if (serverApiVersion !== null && serverApiVersion !== VITE_API_VERSION) {
            addError('Your app is out of date. Try reloading the page.', recoveryActions.RELOAD);
        }
        throw Error('Received bad response ' + httpResponse.status);
    }

    return {
        throwOnHttpError
    }
}

export default useHttpError;