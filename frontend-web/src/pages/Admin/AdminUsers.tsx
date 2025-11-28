import { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { getUsers } from '../../services/user/UserService';
import type { UserResponse } from '../../types/User.types';
import { Loader2, AlertTriangle, User, Edit } from 'lucide-react';
import UserDetailModal from '../../components/Admin/UserDetailModal';
import ExportButtons from '../../components/Shared/Common/ExportButtons';

const reportColumns = [
  { header: 'ID Usuario', accessor: 'id' },
  { header: 'Username', accessor: 'username' },
  { header: 'Auth Subject (Sub)', accessor: 'sub' }
];

export default function AdminUsers() {
  const { keycloak, initialized } = useKeycloak();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [refreshSignal, setRefreshSignal] = useState(false);
  
  const triggerRefresh = () => setRefreshSignal(prev => !prev);

  useEffect(() => {
    // Verificamos inicialización y autenticación básica
    if (!initialized || !keycloak.authenticated) {
       // Si no está inicializado, esperamos. Si inicializó pero no hay token/auth, detenemos carga.
       if (initialized && !keycloak.authenticated) {
         setLoading(false);
         setError("No estás autenticado.");
       }
       return;
    }

    const loadUsers = async () => {
      setLoading(true);
      setError(null);
      try {
        // CORRECCIÓN: Llamamos a getUsers() SIN argumentos.
        // El interceptor en UserService inyecta el token automáticamente.
        const data = await getUsers();
        setUsers(data);
      } catch (err: any) {
        setError(err.message || "Un error desconocido ocurrió.");
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, [initialized, keycloak.authenticated, refreshSignal]); // Dependencia keycloak.authenticated es más segura que .token

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
  
  return (
    <> 
      <div>
        {/* --- Título y botones --- */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-secondary">
            Administrar Usuarios
          </h1>
          {/* Botones de exportación */}
          {!loading && users.length > 0 && (
            <ExportButtons
              data={users}
              filename="reporte_usuarios"
              columns={reportColumns}
            />
          )}
        </div>
        
        {/* Contenedor de Tarjetas de Usuario */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {users.length > 0 ? (
            users.map((user) => (
              <div 
                key={user.id} 
                className="bg-primary border border-gray-700 rounded-lg shadow-md p-4 flex flex-col justify-between"
              >
                <div>
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
                  <div className="space-y-1 text-sm text-gray-300 border-t border-gray-700 pt-3">
                    <div className="flex justify-between">
                      <span className="text-gray-500">Sub (Auth ID):</span>
                      <span className="truncate max-w-[150px]" title={user.sub}>{user.sub}</span>
                    </div>
                  </div>
                </div>

                <button 
                  onClick={() => setSelectedUserId(user.id)}
                  className="mt-4 w-full flex items-center justify-center gap-2 text-blue-500 hover:text-white border border-blue-500 hover:bg-blue-600 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2 text-center transition-colors"
                >
                  <Edit size={16} />
                  Editar
                </button>
              </div>
            ))
          ) : (
            <div className="text-center p-8 text-gray-500 bg-primary border border-gray-700 rounded-lg col-span-full">
              No se encontraron usuarios.
            </div>
          )}
        </div>
      </div>
      
      {/* Modal */}
      <UserDetailModal
        userId={selectedUserId}
        onClose={() => setSelectedUserId(null)}
        onSave={triggerRefresh}
      />
    </>
  );
}