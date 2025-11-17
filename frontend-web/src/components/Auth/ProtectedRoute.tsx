import { useKeycloak } from "@react-keycloak/web";
import { Navigate, useLocation } from "react-router-dom";
import { Loader2 } from "lucide-react"; 

interface Props {
  children: JSX.Element;
  role?: string; 
}

export default function ProtectedRoute({ children, role }: Props) {
  const { keycloak, initialized } = useKeycloak();
  const location = useLocation();

  if (!initialized) {
    return (
      <div className="flex items-center justify-center h-screen bg-primary">
        <Loader2 className="w-16 h-16 animate-spin text-secondary" />
      </div>
    );
  }

  if (!keycloak.authenticated) {
    keycloak.login({ redirectUri: window.location.origin + location.pathname });
    return null; 
  }

  if (role && !keycloak.hasRealmRole(role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}