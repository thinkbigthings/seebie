import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import basicSsl from '@vitejs/plugin-basic-ssl';

export default defineConfig(() => {
    return {
        build: {
            outDir: 'build',
        },
        plugins: [react(), basicSsl()],
        proxy: {
            '/': {
                target: 'https://localhost:9000',
                changeOrigin: true,
                secure: false, // This allows requests to be proxied to servers with self-signed certificates
            },

        },
    }
});