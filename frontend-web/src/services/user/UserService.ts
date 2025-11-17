import axios from 'axios';
import type { 
  UserResponse, 
  UserProfilePictureURLResponse, 
  UserProfileRequest,  
  UserProfileResponse  
} from '../../types/User.types';


const userApi = axios.create ({
    baseURL: "http://localhost:8091/api/v2/users",
    headers: {"Content-Type": "application/json"}
});

export async function getUsers(token: string): Promise<UserResponse[]> {
  try {
    const { data } = await userApi.get<UserResponse[]>('', { // Llama a la baseURL
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


export async function syncUser(subject: string): Promise<UserResponse> {
  try {
    const { data } = await userApi.post<UserResponse>(
      '/me/sync',
      null, // Sin body
      { headers: { 'X-Auth-Subject': subject } }
    );
    return data;
  } catch (err: any) {
    console.error(`Error en syncUser:`, err.response?.data || err.message);
    throw new Error("Fallo al sincronizar el usuario.");
  }
}

export async function createTemporaryUser(): Promise<UserResponse> {
  const tempSub = `temp_guest_${Date.now()}`;
  return syncUser(tempSub);
}

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
}

export async function getProfilePictureUrl(userId: number): Promise<UserProfilePictureURLResponse>{
  const { data } = await userApi.get<UserProfilePictureURLResponse>(`/${userId}/profile-url`)
}
