import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
  
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // All API requests go through authentication service (port 8080)
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: false
      }
    }
  }
})