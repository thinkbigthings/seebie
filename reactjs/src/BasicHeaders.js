
import {REACT_APP_API_VERSION, VERSION_HEADER} from "./Constants";


const GET = {
    headers: basicHeader(),
    method: 'GET'
};

function basicHeader(hasRequestBody=true) {

    // If the server returns a 401 status code and includes one or more WWW-Authenticate headers, then
    // the browser pops up an authentication dialog asking for the username and password
    // Including X-Requested-With by the client signals the server to not respond with that header
    let headers = {
        'X-Requested-With': 'XMLHttpRequest'
    };

    // Content-Type indicates the request body type so should only be set for PUT and POST requests
    if(hasRequestBody) {
        headers["Content-Type"]="application/json";
    }

    headers[VERSION_HEADER] = REACT_APP_API_VERSION;

    return headers;
}

export {basicHeader, GET}