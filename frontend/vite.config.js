import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
  
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/jasmineAPI': {
        target: 'http://localhost:8086/citizenscience',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/jasmineAPI/, '')
      }
    }
  }
})