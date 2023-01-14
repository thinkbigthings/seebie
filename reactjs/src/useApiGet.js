import {basicHeader} from "./BasicHeaders";
import useHttpError from "./useHttpError";
import {useEffect, useState} from "react";

const request = {
    headers: basicHeader(false),
    method: 'GET'
};

const useApiGet = (initialUrl, initialData) => {

    let [url, setUrl] = useState(initialUrl);
    const {throwOnHttpError} = useHttpError();

    const [data, setData] = useState(initialData);

    useEffect(() => {
        fetch(url, request)
            .then(throwOnHttpError)
            .then((res) => res.json())
            .then(setData)
            .catch(error => console.log(error));
    }, [url]);

    return [data, setUrl];
};

export default useApiGet;
