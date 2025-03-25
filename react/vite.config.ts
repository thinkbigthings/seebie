import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import basicSsl from '@vitejs/plugin-basic-ssl';

export default defineConfig(() => {
return {
    build: {
        outDir: 'build',
    },
    plugins: [
        // js and css are included by default in the React plugin
        react(),
        basicSsl()
    ],
    server: {
        open: '#/login', // auto open the login page when vite server starts, this could also just be set to true
        proxy: {
            // if you proxy '/' then it will proxy ws requests intended for the vite server which breaks HMR
            // note you might have to refresh the browser to see HMR changes
            '/api': {
                target: 'https://localhost:9000',
                    changeOrigin: true,
                    secure: false
            },
            '/actuator': {
                target: 'https://localhost:9000',
                    changeOrigin: true,
                    secure: false
            }
        }
    }
}
});
