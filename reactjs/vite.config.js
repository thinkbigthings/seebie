import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import basicSsl from '@vitejs/plugin-basic-ssl';

export default defineConfig(() => {
    return {
        build: {
            outDir: 'build',
        },
        plugins: [
            react({ include: "**/*.jsx" }),
            basicSsl()
        ],
        server: {
            proxy: {
                '/login': {
                    target: 'https://localhost:9000',
                    changeOrigin: true,
                    secure: false
                }
            }
        }
    }
});
