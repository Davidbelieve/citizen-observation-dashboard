import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
  
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Proxy to Oluwabusola's Spring Cloud Gateway (port 8090)
      // Routes /api/regions/south-east-england/** to appropriate microservices
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        secure: false
      }
    }
  }
})