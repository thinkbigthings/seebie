
import {basicHeader} from "../utility/BasicHeaders";
import useHttpError from "./useHttpError";


const useApiDelete = () => {

    const requestHeaders = basicHeader();
    const {throwOnHttpError} = useHttpError();

    function callDelete(url) {

        const requestMeta = {
            headers: requestHeaders,
            method: 'DELETE'
        };

        return fetch(url, requestMeta)
            .then(throwOnHttpError)
            .catch(error => console.log(error));
    }

    return callDelete;
};

export default useApiDelete;