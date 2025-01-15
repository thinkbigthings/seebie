import {GET} from "../utility/BasicHeaders";
import {useCallback, useEffect, useState} from "react";

interface PageMetadata {
    size: number,
    number: number,
    totalElements: number,
    totalPages: number
}

// TODO refactor name to PagedModel so it matches the server
interface PagedModel<T> {
    content: T[];
    page: PageMetadata;
}

const isFirst = (pageMetadata: PageMetadata) => {
    return pageMetadata.number == 0;
}

const isLast = (pageMetadata: PageMetadata) => {
    return pageMetadata.number == (pageMetadata.totalPages-1);
}

function createInitialPage<T>(): PagedModel<T> {
    return {
        content: [] as T[],  // Explicitly typing the empty array
        page: {
            size: 0,
            number: 0,
            totalElements: 0,
            totalPages: 0
        },
    };
}


// Define the return type of the hook explicitly
export interface UseApiGetReturn<T> {
    data: PagedModel<T>;
    pagingControls: {
        next: () => void;
        previous: () => void;
    };
}

const toPagingLabel = <T>(pageData: PagedModel<T>) => {
    const offset = pageData.page.size * pageData.page.number;
    const numElementsInPage = pageData.content.length;
    const firstElementInPage = offset + 1;
    const lastElementInPage = offset + numElementsInPage;
    return firstElementInPage + "-" + lastElementInPage + " of " + pageData.page.totalElements;
}

// This is for paging
const useApiGet = <T>(initialUrl: string, customPageSize: number, reloadCount: number): UseApiGetReturn<T> => {

    // Use the factory function to create an initial page of the correct type
    let customizedPage: PagedModel<T> = createInitialPage<T>();
    customizedPage.page.size = customPageSize;

    const [data, setData] = useState<PagedModel<T>>(customizedPage);

    let newInitialUrl = initialUrl + '?page=' + customizedPage.page.number + '&size=' + customizedPage.page.size

    let [url, setUrl] = useState(newInitialUrl);

    // TODO maybe this should be in a callback hook?
    // const {throwOnHttpError} = useHttpError();

    const movePage = useCallback((amount: number) => {
        let page = structuredClone(data.page);
        page.number = page.number + amount;
        setUrl(initialUrl + '?page=' + page.number + '&size=' + page.size)
    }, [data.page, initialUrl]);

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

export {useApiGet, toPagingLabel, isFirst, isLast};
export type {PagedModel};
