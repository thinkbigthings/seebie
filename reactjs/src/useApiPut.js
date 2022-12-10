
import useError from "./useError";
import {basicHeader} from "./BasicAuth";
import useHttpError from "./useHttpError";


const useApiPut = () => {

    const requestHeaders = basicHeader();
    const { addError } = useError();
    const {throwOnHttpError} = useHttpError();

    function put(url, body) {

        const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

        const requestMeta = {
            headers: requestHeaders,
            method: 'PUT',
            body: bodyString
        };

        return fetch(url, requestMeta)
            .then(throwOnHttpError)
            .catch(error => console.log(error));
    }

    return put;
};

export default useApiPut;