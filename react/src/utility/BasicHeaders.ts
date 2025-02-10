
const GET = {
    headers: basicHeader(false),
    method: 'GET'
};

function basicHeader(hasRequestBody=true):Headers {

    // If the server returns a 401 status code and includes one or more WWW-Authenticate headers, then
    // the browser pops up an authentication dialog asking for the username and password
    // Including X-Requested-With by the client signals the server to not respond with that header
    const headers = new Headers();
    headers.append('X-Requested-With', 'XMLHttpRequest');

    // Content-Type indicates the request body type so should only be set for PUT and POST requests
    if(hasRequestBody) {
        headers.append("Content-Type", "application/json");
    }

    return headers;
}

// takes only an object. If you pass in a string, it gets double-quoted from stringify
const fetchPost = (url: string, body: Record<string, unknown>) => {

    const bodyString = JSON.stringify(body);
    const requestHeaders: HeadersInit = basicHeader();

    const requestMeta: RequestInit = {
        headers: requestHeaders,
        method: 'POST',
        body: bodyString
    };

    return fetch(url, requestMeta);
}

export {basicHeader, GET, fetchPost}