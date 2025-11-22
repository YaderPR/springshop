import React, { useState } from 'react';
import { useCartManager } from '../../hooks/useCartManager';
import { Loader2 } from 'lucide-react';

import { startCheckout } from '../../services/order/OderService';
import { useKeycloak } from '@react-keycloak/web';
import type { ShippingAddressResponse } from '../../services/shipment/ShippingService';
import ShippingForm from '../../components/Shop/Checkout/ShippingForm';


export default function CheckoutPage() {
    // Obtenemos los IDs y el estado de Keycloak
    const { cartId, userId } = useCartManager();
    const { keycloak, initialized } = useKeycloak(); // <-- Añadido
    // Estados de esta página
    const [addressId, setAddressId] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Callback que el ShippingForm nos dará
    const handleShippingSubmit = (savedAddress: ShippingAddressResponse | null) => {
        if (savedAddress && savedAddress.id) {
            setAddressId(savedAddress.id);
            console.log("Dirección guardada con ID:", savedAddress.id);
        } else {
            setError("No se pudo guardar la dirección.");
        }
    };

    // --- ¡ARREGLO 3! ---
    // El manejador del botón final de pago
    const handleFinalCheckout = async () => {
        // Verificamos que todo esté listo
        if (!cartId || !userId || !addressId) {
            setError("Faltan datos (Carrito, Usuario o Dirección) para continuar.");
            return;
        }
        console.log("CheckoutPage - cartId:", cartId, "userId:", userId, "addressId:", addressId);
        // Verificamos que tengamos el token
        if (!initialized || !keycloak.token) {
            setError("Sesión no válida. Por favor, inicie sesión de nuevo.");
            keycloak.login(); // Forzar login
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            console.log("CheckoutPage - cartId:", cartId, "userId:", userId, "addressId:", addressId, "desde el try.. catch");
            // Llamamos a la FUNCIÓN 'startCheckout' directamente
            const response = await startCheckout(
                { cartId, userId, addressId}, // DTO       // ¡Token!
            );

            // ¡ÉXITO! Redirigimos a Stripe

            if (response.checkoutUrl) {
                window.location.href = response.checkoutUrl;
            } else {
                setError("No se recibió una URL de pago del servidor.");
                setIsLoading(false);
            }

        } catch (err: any) {
            console.error('Error al iniciar checkout:', err);
            setError(err.message); // El mensaje de error viene del backend
            setIsLoading(false);
        }
    };

    // Renderizado condicional
    return (
        <div className="container mx-auto py-10 px-4">
            <h1 className="text-3xl font-bold text-secondary mb-6 text-center">Checkout</h1>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                <div>
                    <h2 className="text-xl font-semibold text-white mb-4">1. Dirección de Envío</h2>
                    {!addressId && (
                        <ShippingForm onShippingSubmit={handleShippingSubmit} />
                    )}
                    {addressId && (
                        <div className="bg-gray-800 p-4 rounded-lg shadow-lg text-gray-300">
                            <p className="text-green-400">? Dirección guardada (ID: {addressId}).</p>
                        </div>
                    )}
                </div>

                <div>
                    <h2 className="text-xl font-semibold text-white mb-4">2. Resumen y Pago</h2>
                    {addressId ? (
                        <div className="bg-gray-800 p-6 rounded-lg shadow-lg">
                            <p className="text-gray-300 mb-4">Todo listo. Serás redirigido a Stripe para completar la transacción.</p>
                            
                            <button
                                onClick={handleFinalCheckout}
                                disabled={isLoading || !initialized} // Deshabilitado si Keycloak no está listo
                                className="w-full bg-secondary text-primary font-bold py-3 px-4 rounded-lg hover:bg-lime-400 transition-colors disabled:opacity-50"
                            >
                                {isLoading ? (
                                    <Loader2 className="w-5 h-5 animate-spin mx-auto" />
                                ) : (
                                    "Continuar a Pago Seguro"
                                )}
                            </button>
                            
                            {error && <p className="text-red-400 mt-4">{error}</p>}
                        </div>
                    ) : (
                        <div className="text-center text-gray-400 mt-10">
                            <p>Por favor, completa tu dirección de envío para continuar con el pago.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}