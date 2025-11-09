// src/components/CheckoutButton.tsx
import React from "react";
import { useStripeCheckout } from "../../../hooks/useStripeCheckout";

const CheckoutButton: React.FC = () => {
  const { startCheckout, loading } = useStripeCheckout();

  return (
    <button
      onClick={startCheckout}
      disabled={loading}
      className="bg-secondary ring-1 ring-secondary shadow-sm text-primary hover:text-secondary px-6 py-3 rounded-lg hover:bg-primary transition"
    >
      {loading ? "Procesando..." : "Pagar con Stripe"}
    </button>
  );
};

export default CheckoutButton;
