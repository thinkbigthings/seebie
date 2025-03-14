import {MessageDto} from "../types/message.types.ts";
import {basicHeader} from "./BasicHeaders.ts";

interface PostVariables {
    url: string;
    body: MessageDto;
}

const httpPost = async <T,>(url: string, body: Record<string, unknown>) => {

    const bodyString = JSON.stringify(body);
    const requestHeaders: HeadersInit = basicHeader();

    const requestMeta: RequestInit = {
        headers: requestHeaders,
        method: 'POST',
        body: bodyString
    };

    const response = await fetch(url, requestMeta);
    const data = await response.json();
    return data as T;
}

const httpDelete = (url: string) => {

    const requestHeaders: HeadersInit = basicHeader();

    const requestMeta = {
        headers: requestHeaders,
        method: 'DELETE'
    };

    return fetch(url, requestMeta);
}

export type {PostVariables}
export {httpPost, httpDelete}
