// main.tsx (o index.tsx)

import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ReactKeycloakProvider } from '@react-keycloak/web';

import './index.css';
import App from './App.tsx';
import { handleTokens, keycloakClient } from './components/Auth/keycloak.client.ts';
import { INIT_OPTIONS } from './components/Auth/keycloak.config.ts';


const rootElement = document.getElementById('root');

if (!rootElement) {
    throw new Error('El elemento root no fue encontrado en el DOM.');
}

createRoot(rootElement).render(
    <StrictMode>
        <ReactKeycloakProvider
            authClient={keycloakClient}
            initOptions={INIT_OPTIONS}
            // Usar onTokens para persistir el token al obtener uno nuevo o refrescar
            onTokens={handleTokens}
        >
            <App />
        </ReactKeycloakProvider>
    </StrictMode>,
);