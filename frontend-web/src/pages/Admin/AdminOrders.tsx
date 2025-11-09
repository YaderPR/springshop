import React, { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { getOrders } from '../../services/order/OderService';
import type { OrderResponseDto } from '../../types/Order.types';
import { Loader2, AlertTriangle, CheckCircle, CircleAlert, Eye } from 'lucide-react'; 
import OrderDetailModal from '../../components/Admin/OrderDetailModal';

export default function AdminOrders() {
  const { keycloak, initialized } = useKeycloak();
  const [orders, setOrders] = useState<OrderResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedOrderId, setSelectedOrderId] = useState<number | null>(null);

  // --- (TODA TU LÓGICA DE useEffect, loadOrders, etc. va aquí... ES CORRECTA) ---
  useEffect(() => {
    if (!initialized) {
      return;
    }
    if (!keycloak.token) {
      setLoading(false);
      setError("No se encontró token de autenticación.");
      return;
    }
    const loadOrders = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getOrders(keycloak.token);
        setOrders(data);
      } catch (err: any) {
        setError(err.message || "Un error desconocido ocurrió.");
      } finally {
        setLoading(false);
      }
    };
    loadOrders();
  }, [initialized, keycloak.token]);

  // --- Renderizado Condicional (SIN CAMBIOS) ---
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
        {/* ... (Tu JSX de error es correcto) ... */}
      </div>
    );
  }

  // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
  // --- Renderizado de Éxito (Reemplazamos <table> por Cards) ---
  return (
    <> 
      <div>
        <h1 className="text-3xl font-bold text-secondary mb-6">
          Administrar Órdenes
        </h1>
        
        {/* Contenedor de las Tarjetas */}
        <div className="grid grid-cols-1 gap-4">
          {orders.length > 0 ? (
            orders.map((order) => (
              <div 
                key={order.id} 
                className="bg-primary/50 border  ring-1 ring-lime-400/20 
              transition-all duration-300
              hover:ring-secondary
              hover:shadow-[0_0_15px_rgba(137,254,0,.7)] items-center gap-4 text-gray-100 border-gray-700 rounded-lg shadow-md p-4 flex flex-col sm:flex-row sm:justify-between sm:items-center"
              >
                {/* ... (Toda tu tarjeta de orden es perfecta) ... */}
                <div className="space-y-2">
                  <div className="flex flex-col sm:flex-row sm:items-center sm:gap-4">
                    <span className="text-xl font-bold text-white whitespace-nowrap">
                      Orden #{order.id}
                    </span>
                    {order.status === 'PAID' ? (
                      <span className="flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded-full bg-secondary/20 text-secondary w-fit">
                        <CheckCircle size={14} /> PAGADO
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded-full bg-yellow-500/20 text-yellow-400 w-fit">
                        <CircleAlert size={14} /> {order.status}
                      </span>
                    )}
                  </div>
                  <div className="text-sm text-gray-400">
                    <span>Usuario ID: {order.userId}</span>
                    <span className="mx-2">|</span>
                    <span>Fecha: {new Date(order.createAt).toLocaleDateString()}</span>
                  </div>
                </div>

                <div className="mt-4 sm:mt-0 sm:text-right flex-shrink-0">
                  <div className="text-2xl font-bold text-secondary mb-2">
                    ${order.totalAmount.toFixed(2)}
                  </div>
                  {/* --- 4. Conectar el botón --- */}
                  <button 
                    onClick={() => setSelectedOrderId(order.id)} // <-- Abre el modal
                    className="flex items-center gap-1 sm:ml-auto hover:text-secondary"
                  >
                    <Eye size={16} />
                    Ver Detalles
                  </button>
                </div>

              </div>
            ))
          ) : (
            <div className="text-center p-8 text-gray-500 bg-primary border border-gray-700 rounded-lg">
              No se encontraron órdenes.
            </div>
          )}
        </div>
      </div>

      {/* --- 5. Renderizar el Modal --- */}
      {/* El modal se renderiza aquí, pero solo es visible si 'selectedOrderId' no es null */}
      <OrderDetailModal 
        orderId={selectedOrderId}
        onClose={() => setSelectedOrderId(null)} // Función para cerrar
      />
    </>
  );
}