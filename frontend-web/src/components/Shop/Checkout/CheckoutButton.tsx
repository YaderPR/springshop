import React from "react";
import { useStripeCheckout } from "../../../hooks/useStripeCheckout";
import { toast } from "react-toastify"; // O react-toastify si elegiste esa

interface Props {
  addressId: number | null; // El ID de la dirección seleccionada
}

const CheckoutButton: React.FC<Props> = ({ addressId }) => {
  const { startCheckout, loading } = useStripeCheckout();

  const handleClick = () => {
    if (!addressId) {
      toast.warning("Por favor selecciona una dirección de envío antes de pagar.");
      return;
    }
    // Ahora sí pasamos el ID correcto
    startCheckout(addressId);
  };

  return (
    <button
      onClick={handleClick}
      disabled={loading || !addressId} // Deshabilitamos si no hay dirección o está cargando
      className={`
        px-6 py-3 rounded-lg font-bold shadow-sm transition-all
        ${loading || !addressId 
          ? "bg-gray-600 text-gray-400 cursor-not-allowed" 
          : "bg-secondary text-primary hover:bg-lime-400 hover:scale-[1.02] ring-1 ring-secondary"
        }
      `}
    >
      {loading ? "Procesando..." : "Pagar con Stripe"}
    </button>
  );
};

export default CheckoutButton;