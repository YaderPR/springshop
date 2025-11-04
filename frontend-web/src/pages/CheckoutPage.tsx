import React, { useState } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import CheckoutForm from '../components/Checkout/CheckoutForm';
import ShippingForm from '../components/Checkout/ShippingForm';
import type { ShippingAddressResponse } from '../components/Checkout/ShippingForm';


const stripePromise = loadStripe(import.meta.env.REACT_STRIPE_PUBLISHABLE_KEY);

export default function CheckoutPage() {

  const [savedShippingAddress, setSavedShippingAddress] = useState<ShippingAddressResponse | null>(null);
  const [shippingSubmitted, setShippingSubmitted] = useState(false);

  const handleShippingSubmit = (savedAddress: ShippingAddressResponse | null) => {
    setShippingAddress(savedAddress);
    setShowPaymentForm(true);
    if (saveAddress) {
      console.log("Dirección guardada con ID:", savedAddress.id, savedAddress);
    } else {
      console.log("Fallo al guardar la dirección en checkoutPage");
    }
  }; 

  const showPaymentForm = shippingSubmitted && savedShippingAddress !== null;

  return (
    <div className="container mx-auto py-10 px-4">
      <h1 className="text-3xl font-bold text-secondary mb-6 text-center">Checkout</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
        
        <div>
          {/* Muestra ShippingForm solo si AÚN NO se ha guardado correctamente */}
          {!showPaymentForm && <ShippingForm onShippingSubmit={handleShippingSubmit} />}
          
          {/* Si ya se guardó, podrías mostrar un resumen o nada */}
          {showPaymentForm && (
              <div className="bg-gray-800 p-4 rounded-lg shadow-lg text-gray-300 text-sm">
                 <h4 className="font-semibold text-secondary mb-2">Dirección Guardada:</h4>
                 {/* ... muestra la dirección guardada ... */}
                  <p>{savedShippingAddress?.street}</p> 
                  <p>{savedShippingAddress?.city}, {savedShippingAddress?.state} {savedShippingAddress?.zipCode}</p>
                  <p>{savedShippingAddress?.country}</p>
                   {savedShippingAddress?.phoneNumber && <p>Tel: {savedShippingAddress.phoneNumber}</p>}
              </div>
          )}
        </div>

        <div>
           {/* Muestra el pago solo si showPaymentForm es true */}
          {showPaymentForm ? ( 
            <>
              {/* Muestra la dirección confirmada (opcional, ya la mostramos arriba) */}
              {/* <div className="bg-gray-800 ..."> ... </div> */}

              <Elements stripe={stripePromise}>
                 {/* Pasa el ID al form de pago */}
                <CheckoutForm shippingAddressId={savedShippingAddress?.id} /> 
              </Elements>
            </>
          ) : (
             !shippingSubmitted && ( // Mensaje inicial
                <div className="text-center text-gray-400 mt-10">
                    <p>Por favor, completa tu dirección de envío para continuar.</p>
                </div>
             )
             // Podrías añadir un mensaje si shippingSubmitted es true pero savedShippingAddress es null (error)
          )}
        </div>
      </div>
    </div>
  );
}