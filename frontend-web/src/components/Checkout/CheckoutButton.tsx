// src/components/CheckoutButton.tsx
import React from "react";
import { useStripeCheckout } from "../../hooks/useStripeCheckout";

const CheckoutButton: React.FC = () => {
  const { startCheckout, loading } = useStripeCheckout();

  return (
    <button
      onClick={startCheckout}
      disabled={loading}
      className="bg-blue-600 text-white px-6 py-3 rounded-xl hover:bg-blue-700 transition"
    >
      {loading ? "Procesando..." : "Pagar con Stripe ??"}
    </button>
  );
};

export default CheckoutButton;
