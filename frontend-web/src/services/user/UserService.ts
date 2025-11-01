import axios from 'axios';
import type { UserResponse, UserProfilePictureURLResponse, UserSync } from '../../types/User.types';

const USER_API_BASE_URL = 'http://localhost:8091/api/v2/users';

class UserService {
  async getUserById(userId: number): Promise<UserResponse> {
    const { data } = await axios.get<UserResponse>(`${USER_API_BASE_URL}/${userId}`);
    return data;
  }

  async getUserBySub(userSub: string): Promise<UserResponse> {
    const { data } = await axios.get<UserResponse>(`${USER_API_BASE_URL}/subject/${userSub}`);
    return data;
  }

  // Obtener URL de imagen de perfil
  async getProfilePictureUrl(userId: number): Promise<UserProfilePictureURLResponse> {
    const { data } = await axios.get<UserProfilePictureURLResponse>(`${USER_API_BASE_URL}/${userId}/profile-url`);
    return data;
  }

  // Sincronizar usuario (crear si no existe)
  async syncUser(subject: string): Promise<UserResponse> {
    const { data } = await axios.post<UserResponse>(`${USER_API_BASE_URL}/me/sync`, { subject });
    return data;
  }

  // Método helper para crear un usuario temporal (para testing)
  async createTemporaryUser(username: string): Promise<UserResponse> {
    // Generar un sub temporal
    const tempSub = `temp_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    
    // Primero sincronizamos para crear el usuario
    const user = await this.syncUser(tempSub);
    
    // En una implementación real, aquí actualizarías el username
    // Pero como tu API no tiene endpoint para update, lo dejamos así
    
    return user;
  }
}

export const userService = new UserService();