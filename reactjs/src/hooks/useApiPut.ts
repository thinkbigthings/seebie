import {basicHeader} from "../utility/BasicHeaders";
import useHttpError from "./useHttpError";
import {AnyObject} from "../utility/Constants.ts";

const useApiPut = () => {

    const requestHeaders = basicHeader(true);
    const {throwOnHttpError} = useHttpError();

    function put(url: string, body: AnyObject) {

        const requestMeta = {
            headers: requestHeaders,
            method: 'PUT',
            body: JSON.stringify(body)
        };

        return fetch(url, requestMeta)
            .then(throwOnHttpError)
            .catch(error => console.log(error));
    }

    return put;
};

export default useApiPut;