import {basicHeader} from "./BasicHeaders";
import useHttpError from "./useHttpError";
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
    const {throwOnHttpError} = useHttpError();

    const [data, setData] = useState(initialPage);

    const [reloadCount, setReloadCount] = useState(0);
    const reload = useCallback(() => {
        setReloadCount(p => p + 1);
    }, []);

    function movePage(amount) {
        let pageable = copy(data.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        setUrl(url + '?page=' + pageable.pageNumber + '&size=' + pageable.pageSize)
    }

    const next = useCallback(() => movePage(1), []);
    const previous = useCallback(() => movePage(-1), []);

    const pagingControls = {
        next, previous, reload
    }

    useEffect(() => {
        fetch(url, request)
            .then(throwOnHttpError)
            .then((res) => res.json())
            .then(setData)
            .catch(error => console.log(error));
    }, [url, reloadCount]);

    return [data, pagingControls];
};

export {useApiGet, toPagingLabel};
