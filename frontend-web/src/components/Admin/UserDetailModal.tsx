// src/components/admin/UserDetailModal.tsx (NUEVO ARCHIVO)
import React, { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { getUserById } from '../../services/user/UserService';
import type { UserResponse } from '../../types/User.types';
import { Loader2, X, AlertTriangle, Save } from 'lucide-react';

interface Props {
  userId: number | null; // El ID del usuario a editar
  onClose: () => void;     // Función para cerrar el modal
  onSave: () => void;      // Función para refrescar la lista (para el futuro)
}

export default function UserDetailModal({ userId, onClose, onSave }: Props) {
  const { keycloak, initialized } = useKeycloak();
  const [user, setUser] = useState<UserResponse | null>(null);
  const [username, setUsername] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  // Efecto que carga los datos del usuario cuando el modal se abre
  useEffect(() => {
    if (!userId || !initialized || !keycloak.token) {
      setUser(null); // Limpiar si no hay ID o token
      return;
    }

    const loadUserDetails = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getUserById(userId, keycloak.token);
        setUser(data);
        setUsername(data.username || ''); // Poner el username en el estado del formulario
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadUserDetails();
  }, [userId, initialized, keycloak.token]);

  // Manejador para el futuro (cuando el backend esté listo)
  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);

    // --- LÓGICA DE ACTUALIZACIÓN (PARA EL FUTURO) ---
    // try {
    //   await updateUserService(userId, { username }, keycloak.token);
    //   onSave(); // Refresca la lista en AdminUsers.tsx
    //   onClose(); // Cierra el modal
    // } catch (err: any) {
    //   setError(err.message);
    // } finally {
    //   setIsSaving(false);
    // }
    // -----------------------------------------------

    // Por ahora, solo simulamos y cerramos
    alert("Funcionalidad de guardado aún no implementada en el backend.");
    setIsSaving(false);
  };

  if (!userId) {
    return null;
  }

  return (
    // Overlay
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-primary bg-opacity-10 backdrop-blur-sm p-4"
      onClick={onClose}
    >
      {/* Contenedor del Modal */}
      <div
        className="relative w-full max-w-lg p-6 bg-primary rounded-2xl border border-secondary text-gray-100 shadow-[0_0_15px_rgba(137,254,0,.7)]"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Botón de Cerrar */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-white z-10"
        >
          <X size={24} />
        </button>

        {/* --- Contenido --- */}
        {loading && (
          <div className="flex items-center justify-center h-48">
            <Loader2 className="w-12 h-12 animate-spin text-secondary" />
          </div>
        )}

        {error && (
          <div className="flex flex-col items-center justify-center h-48 bg-red-900/20 border border-red-700 p-6 rounded-lg">
            <AlertTriangle className="w-12 h-12 text-red-500 mb-4" />
            <h2 className="text-xl font-bold text-red-400">Error al Cargar Usuario</h2>
            <p className="text-red-300">{error}</p>
          </div>
        )}

        {user && !loading && !error && (
          <form onSubmit={handleSave}>
            <h2 className="text-3xl font-bold text-secondary mb-6">
              Editar Usuario
            </h2>
            
            <div className="space-y-4">
              {/* Campo ID (Solo lectura) */}
              <div>
                <label className="block text-sm font-medium text-gray-400">User ID</label>
                <input
                  type="text"
                  value={user.id}
                  disabled
                  className="w-full p-3 mt-1 rounded bg-gray-800 border border-gray-700 text-gray-500 cursor-not-allowed"
                />
              </div>

              {/* Campo Sub (Solo lectura) */}
              <div>
                <label className="block text-sm font-medium text-gray-400">Auth Subject (Sub)</label>
                <input
                  type="text"
                  value={user.sub}
                  disabled
                  className="w-full p-3 mt-1 rounded bg-gray-800 border border-gray-700 text-gray-500 cursor-not-allowed"
                />
              </div>

              {/* Campo Username (Editable) */}
              <div>
                <label htmlFor="username" className="block text-sm font-medium text-gray-400">
                  Username
                </label>
                <input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full p-3 mt-1 rounded bg-gray-800 border border-gray-600 text-white"
                />
              </div>
            </div>

            {/* --- Botón de Guardar (Deshabilitado por ahora) --- */}
            <div className="pt-6 mt-6 border-t border-gray-700">
              <button
                type="submit"
                disabled={true} // <-- ¡AQUÍ! Deshabilitado hasta que el backend esté listo
                className="w-full flex items-center justify-center gap-2 bg-secondary text-primary font-bold py-3 rounded-full transition-all disabled:bg-gray-500 disabled:text-gray-800 disabled:cursor-not-allowed"
              >
                {isSaving ? (
                  <Loader2 className="w-5 h-5 animate-spin" />
                ) : (
                  <Save size={18} />
                )}
                Guardar Cambios (Deshabilitado)
              </button>
            </div>
            
          </form>
        )}
      </div>
    </div>
  );
}