import React, { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { getOrderById } from '../../services/order/OderService';
import type { OrderResponseDto } from '../../types/Order.types';
import { Loader2, X, AlertTriangle } from 'lucide-react';

interface Props {
  orderId: number | null; // El ID de la orden a mostrar
  onClose: () => void;     // Función para cerrar el modal
}

export default function OrderDetailModal({ orderId, onClose }: Props) {
  const { keycloak, initialized } = useKeycloak();
  const [order, setOrder] = useState<OrderResponseDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Efecto que se dispara CADA VEZ que el 'orderId' cambia
  useEffect(() => {
    // No hacer nada si el modal no está abierto (orderId es null)
    // o si Keycloak no está listo
    if (!orderId || !initialized) {
      return;
    }

    if (!keycloak.token) {
      setError("No se pudo obtener el token de autenticación.");
      return;
    }

    const loadOrderDetails = async () => {
      setLoading(true);
      setError(null);
      setOrder(null); // Limpiar orden anterior mientras carga
      try {
        // Llama al servicio con el ID y el token
        const data = await getOrderById(orderId, keycloak.token);
        setOrder(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadOrderDetails();
  }, [orderId, initialized, keycloak.token]); // Dependencias del efecto

  // No renderizar nada si el modal no debe estar abierto
  if (!orderId) {
    return null;
  }

  return (
    // Overlay (fondo oscuro)
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-primary bg-opacity-10 backdrop-blur-sm p-4"
      onClick={onClose} // Cierra al hacer clic en el fondo
    >
      {/* Contenedor del Modal */}
      <div
        className="relative w-full max-w-2xl p-6 bg-primary/95 rounded-2xl border border-secondary text-gray-100 shadow-[0_0_15px_rgba(137,254,0,.7)]"
        onClick={(e) => e.stopPropagation()} // Evita que el clic en el modal lo cierre
      >
        {/* Botón de Cerrar */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-white z-10"
        >
          <X size={24} />
        </button>

        {/* --- Contenido --- */}
        {loading && (
          <div className="flex items-center justify-center h-64">
            <Loader2 className="w-12 h-12 animate-spin text-secondary" />
          </div>
        )}

        {error && (
          <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
            <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
            <div>
              <h2 className="text-xl font-bold text-red-400">Error al Cargar la Orden</h2>
              <p className="text-red-300">{error}</p>
            </div>
          </div>
        )}

        {/* Solo mostrar si NO está cargando, NO hay error, y la ORDEN existe */}
        {order && !loading && !error && (
          <div>
            <h2 className="text-3xl font-bold text-secondary mb-4">
              Detalles de la Orden #{order.id}
            </h2>
            
            {/* Detalles principales */}
            <div className="grid grid-cols-2 gap-x-4 gap-y-2 mb-4 text-lg">
              <span className="text-gray-400">Usuario ID:</span>
              <span className="text-white font-medium">{order.userId}</span>
              
              <span className="text-gray-400">Estado:</span>
              <span className={`font-medium ${order.status === 'PAID' ? 'text-secondary' : 'text-yellow-400'}`}>
                {order.status}
              </span>
              
              <span className="text-gray-400">Fecha:</span>
              <span className="text-white font-medium">
                {new Date(order.createAt).toLocaleString()}
              </span>

              <span className="text-gray-400">Total:</span>
              <span className="text-secondary font-bold text-xl">
                ${order.totalAmount.toFixed(2)}
              </span>
            </div>

            {/* Items de la Orden */}
            <div className="border-t border-gray-700 pt-4">
              <h3 className="text-xl font-semibold text-white mb-3">Items Comprados </h3>
              {/* Contenedor con scroll si hay muchos items */}
              <div className="space-y-3 no-scrollbar max-h-60 overflow-y-auto pr-2">
                {order.items.map(item => (
                  <div key={item.id} className="flex justify-between items-center p-3 bg-gray-800 rounded-lg">
                    <div>
                      <p className="font-bold text-secondary">Producto ID: {item.productId}</p>
                      <p className="text-sm text-gray-400">Precio Unit.: ${item.price.toFixed(2)}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-medium text-white">x {item.quantity}</p>
                      <p className="text-lg font-bold text-white">
                        ${(item.price * item.quantity).toFixed(2)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}