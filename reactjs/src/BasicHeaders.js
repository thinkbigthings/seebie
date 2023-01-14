
import {REACT_APP_API_VERSION, VERSION_HEADER} from "./Constants";

function basicHeader(hasRequestBody=true) {

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

export {basicHeader}