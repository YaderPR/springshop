import axios from 'axios';
import type { CheckoutRequestDto, CheckoutResponseDto } from '../../types/order.types'; 
const ORDER_API_BASE_URL = 'http://localhost:8087/api/v2/orders'; 

class OrderService {

  /**
   * Esta es la llamada principal que inicia todo.
   * Llama al endpoint que crea la Orden, reserva el stock y crea la sesión de Stripe.
   */
  async startCheckout(checkoutData: CheckoutRequestDto): Promise<CheckoutResponseDto> {
    try {
      const { data } = await axios.post<CheckoutResponseDto>(
        `${ORDER_API_BASE_URL}/checkout`, 
        checkoutData
      );
      return data;
    } catch (error: any) {
      // Si el backend devuelve un error (ej. 409 por Stock), lo relanzamos
      // para que el componente de React pueda manejarlo y mostrar un mensaje.
      if (error.response && error.response.data) {
        throw new Error(error.response.data.error || 'Error al iniciar el checkout');
      }
      throw new Error('Error de conexión al iniciar el checkout');
    }
  }

  // (Aquí irán otras llamadas a 'order' en el futuro, como 'getOrderHistory', etc.)
}

export const orderService = new OrderService();

