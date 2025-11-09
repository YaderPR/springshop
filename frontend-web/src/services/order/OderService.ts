import axios from 'axios';
import type { CheckoutRequestDto, CheckoutResponseDto, OrderResponseDto } from '../../types/order.types'; 

const orderApi = axios.create({
  baseURL: "http://localhost:8087/api/v2/orders",
  headers: { "Content-Type" : "application/json" },
});


export async function getOrders(token: string): Promise<OrderResponseDto[]> {
  try {
    const { data } = await orderApi.get<OrderResponseDto[]>('', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return data;
  } catch (err: any) {
    console.error("Error al obtener órdenes:", err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al obtener órdenes ");
  }
}

export async function getOrderById(id: number, token: string): Promise<OrderResponseDto> {
  try {
    const { data } = await orderApi.get<OrderResponseDto>(`/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return data;
  } catch (err: any) {
    console.error(`Error al obtener la orden ${id}:`, err.response?.data || err.message);
    throw new Error(err.response?.data?.message || "Error al obtener la orden");
  }
}

export async function startCheckout(
  checkoutData: CheckoutRequestDto, 
  token: string
): Promise<CheckoutResponseDto> {
  try {
    const { data } = await orderApi.post<CheckoutResponseDto>(
      '/checkout', 
      checkoutData,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    return data;
  } catch (error: any) {
    console.error("Error al iniciar checkout:", error.response?.data || error.message);
    // Relanzamos el error específico del backend (ej. "Stock insuficiente")
    throw new Error(error.response?.data?.message || 'Error al iniciar el checkout');
  }
}


