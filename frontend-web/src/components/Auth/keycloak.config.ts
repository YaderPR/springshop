

// --- Constantes de Configuración ---

// Usar variables de entorno para las credenciales.
// Si no están definidas, se usarán valores por defecto para desarrollo.
export const KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:9090';
export const KEYCLOAK_REALM = import.meta.env.VITE_KEYCLOAK_REALM || 'Springshop-realm';
export const KEYCLOAK_CLIENT_ID = import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'springshop-frontend';

// Opciones de Inicialización para ReactKeycloakProvider
export const INIT_OPTIONS: Keycloak.KeycloakInitOptions = {
    // Recomendado: 'check-sso' o 'silent-check-sso' para SPAs.
    onLoad: 'check-sso', 
    checkLoginIframe: false,
    pkceMethod: 'S256',
};

// Configuración inicial del Cliente Keycloak
export const keycloakConfig: Keycloak.KeycloakConfig = {
    url: KEYCLOAK_URL,
    realm: KEYCLOAK_REALM,
    clientId: KEYCLOAK_CLIENT_ID,
};