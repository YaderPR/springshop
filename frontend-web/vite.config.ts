import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    chunkSizeWarningLimit: 1600, // Subimos el límite para que no moleste por ahora
    rollupOptions: {
      output: {
        manualChunks(id) {
          // 1. React y su ecosistema (Core)
          if (id.includes('node_modules/react') || 
              id.includes('node_modules/react-dom') || 
              id.includes('node_modules/react-router-dom')) {
            return 'react-core';
          }

          // 2. Animaciones (Suele ser muy pesado)
          if (id.includes('node_modules/framer-motion')) {
            return 'animations';
          }

          // 3. Íconos
          if (id.includes('node_modules/lucide-react')) {
            return 'icons';
          }

          // 4. Utilidades pesadas específicas
          if (id.includes('node_modules/html2canvas') || id.includes('node_modules/jspdf')) {
            return 'pdf-utils';
          }

          // 5. Cliente HTTP
          if (id.includes('node_modules/axios')) {
            return 'http-client';
          }

          // 6. Lo demás se queda en vendor, pero ahora será más ligero
          if (id.includes('node_modules')) {
            return 'vendor';
          }
        },
      },
    },
  },
});