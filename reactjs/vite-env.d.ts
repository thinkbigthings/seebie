/// <reference types="vite/client" />

interface ImportMeta {
    env: {
        // explicitly define each expected property for better type safety:
        VITE_API_VERSION: string;
    };
}