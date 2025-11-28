import axios, { AxiosError, type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
const USER_ID_KEY = 'app_user_id';

// Utilidad simple para guardar el ID
const setUserIdInStorage = (userId: number): void => {
    localStorage.setItem(USER_ID_KEY, userId.toString());
};

// Utilidad para remover el ID (útil en el logout)
export const removeUserIdFromStorage = (): void => {
    localStorage.removeItem(USER_ID_KEY);
};

// Utilidad para obtener el ID (puede ser útil fuera de este servicio)
export const getUserIdFromStorage = (): number | null => {
    const id = localStorage.getItem(USER_ID_KEY);
    return id ? parseInt(id, 10) : null;
};

import type { 
    UserResponse, 
    UserProfilePictureURLResponse, 
    UserProfileRequest, 
    UserProfileResponse 
} from '../../types/User.types';
import { keycloakClient } from '../../components/Auth/keycloak.client';
import { cartService } from '../cart/CartService';
 // Asegúrate que la ruta sea correcta

// -----------------------------------------------------
// 1. CONFIGURACIÓN DEL CLIENTE AXIOS
// -----------------------------------------------------

const userApi: AxiosInstance = axios.create({
    baseURL: "http://localhost:8080/api/v2/users",
    headers: { "Content-Type": "application/json" }
});

// -----------------------------------------------------
// 2. INTERCEPTOR DE SOLICITUD (REQUEST)
// -----------------------------------------------------

userApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    // Si el cliente Keycloak está autenticado y tiene un token, lo inyectamos
    if (keycloakClient.authenticated && keycloakClient.token) {
        config.headers.Authorization = `Bearer ${keycloakClient.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// -----------------------------------------------------
// 3. INTERCEPTOR DE RESPUESTA (REFRESCO DE TOKEN)
// -----------------------------------------------------

// Variable de control para evitar refrescos concurrentes
let isRefreshing = false;
let failedQueue: Array<{ resolve: (value: unknown) => void; reject: (reason?: any) => void; originalRequest: InternalAxiosRequestConfig }> = [];

// Función que procesa todas las solicitudes fallidas una vez que el token es refrescado
const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            // Reintentar la solicitud original con el nuevo token
            prom.resolve(userApi(prom.originalRequest));
        }
    });
    failedQueue = [];
};

userApi.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        // 1. Si NO es un 401, o ya se intentó refrescar, fallar inmediatamente.
        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        // 2. Marcar como reintento y manejar la cola de solicitudes
        originalRequest._retry = true;

        // 3. Si nadie está refrescando, iniciar el proceso
        if (!isRefreshing) {
            isRefreshing = true;
            try {
                // keycloakClient.updateToken(5) intenta refrescar el token si caducará en < 5 segundos
                const refreshed = await keycloakClient.updateToken(300); // 5 minutos (300 segundos) antes de caducar

                if (refreshed && keycloakClient.token) {
                    // Si el refresh fue exitoso, actualiza el header por si acaso y procesa la cola
                    processQueue(null, keycloakClient.token);
                } else {
                    // Si el refresh falló (ej. refresh token caducado), redirigir a login
                    processQueue(new Error("Refresh de token fallido."));
                    keycloakClient.logout();
                }

            } catch (err) {
                // Error al intentar refrescar (ej. conexión perdida)
                processQueue(err);
                keycloakClient.logout();
            } finally {
                isRefreshing = false;
            }
        }

        // 4. Devolver una nueva promesa que se resolverá/rechazará cuando el token sea refrescado
        return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject, originalRequest });
        });
    }
);


// -----------------------------------------------------
// 4. MÉTODOS DE SERVICIO (SIMPLIFICADOS)
// -----------------------------------------------------

/**
 * Los métodos ya NO necesitan recibir el 'token' como argumento.
 * El token se inyecta automáticamente por el interceptor de request.
 */
export async function getUsers(): Promise<UserResponse[]> {
    try {
        // userApi.get('') ahora lleva el token en el header automáticamente
        const { data } = await userApi.get<UserResponse[]>('');
        return data;
    } catch (err: any) {
        console.error("Error al obtener usuarios:", err.response?.data || err.message);
        throw new Error(err.response?.data?.message || "Error al obtener usuarios");
    }
}

export async function getUserById(id: number): Promise<UserResponse> {
    try {
        const { data } = await userApi.get<UserResponse>(`/${id}`);
        // ✨ APLICACIÓN DEL CAMBIO ✨
        // Guardar el ID del usuario sincronizado en localStorage
        setUserIdInStorage(data.id);
        return data;
    } catch (err: any) {
        console.error(`Error al obtener el usuario ${id}:`, err.response?.data || err.message);
        throw new Error(err.response?.data?.message || "Error al obtener el usuario");
    }
}

export async function syncUser(): Promise<UserResponse> {
    try {
        const { data } = await userApi.post<UserResponse>('/me/sync', null);
        setUserIdInStorage(data.id);
        return data;
    } catch (err: any) {
        console.error(`Error en syncUser:`, err.response?.data || err.message);
        throw new Error("Fallo al sincronizar el usuario.");
    }
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
const API_BASE_URL = "http://localhost:8080/api/v2/users";
export async function createTemporaryUser(): Promise<UserResponse> {
    
    // 1. Generar el token temporal/de invitado
    const tempToken = `temp_guest_${Date.now()}`;
    
    try {
        // 2. Usar una llamada directa de axios para EVITAR el interceptor global 
        //    que inyecta el token de Keycloak.
        const { data } = await axios.post<UserResponse>(
            `${API_BASE_URL}/me/sync`, // Endpoint del sync
            null, 
            {
                headers: {
                    'Content-Type': 'application/json',
                    // 3. Añadir el header de autenticación temporal/de invitado
                    'Authorization': `Bearer ${tempToken}` 
                }
            }
        );
        return data;
    } catch (err: any) {
        console.error(`Error al crear usuario temporal:`, err.response?.data || err.message);
        throw new Error("Fallo al crear y sincronizar el usuario temporal.");
    }
}
const setCartIdAfterLogin = async (userId: number): Promise<void> => {
    if (userId > 0) {
        console.log(`🚀 Usuario ${userId} autenticado. Inicializando carrito...`);
        // Llama al método de inicialización del CartService
        await cartService.initializeCart(userId); 
    }
};
export async function getAndSyncUser(): Promise<UserResponse> {
    
    // 1. Obtener datos de Keycloak (simulado con la autenticación actual)
    // En React, confiamos en que keycloakClient ya está inicializado y autenticado.
    if (!keycloakClient.authenticated || !keycloakClient.token) {
        throw new Error("Keycloak no autenticado o token no disponible.");
    }
    
    // NOTA: La lógica de Keycloak en React típicamente maneja la obtención del
    // token y el UserInfo en el wrapper de autenticación, no en el servicio.
    // Aquí solo necesitamos el token para la llamada al backend.

    try {
        // 2. SINCRONIZAR CON EL API GATEWAY PARA OBTENER EL ID INTERNO
        // Reutilizamos la función syncUser() existente que usa userApi.post('/me/sync', null)
        const syncedUser = await syncUser(); // Este ya guarda el ID en localStorage.

        console.log(`✅ Sincronización finalizada. ID interno: ${syncedUser.id}`);

        // 3. LÓGICA DE CARRITO: Llama a la función auxiliar
        const internalId = syncedUser.id; 
        
        // Asumiendo que UserResponse.id es un 'number'
        await setCartIdAfterLogin(internalId); 
        
        return syncedUser;
        
    } catch (err: any) {
        // Manejo de errores (similar a DioException catch)
        console.error('❌ Error en getAndSyncUser (Sync o Cart):', err.message);
        throw new Error("Fallo en la sincronización del usuario o inicialización del carrito.");
    }
}