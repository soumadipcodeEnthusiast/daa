import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
// BACKEND_PORT lets `npm run dev` target a non-default backend port, e.g. if
// 8080 is already taken on your machine: BACKEND_PORT=8089 npm run dev
const backendPort = process.env.BACKEND_PORT ?? '8095'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: `http://localhost:${backendPort}`,
        changeOrigin: true,
      },
    },
  },
})
