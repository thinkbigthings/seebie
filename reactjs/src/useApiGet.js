import {basicHeader} from "./BasicHeaders";
// import useHttpError from "./useHttpError";
import {useCallback, useEffect, useState} from "react";
import copy from "./Copier";

const request = {
    headers: basicHeader(false),
    method: 'GET'
};

const initialPage = {
    content: [],
    first: true,
    last: true,
    totalElements: 0,
    pageable: {
        offset: 0,
        pageNumber: 0,
        pageSize: 10,
    },
    numberOfElements: 0,
}

const toPagingLabel = (pageData) => {
    const firstElementInPage = pageData.pageable.offset + 1;
    const lastElementInPage = pageData.pageable.offset + pageData.numberOfElements;
    const pagingLabel = firstElementInPage + "-" + lastElementInPage + " of " + pageData.totalElements;
    return pagingLabel;
}

// This is for paging
const useApiGet = (initialUrl) => {

    let [url, setUrl] = useState(initialUrl);

    // TODO maybe this should be in a callback hook?
    // const {throwOnHttpError} = useHttpError();

    const [data, setData] = useState(initialPage);

    const [reloadCount, setReloadCount] = useState(0);
    const reload = useCallback(() => {
        setReloadCount(p => p + 1);
    }, []);


    const movePage = useCallback((amount) => {
        let pageable = copy(data.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        setUrl(initialUrl + '?page=' + pageable.pageNumber + '&size=' + pageable.pageSize)
    }, [data.pageable, initialUrl]);

    const next = useCallback(() => movePage(1), [movePage]);
    const previous = useCallback(() => movePage(-1), [movePage]);

    const pagingControls = {
        next, previous, reload
    }

    useEffect(() => {
        fetch(url, request)
            // .then(throwOnHttpError)
            .then((res) => res.json())
            .then(setData)
            .catch(error => console.log(error));
    }, [url, reloadCount]);

    return [data, pagingControls];
};

export {useApiGet, toPagingLabel};
