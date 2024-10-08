import {RecoveryActions} from "../utility/ErrorContext";
import useError from "./useError";

const useHttpError = () => {

    const {addError} = useError();

    const throwOnHttpError = (httpResponse: Response) => {

        if (httpResponse.status === 200) {
            return httpResponse;
        }

        if (httpResponse.status === 400) {
            addError('There was an input error. Try again.', RecoveryActions.NONE);
        }
        if (httpResponse.status === 401) {
            addError("Authentication failed. Login to continue.", RecoveryActions.LOGIN);
        }
        if (httpResponse.status === 403) {
            addError('The action was forbidden. Contact your administrator for additional privileges.', RecoveryActions.NONE);
        }
        if (httpResponse.status === 404) {
            addError('Endpoint not found: ' + httpResponse.url, RecoveryActions.NONE);
        }
        if (httpResponse.status >= 500) {
            addError('There was a server error. If the error continues contact your administrator.', RecoveryActions.NONE);
        }

        throw Error('Received bad response ' + httpResponse.status);
    }

    return {
        throwOnHttpError
    }
}

export default useHttpError;