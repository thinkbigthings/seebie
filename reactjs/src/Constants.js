
const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;

export {VERSION_HEADER, REACT_APP_API_VERSION};