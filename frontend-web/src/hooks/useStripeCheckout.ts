import { useState } from "react";
import { stripePromise } from "../config/StripeConfig";
import { createPayment } from "../api/PaymentApi";
import { useCart } from "../context/CartContext";

export const useStripeCheckout = () => {
  const { cartItems, total, cartId, userId } = useCart();
  const [loading, setLoading] = useState(false);

  const startCheckout = async () => {
    try {
      setLoading(true);
      const stripe = await stripePromise;

      // Simulamos un pedido temporal (puedes obtener el orderId real del backend)
      const orderId = cartId || 1;

      // Crear el registro de pago en tu microservicio
      const payment = await createPayment({
        orderId,
        method: "CARD",
        amount: total,
        currency: "usd",
        status: "PENDING",
      });

      console.log("? Pago creado:", payment);

      // Abrir el Checkout (por ahora simulado, más abajo te explico cómo hacerlo real)
      if (!stripe) throw new Error("Stripe no se cargó correctamente.");

      const { error } = await stripe.redirectToCheckout({
        lineItems: cartItems.map((item) => ({
          price_data: {
            currency: "usd",
            product_data: { name: item.product?.name ?? "Producto" },
            unit_amount: Math.round(item.price * 100),
          },
          quantity: item.quantity,
        })),
        mode: "payment",
        successUrl: window.location.origin + "/checkout/success",
        cancelUrl: window.location.origin + "/checkout/cancel",
      });

      if (error) console.error("? Error en Stripe:", error.message);
    } catch (err) {
      console.error("Error en checkout:", err);
    } finally {
      setLoading(false);
    }
  };

  return { startCheckout, loading };
};
