import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import basicSsl from '@vitejs/plugin-basic-ssl';

export default defineConfig(() => {
    return {
        build: {
            outDir: 'build',
        },
        plugins: [
            // js and css are included by default in the react plugin
            react({ include: "**/*.jsx" }),
            basicSsl()
        ],
        server: {
            open: true, // auto open the browser when vite server starts
            proxy: {
                // if you proxy '/' then it will proxy ws requests intended for the vite server which breaks HMR
                // note you might have to refresh the browser to see HMR changes
                '/api': {
                    target: 'https://localhost:9000',
                    changeOrigin: true,
                    secure: false
                }
            }
        }
    }
});
