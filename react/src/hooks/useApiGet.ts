import {GET} from "../utility/BasicHeaders";
import {useCallback, useEffect, useState} from "react";

interface PageMetadata {
    size: number,
    number: number,
    totalElements: number,
    totalPages: number
}

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

const toPagingLabel = <T>(pageData: PagedModel<T>): string => {
    const { size, number, totalElements } = pageData.page;
    const offset = size * number;
    const numElementsInPage = pageData.content.length;

    if (totalElements === 0) {
        return "0 of 0"; // Handle empty data gracefully
    }

    const firstElementInPage = offset + 1;
    const lastElementInPage = offset + numElementsInPage;

    return `${firstElementInPage} - ${lastElementInPage} of ${totalElements}`;
};

const buildPagingUrl = (baseUrl: string, page: PageMetadata) => {
    return `${baseUrl}?page=${page.number}&size=${page.size}`;
}

// This is for paging
const useApiGet = <T>(initialUrl: string, customPageSize: number, reloadCount: number): UseApiGetReturn<T> => {

    // Use the factory function to create an initial page of the correct type
    let customizedPage: PagedModel<T> = createInitialPage<T>();
    customizedPage.page.size = customPageSize;

    const [data, setData] = useState<PagedModel<T>>(customizedPage);

    let newInitialUrl = buildPagingUrl(initialUrl, customizedPage.page);

    let [url, setUrl] = useState(newInitialUrl);

    const movePage = useCallback((amount: number) => {
        let page = structuredClone(data.page);
        page.number = page.number + amount;
        setUrl(buildPagingUrl(initialUrl, page))
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
