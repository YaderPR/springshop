import { useState } from "react";
import { startCheckout as startBackendCheckout } from "../services/order/OderService";
import { useCart } from "../context/CartContext";
import { toast } from "react-toastify"; // <--- Importamos de react-toastify

export const useStripeCheckout = () => {
  const { cartId, userId } = useCart();
  const [loading, setLoading] = useState(false);

  const startCheckout = async (addressId: number) => {
    if (!cartId || !userId) {
      toast.error("Error: Faltan datos del carrito o usuario.");
      return;
    }

    if (!addressId) {
      toast.warn("Por favor selecciona una dirección de envío."); // .warn en lugar de .warning
      return;
    }

    try {
      setLoading(true);

      const checkoutRequest = {
        cartId,
        userId,
        addressId,
        redirectUrl: window.location.origin + "/checkout/success"
      };

      // Toast de carga (React-Toastify usa update para promesas, 
      // pero para mantenerlo simple usaremos un loading manual)
      const toastId = toast.loading("Conectando con Stripe...");

      const response = await startBackendCheckout(checkoutRequest);

      if (response && response.checkoutUrl) {
        toast.update(toastId, { render: "Redirigiendo...", type: "success", isLoading: false, autoClose: 2000 });
        window.location.href = response.checkoutUrl;
      } else {
        toast.update(toastId, { render: "Error al obtener URL", type: "error", isLoading: false, autoClose: 3000 });
        throw new Error("El servidor no devolvió una URL válida.");
      }

    } catch (err: any) {
      console.error("Error:", err);
      // toast.dismiss(); // Opcional si quieres limpiar
      toast.error(err.message || "Error al procesar el pago.");
    } finally {
      setLoading(false);
    }
  };

  return { startCheckout, loading };
};