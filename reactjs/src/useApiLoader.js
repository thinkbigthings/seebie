import { useState, useEffect } from 'react';

import {basicHeader} from "./BasicAuth";
import useHttpError from "./useHttpError";

// inspired by https://www.henriksommerfeld.se/error-handling-with-fetch/

// this is more of a data loader for a page, which is useful as a hook
const useApiLoader = (initialUrl, initialData) => {

    const [url, setUrl] = useState(initialUrl);
    const [isLoading, setLoading] = useState(true);
    const [isLongRequest, setLongRequest] = useState(false);
    const [fetchedData, setFetchedData] = useState(initialData);

    const requestHeaders = basicHeader();

    const { throwOnHttpError } = useHttpError();

    const longLoadTimeMs = 1000;

    useEffect(() => {

        // the caller can choose to only show a spinner if it takes a long time using the isLongRequest flag
        // otherwise it's a poor UX to flash a spinner for a fraction of a second
        let longRequestTimer = setTimeout(() => {}, longLoadTimeMs);

        const handleFetchResponse = response => {

            clearTimeout(longRequestTimer);
            setLoading(false);
            setLongRequest(false);

            return response.ok && response.json ? response.json() : initialData;
        };

        const fetchData = () => {

            clearTimeout(longRequestTimer);
            longRequestTimer = setTimeout(() => setLongRequest(true), longLoadTimeMs);
            setLoading(true);
            setLongRequest(false);

            let request = {
                headers: requestHeaders,
            };

            // console.log(JSON.stringify(request));

            return fetch(url, request)
                .then(throwOnHttpError)
                .then(handleFetchResponse)
                .catch(error => {
                    console.log(error);
                    return initialData;
                });
        };

        if(url) {
            fetchData().then(data => setFetchedData(data));
        }

        // // // might need to set the cleanup function if any flags need to be reset for subsequent calls
        // return () => {
        //    unmounted = true;
        // }

        // TODO React Hook useEffect has missing dependencies: 'initialData', 'requestHeaders', and 'throwOnHttpError'.
        //  Either include them or remove the dependency array  react-hooks/exhaustive-deps

    }, [url]);

    return {
        setUrl,
        isLoading,
        isLongRequest,
        fetchedData
    }
};

export default useApiLoader;