// components/User/UserInfo.tsx
import React from 'react';
import { useUser } from '../../hooks/useUser';
import { User, Loader2 } from 'lucide-react';

interface UserInfoProps {
  userId: number;
}

export const UserInfo: React.FC<UserInfoProps> = ({ userId }) => {
  const { user, loading, error } = useUser(userId);

  if (loading) {
    return (
      <div className="flex items-center text-gray-600">
        <Loader2 className="w-4 h-4 animate-spin mr-2" />
        <span>Cargando información...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-3">
        <p className="text-red-800 text-sm">Error: {error}</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="text-gray-500">
        No se encontró información del usuario
      </div>
    );
  }

  return (
    <div className="flex items-center space-x-3 bg-white rounded-lg p-4 shadow-sm">
      <div className="flex-shrink-0">
        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
          <User className="w-5 h-5 text-blue-600" />
        </div>
      </div>
      
      <div className="flex-1">
        <h3 className="font-semibold text-gray-900">
          {user.profile?.firstName && user.profile?.lastName 
            ? `${user.profile.firstName} ${user.profile.lastName}`
            : user.username || `Usuario #${user.id}`
          }
        </h3>
        
        {user.profile?.firstName && (
          <p className="text-sm text-gray-600">
            {user.username}
          </p>
        )}
      </div>
      
      {user.profile?.profilePicUrl && (
        <div className="flex-shrink-0">
          <img 
            src={user.profile.profilePicUrl} 
            alt="Profile" 
            className="w-10 h-10 rounded-full object-cover"
          />
        </div>
      )}
    </div>
  );
};