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
// 1. CONFIGURACIÓN DE AXIOS E INTERCEPTORES
// -----------------------------------------------------

const cartApi: AxiosInstance = axios.create({
    baseURL: CART_API_BASE_URL,
    headers: { "Content-Type": "application/json" }
});

// Variables y funciones para el refresco de token 
let isRefreshing = false;
let failedQueue: Array<{ resolve: (value: unknown) => void; reject: (reason?: any) => void; originalRequest: InternalAxiosRequestConfig }> = [];

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
    // MÉTODOS DE PERSISTENCIA LOCAL
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
    }
    
    // ====================================================================
    // MÉTODOS DE LA API 
    // ====================================================================

    async getExistingCartByUserId(userId: number): Promise<CartResponseDto | null> {
        try {
            console.log('🔎 [CartService] Buscando carrito existente para UserId:', userId);
            const { data } = await cartApi.get<CartResponseDto>(`/user/${userId}`);
            return data;
        } catch (error) {
            if (axios.isAxiosError(error)) {
                // CORRECCIÓN: El backend devuelve 500 en lugar de 404 cuando no hay carrito.
                // Tratamos 500 como "No encontrado" para permitir la creación.
                if (error.response?.status === 404 || error.response?.status === 500) {
                    console.log('✅ [CartService] No se encontró carrito activo (404/500). Se creará uno nuevo.');
                    return null;
                }
            }
            throw error;
        }
    }

    async createUserCart(userId: number): Promise<CartResponseDto> {
        console.log('✨ [CartService] Creando nuevo carrito para UserId:', userId);
        // La API espera un body con userId
        const { data } = await cartApi.post<CartResponseDto>('', { userId }); 
        return data;
    }


    async getCartById(cartId: number): Promise<CartResponseDto> {
        const { data } = await cartApi.get<CartResponseDto>(`/${cartId}`);
        return data;
    }

    // ====================================================================
    // 💡 LÓGICA DE INICIALIZACIÓN (Orquestación)
    // ====================================================================

   
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

    async getAllCarts(): Promise<CartResponseDto[]> {
        const { data } = await cartApi.get<CartResponseDto[]>('');
        return data;
    }

    async updateCart(cartId: number, userId: number): Promise<CartResponseDto> {
        const { data } = await cartApi.put<CartResponseDto>(`/${cartId}`, { userId });
        return data;
    }

    async deleteCart(cartId: number): Promise<void> {
        await cartApi.delete(`/${cartId}`);
    }

    async clearCart(cartId: number): Promise<void> {
        await cartApi.delete(`/${cartId}/items`);
    }

    // ========== OPERACIONES DE ITEMS DEL CARRITO ==========

    async getCartItems(cartId: number): Promise<CartItemResponseDto[]> {
        const { data } = await cartApi.get<CartItemResponseDto[]>(`/${cartId}/items`);
        return data;
    }

    async addItemToCart(cartId: number, item: CartItemCreateRequestDto): Promise<CartItemResponseDto> {
        const { data } = await cartApi.post<CartItemResponseDto>(
            `/${cartId}/items`,
            item
        );
        return data;
    }

    async updateCartItem(
        cartId: number,
        itemId: number,
        item: CartItemUpdateRequestDto
    ): Promise<CartItemResponseDto> {
        const { data } = await cartApi.put(`/${cartId}/items/${itemId}`, item);
        return data; 
    }

    async removeItemFromCart(cartId: number, itemId: number): Promise<void> {
        await cartApi.delete(`/${cartId}/items/${itemId}`);
    }

    async getCartTotal(cartId: number): Promise<number> {
        const { data } = await cartApi.get<number>(`/${cartId}/items/total`);
        return data;
    }
};

export const cartService = new CartService();