import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/as2/admin/',
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'query-vendor': ['@tanstack/react-query', '@tanstack/react-table'],
          'form-vendor': ['react-hook-form', 'zod', '@hookform/resolvers']
        }
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/as2/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
