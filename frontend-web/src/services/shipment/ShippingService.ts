import axios, { AxiosError, type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { keycloakClient } from '../../components/Auth/keycloak.client';

// -----------------------------------------------------
// 1. TIPOS DE DATOS
// -----------------------------------------------------

export interface ShippingAddressRequest {
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
    phoneNumber?: string;
    userId: number; // Aunque el backend debería ignorar esto, se mantiene para tipado
}

export interface ShippingAddressResponse extends ShippingAddressRequest {
    id: number;
}

const API_SHIPPING_URL = "http://localhost:8080/api/v2/addresses";

// -----------------------------------------------------
// 2. CONFIGURACIÓN DEL CLIENTE AXIOS CON INTERCEPTORES
// -----------------------------------------------------

const shippingApi: AxiosInstance = axios.create({
    baseURL: API_SHIPPING_URL,
    headers: { "Content-Type": "application/json" }
});

// Variables de control para el refresco de token (duplicadas para modularidad)
let isRefreshing = false;
let failedQueue: Array<{ 
    resolve: (value: unknown) => void; 
    reject: (reason?: any) => void; 
    originalRequest: InternalAxiosRequestConfig 
}> = [];

const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            // Reintentar usando la instancia 'shippingApi'
            prom.resolve(shippingApi(prom.originalRequest)); 
        }
    });
    failedQueue = [];
};

// INTERCEPTOR DE SOLICITUD: Inyecta el Token
shippingApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    if (keycloakClient.authenticated && keycloakClient.token) {
        config.headers.Authorization = `Bearer ${keycloakClient.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// INTERCEPTOR DE RESPUESTA: Maneja el Refresco de Token (401)
shippingApi.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if (!isRefreshing) {
            isRefreshing = true;
            try {
                const refreshed = await keycloakClient.updateToken(300); 

                if (refreshed) {
                    processQueue(null, keycloakClient.token);
                } else {
                    processQueue(new Error("Sesión expirada."));
                    keycloakClient.logout();
                }

            } catch (err) {
                processQueue(err);
                keycloakClient.logout();
            } finally {
                isRefreshing = false;
            }
        }

        return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject, originalRequest });
        });
    }
);

// -----------------------------------------------------
// 3. MÉTODOS DE SERVICIO (USANDO 'shippingApi')
// -----------------------------------------------------

export async function createShippingAddress (
    addressData: ShippingAddressRequest
): Promise<ShippingAddressResponse> {
    
    // El 'userId' debe ser gestionado por el backend a partir del JWT.
    // El cuerpo de la petición simplemente envía los datos de la dirección.
    const payload = addressData; 

    try {
        // Usamos la instancia 'shippingApi', el token se inyecta automáticamente.
        const { data } = await shippingApi.post<ShippingAddressResponse>(
            '', // POST a la URL base /api/v2/addresses
            payload
        );
        return data;
    } catch (err: any) {
        // Manejo de errores de Axios para una respuesta más limpia
        const errorMessage = err.response?.data?.message || `Error al crear dirección: ${err.message}`;
        console.error("Error al crear dirección de envío:", err.response?.data || err.message);
        throw new Error(errorMessage);
    }
}

// Puedes añadir otros métodos aquí (ej. getAddressesByUser, updateAddress, deleteAddress)
// para reutilizar la lógica de autenticación y refresco.

export async function getAddressesByUser(userId: number): Promise<ShippingAddressResponse[]> {
    // Asumiendo que el endpoint es GET /api/v2/addresses/user/{userId}
    // y que el backend verifica que el userId coincida con el usuario del token.
    try {
        const { data } = await shippingApi.get<ShippingAddressResponse[]>(`/user/${userId}`);
        return data;
    } catch (err: any) {
        const errorMessage = err.response?.data?.message || `Error al obtener direcciones: ${err.message}`;
        throw new Error(errorMessage);
    }
}