// services/api.ts
import axios from 'axios';

// Configuración base de axios para diferentes microservicios
axios.defaults.headers.common['Content-Type'] = 'application/json';

// Puedes configurar interceptores globales aquí si es necesario
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

