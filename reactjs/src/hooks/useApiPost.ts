import {basicHeader} from "../utility/BasicHeaders";
import useHttpError from "./useHttpError";
import {AnyObject} from "../utility/Constants.ts";


const useApiPost = () => {

    const requestHeaders = basicHeader();
    const {throwOnHttpError} = useHttpError();

    function post(url: string, body: AnyObject) {

        const requestMeta = {
            headers: requestHeaders,
            method: 'POST',
            body: JSON.stringify(body)
        };

        return fetch(url, requestMeta)
            .then(throwOnHttpError)
            .catch(error => console.log(error));
    }

    return post;
};

export default useApiPost;