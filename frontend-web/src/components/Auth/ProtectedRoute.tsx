import { useKeycloak } from "@react-keycloak/web";
import { Navigate, useLocation } from "react-router-dom";
import { Loader2 } from "lucide-react"; // Para un spinner de carga

interface Props {
  children: JSX.Element;
  role: string; // El rol que queremos verificar, ej. "admin"
}

export default function ProtectedRoute({ children, role }: Props) {
  const { keycloak, initialized } = useKeycloak();
  const location = useLocation();

  // --- 1. Esperando a que Keycloak se inicialice ---
  // Mientras 'initialized' es false, 'keycloak.authenticated' no es fiable.
  if (!initialized) {
    return (
      <div className="flex items-center justify-center h-screen bg-primary">
        <Loader2 className="w-16 h-16 animate-spin text-secondary" />
      </div>
    );
  }

  // --- 2. El usuario NO está autenticado ---
  // Si no está logueado, lo mandamos a la página de login de Keycloak.
  // Guardamos la página actual ('location') para que Keycloak nos devuelva aquí.
  if (!keycloak.authenticated) {
    keycloak.login({ redirectUri: window.location.origin + location.pathname });
    return null; // No renderizar nada mientras redirige
  }

  // --- 3. El usuario ESTÁ autenticado, pero NO tiene el rol ---
  // (Asegúrate de que tu rol en Keycloak se llame 'admin')
  if (!keycloak.hasRealmRole(role)) {
    // Si es un usuario normal, lo mandamos a la página de inicio.
    // No tiene nada que hacer en /admin.
    return <Navigate to="/" replace />;
  }

  // --- 4. ¡Éxito! ---
  // El usuario está autenticado Y tiene el rol de admin.
  // Renderizamos los 'children' (que será tu <AdminLayout />).
  return children;
}