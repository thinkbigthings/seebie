
const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { VITE_API_VERSION } = import.meta.env;

export {VERSION_HEADER, VITE_API_VERSION};