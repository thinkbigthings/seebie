
import {REACT_APP_API_VERSION, VERSION_HEADER} from "./Constants";

function basicHeader() {

    let headers = {
        "Content-Type": "application/json",
        'X-Requested-With': 'XMLHttpRequest'
    };
    headers[VERSION_HEADER] = REACT_APP_API_VERSION;
    return headers;
}

export {basicHeader}