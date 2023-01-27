import {basicHeader} from "./BasicHeaders";
import useHttpError from "./useHttpError";
import {useCallback, useEffect, useState} from "react";

const request = {
    headers: basicHeader(false),
    method: 'GET'
};

const useApiGet = (initialUrl, initialData) => {

    let [url, setUrl] = useState(initialUrl);
    const {throwOnHttpError} = useHttpError();

    const [data, setData] = useState(initialData);

    const [reloadCount, setReloadCount] = useState(0);
    const reload = useCallback(() => {
        setReloadCount(p => p + 1);
    }, []);

    useEffect(() => {
        fetch(url, request)
            .then(throwOnHttpError)
            .then((res) => res.json())
            .then(setData)
            .catch(error => console.log(error));
    }, [url, reloadCount]);

    return [data, setUrl, reload];
};

export default useApiGet;
