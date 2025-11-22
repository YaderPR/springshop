import axios, { AxiosError, type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import type {
    CartItemCreateRequestDto,
    CartItemUpdateRequestDto,
    CartResponseDto,
    CartItemResponseDto
} from '../../types/cart.types';
import { keycloakClient } from '../../components/Auth/keycloak.client';

const CART_API_BASE_URL = 'http://localhost:8080/api/v2/carts';
const CART_ID_KEY = 'app_cart_id';

// -----------------------------------------------------
// 1. CONFIGURACIÓN DE AXIOS E INTERCEPTORES (Sin Cambios)
// -----------------------------------------------------

const cartApi: AxiosInstance = axios.create({
    baseURL: CART_API_BASE_URL,
    headers: { "Content-Type": "application/json" }
});

// Variables y funciones para el refresco de token (omitiendo código repetitivo)
let isRefreshing = false;
let failedQueue: Array<{ resolve: (value: unknown) => void; reject: (reason?: any) => void; originalRequest: InternalAxiosRequestConfig }> = [];

// ... (Implementación de processQueue, interceptor.request, interceptor.response se mantiene) ...

const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(cartApi(prom.originalRequest));
        }
    });
    failedQueue = [];
};

cartApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    if (keycloakClient.authenticated && keycloakClient.token) {
        config.headers.Authorization = `Bearer ${keycloakClient.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

cartApi.interceptors.response.use(
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
                    processQueue(new Error("Sesión expirada. Por favor, inicie sesión nuevamente."));
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
// 2. CLASE DE SERVICIO
// -----------------------------------------------------

class CartService {
    
    // ====================================================================
    // 🔑 MÉTODOS DE PERSISTENCIA LOCAL
    // ====================================================================

    public getLocalCartId(): number | null {
        const id = localStorage.getItem(CART_ID_KEY);
        return id ? parseInt(id, 10) : null;
    }

    private setLocalCartId(cartId: number): void {
        localStorage.setItem(CART_ID_KEY, cartId.toString());
    }

    public removeLocalCartId(): void {
        localStorage.removeItem(CART_ID_KEY);
        console.log('🗑️ [CartService] ID de carrito local limpiado.');
    }
    
    // ====================================================================
    // 🎯 MÉTODOS DE LA API (Fieles al contrato)
    // ====================================================================

    /**
     * @GET /api/v2/carts/user/{userId} (getLastCartByUser)
     * Busca el último (y asumimos activo) carrito para el usuario.
     * Si la API devuelve 404, retorna null.
     */
    async getExistingCartByUserId(userId: number): Promise<CartResponseDto | null> {
        try {
            console.log('🔎 [CartService] Buscando carrito existente para UserId:', userId);
            const { data } = await cartApi.get<CartResponseDto>(`/user/${userId}`);
            return data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.status === 404) {
                console.log('✅ [CartService] No se encontró carrito activo (404).');
                return null;
            }
            throw error;
        }
    }

    /**
     * @POST /api/v2/carts (createCart)
     * Crea un nuevo carrito.
     */
    async createUserCart(userId: number): Promise<CartResponseDto> {
        console.log('✨ [CartService] Creando nuevo carrito para UserId:', userId);
        // La API espera un body con userId
        const { data } = await cartApi.post<CartResponseDto>('', { userId }); 
        return data;
    }

    /**
     * @GET /api/v2/carts/{id} (getCartById)
     */
    async getCartById(cartId: number): Promise<CartResponseDto> {
        const { data } = await cartApi.get<CartResponseDto>(`/${cartId}`);
        return data;
    }

    // ====================================================================
    // 💡 LÓGICA DE INICIALIZACIÓN (Orquestación)
    // ====================================================================

    /**
     * 🎯 Lógica de inicialización (llamada desde UserService después de sync).
     * @param userId El ID interno del usuario.
     */
    async initializeCart(userId: number): Promise<CartResponseDto> {
        console.log(`🔄 [CartService.initializeCart] Iniciando chequeo para UserID: ${userId}`);
        
        let cartResponse: CartResponseDto;

        try {
            // 1. INTENTAR OBTENER CARRITO EXISTENTE (Usando el endpoint definido)
            const existingCart = await this.getExistingCartByUserId(userId);
            
            if (existingCart) {
                cartResponse = existingCart;
            } else {
                // 2. SI NO EXISTE (404), CREAR UNO NUEVO (Usando el endpoint definido)
                cartResponse = await this.createUserCart(userId);
            }
            
            // 3. Persistir el ID
            this.setLocalCartId(cartResponse.id);
            
            return cartResponse;
            
        } catch (e: any) {
            console.error('❌ [CartService.initializeCart] Error al inicializar/crear el carrito:', e.message);
            this.removeLocalCartId();
            throw new Error(`Fallo al inicializar el carrito: ${e.message}`);
        }
    }


    // ====================================================================
    // 5. MÉTODOS CRUD ESTÁNDAR (Fieles al contrato)
    // ====================================================================

    /**
     * @GET /api/v2/carts (getAllCarts) - Asume permisos de Admin/Management
     */
    async getAllCarts(): Promise<CartResponseDto[]> {
        const { data } = await cartApi.get<CartResponseDto[]>('');
        return data;
    }

    /**
     * @PUT /api/v2/carts/{id} (updateCart)
     */
    async updateCart(cartId: number, userId: number): Promise<CartResponseDto> {
        const { data } = await cartApi.put<CartResponseDto>(`/${cartId}`, { userId });
        return data;
    }

    /**
     * @DELETE /api/v2/carts/{id} (deleteCart)
     */
    async deleteCart(cartId: number): Promise<void> {
        await cartApi.delete(`/${cartId}`);
    }

    /**
     * @DELETE /api/v2/carts/{cartId}/items (clearCart)
     */
    async clearCart(cartId: number): Promise<void> {
        await cartApi.delete(`/${cartId}/items`);
    }

    // ========== OPERACIONES DE ITEMS DEL CARRITO ==========

    /**
     * @GET /api/v2/carts/{cartId}/items (getCartItems)
     */
    async getCartItems(cartId: number): Promise<CartItemResponseDto[]> {
        const { data } = await cartApi.get<CartItemResponseDto[]>(`/${cartId}/items`);
        return data;
    }

    /**
     * @POST /api/v2/carts/{cartId}/items (addItemToCart)
     */
    async addItemToCart(cartId: number, item: CartItemCreateRequestDto): Promise<CartItemResponseDto> {
        const { data } = await cartApi.post<CartItemResponseDto>(
            `/${cartId}/items`,
            item
        );
        return data;
    }

    /**
     * @PUT /api/v2/carts/{cartId}/items/{itemId} (updateCartItem)
     */
    async updateCartItem(
        cartId: number,
        itemId: number,
        item: CartItemUpdateRequestDto
    ): Promise<CartItemResponseDto> {
        await cartApi.put(`/${cartId}/items/${itemId}`, item);
        // Según la definición, este PUT devuelve el DTO del ítem, aunque solo necesitamos el 200/204.
        // Asumiendo que quieres el DTO de vuelta:
        // const { data } = await cartApi.put<CartItemResponseDto>(...); return data;
        // Pero para simplificar el flujo y dado que el Flutter code no lo retornaba explícitamente:
        const { data } = await cartApi.get<CartItemResponseDto>(`/${cartId}/items/${itemId}`);
        return data; // O deberías devolver el DTO de la respuesta PUT si lo tienes.
    }

    /**
     * @DELETE /api/v2/carts/{cartId}/items/{itemId} (deleteCartItem)
     */
    async removeItemFromCart(cartId: number, itemId: number): Promise<void> {
        await cartApi.delete(`/${cartId}/items/${itemId}`);
    }

    /**
     * @GET /api/v2/carts/{cartId}/items/total (getCartTotal)
     */
    async getCartTotal(cartId: number): Promise<number> {
        const { data } = await cartApi.get<number>(`/${cartId}/items/total`);
        return data;
    }
};

export const cartService = new CartService();