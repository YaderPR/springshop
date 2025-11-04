import React, { useState } from 'react';
import { CardElement, useStripe, useElements } from '@stripe/react-stripe-js';

// Estilos básicos para CardElement (opcional, puedes personalizar)
const CARD_ELEMENT_OPTIONS = {
  style: {
    base: {
      color: "#ffffff", 
      fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
      fontSmoothing: "antialiased",
      fontSize: "16px",
      "::placeholder": {
        color: "#aab7c4", 
      },
    },
    invalid: {
      color: "#fa755a", 
      iconColor: "#fa755a",
    },
  },
};


export default function CheckoutForm() {
  const stripe = useStripe(); // Hook para interactuar con Stripe API
  const elements = useElements(); // Hook para acceder a los Elements montados (ej. CardElement)
  const [error, setError] = useState<string | null>(null);
  const [processing, setProcessing] = useState(false);
  const [succeeded, setSucceeded] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setProcessing(true);
    setError(null);

    if (!stripe || !elements) {
      setError("Stripe no está listo. Intenta de nuevo.");
      setProcessing(false);
      return;
    }

    const cardElement = elements.getElement(CardElement);

    if (cardElement == null) {
      setError("No se encontró el elemento de tarjeta.");
      setProcessing(false);
      return;
    }

    // --- ¡ESTA ES LA PARTE SOLO FRONTEND! ---
    // Creamos un "PaymentMethod" directamente desde el navegador.
    // En una implementación real, enviarías esto a tu backend.
    const { error: paymentMethodError, paymentMethod } = await stripe.createPaymentMethod({
      type: 'card',
      card: cardElement,
      // billing_details: { // Puedes añadir datos del cliente aquí
      //   name: 'Jenny Rosen', 
      // },
    });
    // ------------------------------------------

    if (paymentMethodError) {
      setError(paymentMethodError.message ?? "Ocurrió un error.");
      setProcessing(false);
    } else {
      console.log('PaymentMethod creado (solo frontend):', paymentMethod);
      // En una app real, enviarías paymentMethod.id a tu backend
      // para crear el PaymentIntent y confirmar el pago.
      setError(null);
      setProcessing(false);
      setSucceeded(true);
      // Aquí podrías vaciar el carrito, redirigir, etc.
    }
  };

  return (
    <form onSubmit={handleSubmit} className="max-w-md mx-auto bg-gray-800 p-6 rounded-lg shadow-lg">
      <h3 className="text-xl font-semibold text-secondary mb-4">Detalles de Pago</h3>
      
      {/* Elemento de Tarjeta de Stripe */}
      <div className="bg-gray-700 p-3 rounded mb-4">
        <CardElement options={CARD_ELEMENT_OPTIONS} />
      </div>

      {/* Mostrar Errores */}
      {error && <div className="text-red-500 text-sm mb-4" role="alert">{error}</div>}
      
      {/* Mensaje de Éxito */}
      {succeeded && (
        <p className="text-secondary font-semibold mb-4">
          ¡Pago simulado exitoso! (Solo frontend)
        </p>
      )}

      {/* Botón de Pagar */}
      <button 
        type="submit" 
        disabled={!stripe || processing || succeeded}
        className="w-full bg-secondary text-primary font-bold py-3 rounded-full hover:bg-lime-400 transition-all disabled:opacity-50"
      >
        {processing ? "Procesando..." : "Pagar Ahora (Simulado)"}
      </button>
    </form>
  );
}