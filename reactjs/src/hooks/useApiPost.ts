import {basicHeader} from "../utility/BasicHeaders";
import useHttpError from "./useHttpError";
import {ChallengeDto} from "../types/challenge.types";
import {PasswordResetRequest, RegistrationRequest} from "../types/user.types";
import {SleepDto} from "../types/sleep.types.ts";


const useApiPost = () => {

    const requestHeaders = basicHeader();
    const {throwOnHttpError} = useHttpError();

    function post(url: string, body: SleepDto | ChallengeDto | RegistrationRequest | PasswordResetRequest) {

        const serializedData = JSON.stringify(body);

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