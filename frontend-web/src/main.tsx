import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import App from './App.tsx'

import { ReactKeycloakProvider } from '@react-keycloak/web'
import Keycloak from 'keycloak-js'

const keycloakClient = new Keycloak({
  url: 'http://localhost:8080', 
  realm: 'springshop-realm', 
  clientId: 'springshop-frontend' 
});

const initOptions = {
  onLoad: 'check-sso', 
  silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
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