import {GET} from "../utility/BasicHeaders";
import {useCallback, useEffect, useState} from "react";

interface PageData<T> {
    content: T[];
    first: boolean;
    last: boolean;
    totalElements: number;
    pageable: {
        offset: number;
        pageNumber: number;
        pageSize: number;
    };
    numberOfElements: number;
}

function createInitialPage<T>(): PageData<T> {
    return {
        content: [] as T[],  // Explicitly typing the empty array
        first: true,
        last: true,
        totalElements: 0,
        pageable: {
            offset: 0,
            pageNumber: 0,
            pageSize: 10,
        },
        numberOfElements: 0,
    };
}


// Define the return type of the hook explicitly
export interface UseApiGetReturn<T> {
    data: PageData<T>;
    pagingControls: {
        next: () => void;
        previous: () => void;
    };
}

const toPagingLabel = <T>(pageData: PageData<T>) => {
    const firstElementInPage = pageData.pageable.offset + 1;
    const lastElementInPage = pageData.pageable.offset + pageData.numberOfElements;
    const pagingLabel = firstElementInPage + "-" + lastElementInPage + " of " + pageData.totalElements;
    return pagingLabel;
}

// This is for paging
const useApiGet = <T>(initialUrl: string, customPageSize: number, reloadCount: number): UseApiGetReturn<T> => {

    // Use the factory function to create an initial page of the correct type
    let customizedPage: PageData<T> = createInitialPage<T>();
    customizedPage.pageable.pageSize = customPageSize;

    const [data, setData] = useState<PageData<T>>(customizedPage);

    let newInitialUrl = initialUrl + '?page=' + customizedPage.pageable.pageNumber + '&size=' + customizedPage.pageable.pageSize

    let [url, setUrl] = useState(newInitialUrl);

    // TODO maybe this should be in a callback hook?
    // const {throwOnHttpError} = useHttpError();

    const movePage = useCallback((amount: number) => {
        let pageable = structuredClone(data.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        setUrl(initialUrl + '?page=' + pageable.pageNumber + '&size=' + pageable.pageSize)
    }, [data.pageable, initialUrl]);

    const next = useCallback(() => movePage(1), [movePage]);
    const previous = useCallback(() => movePage(-1), [movePage]);

    const pagingControls = {
        next, previous
    }

    // reload count is kind of a synthetic property to trigger state changes which triggers fetch updates
    // reload count is controlled from outside, so the parent controls re-rendering
    // this is important when we want to reload from something changing (like creating new data that affects the list)
    useEffect(() => {
        fetch(url, GET)
            // .then(throwOnHttpError)
            .then((res) => res.json())
            .then(setData)
            .catch(error => console.log(error));
    }, [url, reloadCount]);

    return {data, pagingControls};
};

export {useApiGet, toPagingLabel};
export type {PageData};
