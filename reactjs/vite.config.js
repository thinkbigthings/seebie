import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';

export default defineConfig(() => {
    return {
        build: {
            outDir: 'build',
        },
        plugins: [react()],
        // proxy: {
        //     // string shorthand: http://localhost:5173/ -> http://localhost:4567/foo
        //     '/login': 'https://localhost:9000/login',
        // }
    }
});