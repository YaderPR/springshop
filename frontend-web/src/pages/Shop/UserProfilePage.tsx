import React from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { User, Mail, Shield, Key } from 'lucide-react';

const UserProfilePage = () => {
  const { keycloak } = useKeycloak();
  
  // Extract user data safely
  const userData = {
    username: keycloak.tokenParsed?.preferred_username || '',
    email: keycloak.tokenParsed?.email || '',
    firstName: keycloak.tokenParsed?.given_name || '',
    lastName: keycloak.tokenParsed?.family_name || '',
    fullName: keycloak.tokenParsed?.name || '',
    roles: keycloak.realmAccess?.roles || [],
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-black bg-[radial-gradient(circle_at_center,#181e11_0%,#000000_60%)] py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        {/* Header Section */}
        <div className="text-center mb-12">
          <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-secondary/10 mb-6 ring-2 ring-secondary/50 shadow-[0_0_20px_rgba(132,204,22,0.3)]">
            <User size={40} className="text-secondary" />
          </div>
          <h1 className="text-4xl font-bold text-white mb-2 tracking-tight">
            Mi Perfil
          </h1>
          <p className="text-gray-400 text-lg">
            Información de tu cuenta registrada
          </p>
        </div>

        {/* Main Card */}
        <div className="bg-gray-800/50 backdrop-blur-xl rounded-2xl border border-gray-700/50 shadow-xl overflow-hidden">
          {/* Banner Decoration */}
          <div className="h-2 bg-gradient-to-r from-secondary via-lime-400 to-secondary opacity-75" />
          
          <div className="p-8 space-y-8">
            {/* Personal Information Section */}
            <div>
              <h2 className="text-xl font-semibold text-white mb-6 flex items-center gap-2">
                <User size={20} className="text-secondary" />
                Información Personal
              </h2>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-400 ml-1">
                    Nombre
                  </label>
                  <div className="relative group">
                    <input
                      type="text"
                      value={userData.firstName}
                      readOnly
                      className="w-full bg-gray-900/50 border border-gray-700 text-gray-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-secondary/50 focus:border-transparent transition-all cursor-default hover:bg-gray-900/70"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-400 ml-1">
                    Apellido
                  </label>
                  <div className="relative group">
                    <input
                      type="text"
                      value={userData.lastName}
                      readOnly
                      className="w-full bg-gray-900/50 border border-gray-700 text-gray-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-secondary/50 focus:border-transparent transition-all cursor-default hover:bg-gray-900/70"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div className="h-px bg-gray-700/50" />

            {/* Account Details Section */}
            <div>
              <h2 className="text-xl font-semibold text-white mb-6 flex items-center gap-2">
                <Shield size={20} className="text-secondary" />
                Detalles de la Cuenta
              </h2>

              <div className="space-y-6">
                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-400 ml-1">
                    Nombre de Usuario
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <User size={18} className="text-gray-500" />
                    </div>
                    <input
                      type="text"
                      value={userData.username}
                      readOnly
                      className="w-full bg-gray-900/50 border border-gray-700 text-gray-300 rounded-xl pl-11 pr-4 py-3 focus:outline-none focus:ring-2 focus:ring-secondary/50 focus:border-transparent transition-all cursor-default hover:bg-gray-900/70"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-400 ml-1">
                    Correo Electrónico
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Mail size={18} className="text-gray-500" />
                    </div>
                    <input
                      type="text"
                      value={userData.email}
                      readOnly
                      className="w-full bg-gray-900/50 border border-gray-700 text-gray-300 rounded-xl pl-11 pr-4 py-3 focus:outline-none focus:ring-2 focus:ring-secondary/50 focus:border-transparent transition-all cursor-default hover:bg-gray-900/70"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-400 ml-1">
                    ID de Usuario (Keycloak)
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Key size={18} className="text-gray-500" />
                    </div>
                    <input
                      type="text"
                      value={keycloak.subject || ''}
                      readOnly
                      className="w-full bg-gray-900/50 border border-gray-700 text-gray-500 rounded-xl pl-11 pr-4 py-3 font-mono text-sm focus:outline-none focus:ring-2 focus:ring-secondary/50 focus:border-transparent transition-all cursor-default hover:bg-gray-900/70"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          {/* Footer Info */}
          <div className="bg-gray-900/50 px-8 py-4 border-t border-gray-700/50">
            <p className="text-xs text-center text-gray-500">
              Esta información es gestionada por el sistema de autenticación centralizado y no puede ser modificada directamente aquí.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfilePage;
