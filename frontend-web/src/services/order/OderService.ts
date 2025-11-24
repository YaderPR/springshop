import axios, { AxiosError, type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import type { CheckoutRequestDto, CheckoutResponseDto, OrderResponseDto, updateOrderStatus } from '../../types/Order.types'; 
import { keycloakClient } from '../../components/Auth/keycloak.client'; // Asegúrate de la ruta correcta

const orderApi: AxiosInstance = axios.create({
  baseURL: "http://localhost:8080/api/v2/orders",
  headers: { "Content-Type" : "application/json" },
});

// -----------------------------------------------------
// 1. INTERCEPTORES (Inyección y Refresco de Token)
// -----------------------------------------------------

// Variables de control para el refresco de token
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
            // Reintentar usando la instancia 'orderApi'
            prom.resolve(orderApi(prom.originalRequest)); 
        }
    });
    failedQueue = [];
};

// Interceptor de Solicitud: Inyecta el Token
orderApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    // Si el cliente Keycloak está autenticado, inyectamos el token
    if (keycloakClient.authenticated && keycloakClient.token) {
        config.headers.Authorization = `Bearer ${keycloakClient.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Interceptor de Respuesta: Maneja el Refresco de Token (401)
orderApi.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        // Si no es un 401 o ya se reintentó, fallar
        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if (!isRefreshing) {
            isRefreshing = true;
            try {
                // Intenta refrescar el token
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
// 2. MÉTODOS DE SERVICIO (Sin el argumento 'token')
// -----------------------------------------------------

/**
 * Obtiene las órdenes del usuario autenticado.
 */
export async function getOrders(): Promise<OrderResponseDto[]> {
  try {
    // El token se inyecta automáticamente por el interceptor
    const { data } = await orderApi.get<OrderResponseDto[]>('');
    return data;
  } catch (err: any) {
    console.error("Error al obtener órdenes:", err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al obtener órdenes ");
  }
}

/**
 * Obtiene una orden específica por ID (debe ser del usuario autenticado).
 */
export async function getOrderById(id: number): Promise<OrderResponseDto> {
  try {
    const { data } = await orderApi.get<OrderResponseDto>(`/${id}`);
    return data;
  } catch (err: any) {
    console.error(`Error al obtener la orden ${id}:`, err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al obtener la orden");
  }
}

/**
 * Inicia el proceso de checkout.
 */
export async function startCheckout(
  checkoutData: CheckoutRequestDto, 
): Promise<CheckoutResponseDto> {
  try {
  console.log("CheckoutPage - cartId:", checkoutData.cartId, "userId:", checkoutData.userId, "addressId:", checkoutData.addressId, "startCheckout");
    const { data } = await orderApi.post<CheckoutResponseDto>(
      '/checkout', 
      checkoutData,
    );
    return data;
  } catch (error: any) {
    console.error("Error al iniciar checkout:", error.response?.data || error.message);
    throw new Error(error.response?.data?.message || 'Error al iniciar el checkout');
  }
}

/**
 * Actualiza el estado de una orden. (Probablemente requiere privilegios de Admin/Staff)
 */
export async function updateOrderStatus(
  id: number, 
  statusUpdate: updateOrderStatus, 
): Promise<OrderResponseDto> {
  try {
    const { data } = await orderApi.patch<OrderResponseDto>(
      `/${id}`, 
      statusUpdate,
    );
    return data;
  } catch (err: any) {
    console.error(`Error al actualizar estado de la orden ${id}:`, err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al actualizar la orden");
  }
}