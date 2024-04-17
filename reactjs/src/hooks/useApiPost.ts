import {basicHeader} from "../utility/BasicHeaders";
import useHttpError from "./useHttpError";
import {ChallengeDto} from "../utility/Mapper.ts";


const useApiPost = () => {

    const requestHeaders = basicHeader();
    const {throwOnHttpError} = useHttpError();

    function post(url: string, body: Record<string, unknown> | string | ChallengeDto) {

        const serializedData = typeof body === "string" ? body : JSON.stringify(body);

        const requestMeta = {
            headers: requestHeaders,
            method: 'POST',
            body: serializedData
        };

        return fetch(url, requestMeta)
            .then(throwOnHttpError)
            .catch(error => console.log(error));
    }

    return post;
};

export default useApiPost;