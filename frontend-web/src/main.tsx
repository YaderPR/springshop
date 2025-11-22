import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import './index.css'
import App from './App.tsx'
import { ReactKeycloakProvider } from '@react-keycloak/web'
import Keycloak from 'keycloak-js'

const storedToken = localStorage.getItem('kc_token');
const storedRefreshToken = localStorage.getItem('kc_refreshToken');

// const keycloakConfig = new KeycloakConfig

const keycloakClient = new Keycloak({
  url: 'http://localhost:9090', 
  realm: 'Springshop-realm',
  clientId: 'springshop-frontend', 
});

const initOptions = {
  onLoad: 'check-sso',
  checkLoginIframe: false,
  pkceMethod: 'S256',
};

const handleTokens = (tokens: { token?: string; refreshToken?: string }) => {
  if (tokens.token) {
    localStorage.setItem('kc_token', tokens.token);
  }
  if (tokens.refreshToken) {
    localStorage.setItem('kc_refreshToken', tokens.refreshToken);
  }
};

const originalLogout = keycloakClient.logout;
keycloakClient.logout = (options) => {
  localStorage.removeItem('kc_token');
  localStorage.removeItem('kc_refreshToken');
  return originalLogout.call(keycloakClient, options);
};

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ReactKeycloakProvider
      authClient={keycloakClient}
      initOptions={initOptions}
    >
      <App />
    </ReactKeycloakProvider>
  </StrictMode>,
)