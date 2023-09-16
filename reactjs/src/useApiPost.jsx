
import {basicHeader} from "./utility/BasicHeaders";
import useHttpError from "./useHttpError";


const useApiPost = () => {

    const requestHeaders = basicHeader();
    const {throwOnHttpError} = useHttpError();

    function post(url, body) {

        const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

        const requestMeta = {
            headers: requestHeaders,
            method: 'POST',
            body: bodyString
        };

        return fetch(url, requestMeta)
            .then(throwOnHttpError)
            .catch(error => console.log(error));
    }

    return post;
};

export default useApiPost;