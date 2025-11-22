import axios from 'axios';
import type { 
  UserResponse, 
  UserProfilePictureURLResponse, 
  UserProfileRequest,  
  UserProfileResponse  
} from '../../types/User.types';

const userApi = axios.create ({
  baseURL: "http://localhost:8080/api/v2/users",
  headers: {"Content-Type": "application/json"}
});

// --- Métodos existentes (sin cambios, solo contexto) ---

export async function getUsers(token: string): Promise<UserResponse[]> {
  try {
    const { data } = await userApi.get<UserResponse[]>('', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return data;
  } catch (err: any) {
    console.error("Error al obtener usuarios:", err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al obtener usuarios");
  }
}

export async function getUserById(id: number, token: string): Promise<UserResponse> {
  try {
    const { data } = await userApi.get<UserResponse>(`/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return data;
  } catch (err: any) {
    console.error(`Error al obtener el usuario ${id}:`, err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al obtener el usuario");
  }
}

// --- CAMBIOS APLICADOS AQUÍ ---

export async function syncUser(token: string): Promise<UserResponse> {
  try {
    // Ahora usamos el token en el header Authorization estándar
    const { data } = await userApi.post<UserResponse>(
      '/me/sync',
      null, // Body se mantiene vacío si el backend solo necesita el token para extraer la info
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    return data;
  } catch (err: any) {
    console.error(`Error en syncUser:`, err.response?.data || err.message);
    throw new Error("Fallo al sincronizar el usuario.");
  }
}

// NOTA: Esta función enviará "Bearer temp_guest_..." al backend.
// Asegúrate de que tu backend acepte tokens que no sean JWT reales para este caso.
export async function createTemporaryUser(): Promise<UserResponse> {
  const tempToken = `temp_guest_${Date.now()}`; 
  return syncUser(tempToken);
}

// --- Resto de métodos ---

export async function createGuestUser(profileData: UserProfileRequest): Promise<UserProfileResponse> {
  try {
    const { data } = await userApi.post<UserProfileResponse>(
      "/user-profile",
      profileData
    );
    return data;
  } catch (err: any) {
    console.error("Error al crear usuario invitado:", err.response?.data || err.message);
    throw new Error("No se pudo crear el usuario invitado.");
  }
}

export async function getUserBySub(userSub: string): Promise<UserResponse> {
  const { data } = await userApi.get<UserResponse>(`/${userSub}`)
  return data;
}

export async function getProfilePictureUrl(userId: number): Promise<UserProfilePictureURLResponse>{
  const { data } = await userApi.get<UserProfilePictureURLResponse>(`/${userId}/profile-url`)
  return data;
}