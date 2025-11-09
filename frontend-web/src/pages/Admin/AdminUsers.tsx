import React, { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { getUsers } from '../../services/user/UserService';
import type { UserResponse } from '../../types/User.types';
import { Loader2, AlertTriangle, User, ShieldCheck } from 'lucide-react';

export default function AdminUsers() {
  const { keycloak, initialized } = useKeycloak();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Carga de datos, idéntica a AdminOrders
  useEffect(() => {
    if (!initialized) {
      return;
    }
    if (!keycloak.token) {
      setLoading(false);
      setError("No se encontró token de autenticación.");
      return;
    }

    const loadUsers = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getUsers(keycloak.token);
        setUsers(data);
      } catch (err: any) {
        setError(err.message || "Un error desconocido ocurrió.");
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
    
  }, [initialized, keycloak.token]);

  // --- Renderizado Condicional ---
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
        <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
        <div>
          <h2 className="text-xl font-bold text-red-400">Error al Cargar Usuarios</h2>
          <p className="text-red-300">{error}</p>
        </div>
      </div>
    );
  }

  // --- Renderizado de Éxito ---
  return (
    <div>
      <h1 className="text-3xl font-bold text-secondary mb-6">
        Administrar Usuarios
      </h1>
      
      {/* Contenedor de Tarjetas de Usuario */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {users.length > 0 ? (
          users.map((user) => (
            <div 
              key={user.id} 
              className="bg-primary border border-gray-700 rounded-lg shadow-md p-4"
            >
              <div className="flex items-center gap-4 mb-3">
                <div className="flex-shrink-0 p-2 bg-gray-700 rounded-full">
                  <User className="w-6 h-6 text-secondary" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white whitespace-nowrap">
                    {user.username || "Usuario sin nombre"}
                  </h3>
                  <span className="text-sm text-gray-400">
                    ID: {user.id}
                  </span>
                </div>
              </div>

              {/* Detalles del Usuario */}
              <div className="space-y-1 text-sm text-gray-300 border-t border-gray-700 pt-3">
                <p className="flex justify-between">
                  <span className="text-gray-500">Sub (Auth ID):</span>
                  <span className="truncate max-w-[200px]">{user.sub}</span>
                </p>
                
                {/* (En el futuro, podríamos mostrar los roles aquí) */}
                {/* <p className="flex justify-between">
                  <span className="text-gray-500">Roles:</span>
                  <span className="text-secondary font-medium">ADMIN</span>
                </p> */}
              </div>

              {/* (En el futuro, podemos añadir un botón de "Editar Roles") */}
              {/* <button className="mt-4 w-full text-blue-500">
                Editar
              </button> */}
            </div>
          ))
        ) : (
          <div className="text-center p-8 text-gray-500 bg-primary border border-gray-700 rounded-lg col-span-full">
            No se encontraron usuarios.
          </div>
        )}
      </div>
    </div>
  );
}