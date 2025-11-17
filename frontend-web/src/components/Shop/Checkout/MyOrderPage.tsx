import React, { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { getOrders } from '../../../services/order/OderService';
import type { OrderResponseDto } from '../../types/Order.types';
import { Loader2, AlertTriangle, CheckCircle, CircleAlert, PackageSearch } from 'lucide-react';
import { Navigate } from 'react-router-dom';

export default function MyOrderPage() {
  const { keycloak, initialized } = useKeycloak();
  const [orders, setOrders] = useState<OrderResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    
    if (!initialized) {
      return; 
    }
    
    // 2. Si no está logueado, no debería estar aquí.
    if (!keycloak.authenticated) {
      setLoading(false);
      setError("Necesitas iniciar sesión para ver tus órdenes.");
      return;
    }

    const loadOrders = async () => {
      setLoading(true);
      setError(null);
      try {
        // 3. Llamamos a la misma función que usa el admin
        const data = await getOrders(keycloak.token!);
        data.sort((a, b) => b.id - a.id); // Ordenamos por más reciente
        setOrders(data);
      } catch (err: any) {
        setError(err.message || "Un error desconocido ocurrió.");
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, [initialized, keycloak.token]);

  // --- Renderizado Condicional ---
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  // Si no está logueado (y ya no está cargando), lo redirigimos
  if (!keycloak.authenticated) {
    return <Navigate to="/" replace />;
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
        <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
        <div>
          <h2 className="text-xl font-bold text-red-400">Error al Cargar Órdenes</h2>
          <p className="text-red-300">{error}</p>
        </div>
      </div>
    );
  }

  // --- Renderizado de Éxito ---
  return (
    <div className="container mx-auto p-4 md:p-8">
      <h1 className="text-3xl font-bold text-secondary mb-6">
        Mis Pedidos
      </h1>
      
      {/* Contenedor de las Tarjetas */}
      <div className="grid grid-cols-1 gap-4">
        {orders.length > 0 ? (
          orders.map((order) => (
            <div 
              key={order.id} 
              className="bg-primary border border-gray-700 rounded-lg shadow-md p-4 flex flex-col sm:flex-row sm:justify-between sm:items-center"
            >
              <div className="space-y-2">
                <div className="flex items-center gap-4">
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
                  <span>Fecha: {new Date(order.createAt).toLocaleDateString()}</span>
                </div>
              </div>
              <div className="mt-4 sm:mt-0 sm:text-right flex-shrink-0">
                <div className="text-2xl font-bold text-secondary mb-2">
                  ${order.totalAmount.toFixed(2)}
                </div>
                {/* No ponemos "Ver Detalles" ya que un usuario normal no necesita ver el modal de admin */}
              </div>
            </div>
          ))
        ) : (
          <div className="text-center p-8 text-gray-500 bg-primary border border-gray-700 rounded-lg">
            <PackageSearch size={48} className="mx-auto mb-2" />
            Aún no has realizado ningún pedido.
          </div>
        )}
      </div>
    </div>
  );
}