import axios from 'axios';
import type { 
  UserResponse, 
  UserProfilePictureURLResponse, 
  UserProfileRequest,  
  UserProfileResponse  
} from '../../types/User.types';

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

  /**
   * Crea un nuevo usuario "invitado" llamando al endpoint público
   * POST /api/v2/users/user-profile.
   * Este es el endpoint que tu backend (UserController.java) expone
   * sin seguridad de headers.
   */
  // async createGuestUser(profileData: UserProfileRequest): Promise<UserProfileResponse> {
  //   try {
  //     const { data } = await axios.post<UserProfileResponse>(
  //       `${USER_API_BASE_URL}/user-profile`,
  //       profileData
  //     );
      
  //     // 'data' será el UserProfileResponse.
  //     // Asumimos que este objeto de respuesta contiene el 'id' del usuario
  //     // (o un 'userId') que necesitamos para el 'useCartManager'.
  //     return data;

  //   } catch (err: any) {
  //     console.error("Error al crear usuario invitado:", err.response?.data || err.message);
  //     throw new Error("No se pudo crear el usuario invitado.");
  //   }
  // }

async syncUser(subject: string): Promise<UserResponse> {
    try {
      const { data } = await axios.post<UserResponse>(
        `${USER_API_BASE_URL}/me/sync`,
        null, // No enviamos BODY
        {
          headers: {
            'X-Auth-Subject': subject // <-- ¡LA SOLUCIÓN! Enviamos el 'sub' como header.
          }
        }
      );
      return data;
    } catch (err: any) {
      console.error(`Error en syncUser con sub=${subject}:`, err.response?.data || err.message);
      throw new Error("Fallo al sincronizar el usuario.");
    }
  }

  async createTemporaryUser(): Promise<UserResponse> {
    const tempSub = `temp_guest_${Date.now()}`;
    const user = await this.syncUser(tempSub);
    return user;
  }
  
}
export const userService = new UserService();