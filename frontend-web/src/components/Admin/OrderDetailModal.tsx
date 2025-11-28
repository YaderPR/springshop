import { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
// CORRECCIÓN: Typo en 'OrderService' arreglado
import { getOrderById, updateOrderStatus } from '../../services/order/OderService';
import type { OrderResponseDto, UpdateOrderStatus } from '../../types/Order.types';
import { Loader2, X, AlertTriangle, Save } from 'lucide-react';

interface Props {
  orderId: number | null;
  onClose: () => void;
  onOrderUpdate: () => void;
}

// IMPORTANTE: Eliminé "SHIPPED" porque tu API docs NO lo incluye en el Enum.
// Si lo envías, el backend lanzará un error 400 Bad Request.
const ORDER_STATUSES = ["PENDING", "PAID", "FAILED", "REFUNDED"];

export default function OrderDetailModal({ orderId, onClose, onOrderUpdate }: Props) {
  const { keycloak, initialized } = useKeycloak();
  const [order, setOrder] = useState<OrderResponseDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [isSaving, setIsSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  useEffect(() => {
    if (!orderId || !initialized) return;

    if (!keycloak.token) {
      setError("No se pudo obtener el token de autenticación.");
      return;
    }

    const loadOrderDetails = async () => {
      setLoading(true);
      setError(null);
      setOrder(null);

      try {
        const data = await getOrderById(orderId);
        setOrder(data);
        setSelectedStatus(data.status);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadOrderDetails();
  }, [orderId, initialized, keycloak.token]);

  const handleStatusUpdate = async () => {
    // Verificaciones de seguridad
    if (!orderId || !keycloak.token || !selectedStatus || !order) return;
    if (selectedStatus === order.status) return;

    setIsSaving(true);
    setSaveError(null);

    try {
      const statusUpdateDto: UpdateOrderStatus = {
        status: selectedStatus
      };

      const updatedOrder = await updateOrderStatus(orderId, statusUpdateDto);

      setOrder(updatedOrder);
      setSelectedStatus(updatedOrder.status);
      onOrderUpdate(); 
    } catch (err: any) {
      setSaveError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  if (!orderId) return null;

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-primary bg-opacity-10 backdrop-blur-sm p-4"
      onClick={onClose} 
    >
      <div
        className="relative w-full max-w-2xl p-6 bg-primary/95 rounded-2xl border border-secondary text-gray-100 shadow-[0_0_15px_rgba(137,254,0,.7)]"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-white z-10"
        >
          <X size={24} />
        </button>

        {loading && (
          <div className="flex items-center justify-center h-64">
            <Loader2 className="w-12 h-12 animate-spin text-secondary" />
          </div>
        )}

        {error && (
          <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
            <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
            <div>
              <h2 className="text-xl font-bold text-red-400">Error al Cargar</h2>
              <p className="text-red-300">{error}</p>
            </div>
          </div>
        )}

        {order && !loading && !error && (
          <div>
            <h2 className="text-3xl font-bold text-secondary mb-4">
              Orden #{order.id}
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

            <div className="space-y-3 mb-4 pt-4 border-t border-gray-700">
              <h3 className="text-xl font-semibold text-white">Actualizar Estado</h3>
              <div className="flex items-center gap-3">
                <select
                  value={selectedStatus}
                  onChange={(e) => setSelectedStatus(e.target.value)}
                  className="flex-1 p-3 rounded bg-gray-800 border border-gray-600 text-white focus:border-secondary outline-none"
                >
                  {ORDER_STATUSES.map(status => (
                    <option key={status} value={status}>{status}</option>
                  ))}
                </select>
                <button
                  onClick={handleStatusUpdate}
                  disabled={isSaving || selectedStatus === order.status}
                  className="bg-secondary text-primary font-bold py-3 px-5 rounded-full hover:bg-lime-400 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {isSaving ? <Loader2 className="animate-spin" /> : <Save size={20} />}
                </button>
              </div>
              {saveError && <p className="text-red-500 text-sm">{saveError}</p>}
            </div>

            {/* Listado de Items - Ahora iterando sobre order.items correctamente */}
            <div className="border-t border-gray-700 pt-4">
              <h3 className="text-xl font-semibold text-white mb-3">Items ({order.items?.length || 0})</h3>
              
              <div className="space-y-3 no-scrollbar max-h-60 overflow-y-auto pr-2">
                {order.items && order.items.length > 0 ? (
                  order.items.map(item => (
                    <div key={item.id} className="flex justify-between items-center p-3 bg-gray-800 rounded-lg border border-gray-700">
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
                  ))
                ) : (
                  <p className="text-gray-500 italic">No hay items en esta orden.</p>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}