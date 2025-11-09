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


export async function getUserById(userId: number): Promise<UserResponse> {
  const { data } = await userApi.get<UserResponse>(`/${userId}`);
  return data;
}


async function syncUser(subject: string): Promise<UserResponse> {
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

// class UserService {

//   // async getUserBySub(userSub: string): Promise<UserResponse> {
//   //   const { data } = await axios.get<UserResponse>(`${USER_API_BASE_URL}/subject/${userSub}`);
//   //   return data;
//   // }

//   // Obtener URL de imagen de perfil
//   // async getProfilePictureUrl(userId: number): Promise<UserProfilePictureURLResponse> {
//   //   const { data } = await axios.get<UserProfilePictureURLResponse>(`${USER_API_BASE_URL}/${userId}/profile-url`);
//   //   return data;
//   // }

  
// }
// export const userService = new UserService();