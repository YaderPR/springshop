import React, { useState } from 'react';
import { useCartManager } from '../hooks/useCartManager';
import { orderService } from '../services/order/OderService';
import ShippingForm from '../components/Checkout/ShippingForm';
import type { ShippingAddressResponse } from '../components/Checkout/ShippingForm';
import { Loader2 } from 'lucide-react';

export default function CheckoutPage() {
    // 1. Obtenemos los IDs del manager
    const { cartId, userId } = useCartManager();

    // 2. Estados de esta página
    const [addressId, setAddressId] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // 3. Callback que el ShippingForm nos dará
    const handleShippingSubmit = (savedAddress: ShippingAddressResponse | null) => {
        if (savedAddress && savedAddress.id) {
            setAddressId(savedAddress.id);
            console.log("Dirección guardada con ID:", savedAddress.id);
        } else {
            setError("No se pudo guardar la dirección.");
        }
    };

    // 4. El manejador del botón final de pago
    const handleFinalCheckout = async () => {
        if (!cartId || !userId || !addressId) {
            setError("Faltan datos (Carrito, Usuario o Dirección) para continuar.");
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            // 5. Llamamos al backend (OrderController)
            const response = await orderService.startCheckout({
                cartId,
                userId, // Este ID ahora es real (ej. 125)
                addressId
            });

            // 6. ¡ÉXITO! Redirigimos a Stripe
            if (response.checkoutUrl) {
                window.location.href = response.checkoutUrl;
            } else {
                setError("No se recibió una URL de pago del servidor.");
                setIsLoading(false);
            }

        } catch (err: any) {
            // 7. ¡ERROR! El backend nos dice qué pasó (ej. "Stock insuficiente")
            console.error('Error al iniciar checkout:', err);
            setError(err.message); // El mensaje de error viene del backend
            setIsLoading(false);
        }
    };

    // 8. Renderizado condicional
    return (
        <div className="container mx-auto py-10 px-4">
            <h1 className="text-3xl font-bold text-secondary mb-6 text-center">Checkout</h1>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                <div>
                    <h2 className="text-xl font-semibold text-white mb-4">1. Dirección de Envío</h2>
                    {/* El formulario de dirección */}
                    {!addressId && (
                        <ShippingForm onShippingSubmit={handleShippingSubmit} />
                    )}
                    
                    {/* Mensaje de confirmación de dirección */}
                    {addressId && (
                        <div className="bg-gray-800 p-4 rounded-lg shadow-lg text-gray-300">
                            <p className="text-green-400"> Dirección guardada (ID: {addressId}).</p>
                        </div>
                    )}
                </div>

                <div>
                    <h2 className="text-xl font-semibold text-white mb-4">2. Resumen y Pago</h2>
                    {/* Mostramos el botón de pago SÓLO si ya tenemos la dirección */}
                    {addressId ? (
                        <div className="bg-gray-800 p-6 rounded-lg shadow-lg">
                            <p className="text-gray-300 mb-4">Todo listo. Serás redirigido a Stripe, nuestro proveedor de pagos seguro, para completar la transacción.</p>
                            
                            <button
                                onClick={handleFinalCheckout}
                                disabled={isLoading}
                                className="w-full bg-secondary text-black font-bold py-3 px-4 rounded-lg hover:bg-lime-400 transition-colors disabled:opacity-50"
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
                            <p>Por favor, completa tu dirección de envío para continuar con el pago. AQUÍ ESTAS CARNALITO JEJE</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}