
interface PostVariables<T> {
    url: string;
    body: T;
}

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

function buildRequestMeta(method:HttpMethod='GET', body:string=''):RequestInit {

    // If the server returns a 401 and includes one or more WWW-Authenticate headers,
    // then the browser pops up a dialog asking for the username and password
    // Including X-Requested-With by the client signals the server to not respond with that header
    const headers : HeadersInit = new Headers();
    headers.append('X-Requested-With', 'XMLHttpRequest');

    // Content-Type indicates the request body type so should only be set for PUT and POST requests
    if(method === 'POST' || method === 'PUT') {
        headers.append("Content-Type", "application/json");
        return { headers, method, body }
    }

    return { headers, method };
}

const httpGet = async <R,>(url: string) => {
    const response = await fetch(url, buildRequestMeta());
    return await response.json() as R;
}


const httpPost = async <T,R>(url: string, body: T) => {
    const requestMeta = buildRequestMeta('POST', JSON.stringify(body));
    const response = await fetch(url, requestMeta);
    return await response.json() as R
}

const httpPut = async <T,R>(url: string, body: T) => {
    const requestMeta = buildRequestMeta('PUT', JSON.stringify(body));
    const response = await fetch(url, requestMeta);
    return await response.json() as R;
}

const httpDelete = (url: string) => {
    return fetch(url, buildRequestMeta('DELETE'));
}

export type {PostVariables}
export {httpGet, httpPost, httpDelete}
