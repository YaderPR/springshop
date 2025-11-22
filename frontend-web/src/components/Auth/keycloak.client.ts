// keycloak.client.ts

import { keycloakConfig } from './keycloak.config';
import Keycloak from 'keycloak-js';

const TOKEN_KEY = 'kc_token';
const REFRESH_TOKEN_KEY = 'kc_refreshToken';

// 1. Crear la instancia del cliente Keycloak
export const keycloakClient = new Keycloak(keycloakConfig);

// 2. Cargar tokens desde localStorage si existen al crear el cliente
const storedToken = localStorage.getItem(TOKEN_KEY);
const storedRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

keycloakClient.token = storedToken || undefined;
keycloakClient.refreshToken = storedRefreshToken || undefined;


// 3. Extender la función de logout para limpiar localStorage
const originalLogout = keycloakClient.logout;
keycloakClient.logout = (options) => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    return originalLogout.call(keycloakClient, options);
};

// 4. Función de manejo de tokens para guardar en localStorage
interface Tokens {
    token?: string;
    refreshToken?: string;
}

export const handleTokens = (tokens: Tokens): void => {
    if (tokens.token) {
        localStorage.setItem(TOKEN_KEY, tokens.token);
    }
    if (tokens.refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    }
    // Opcionalmente: Limpiar si un token se anula (aunque Keycloak lo gestiona)
};