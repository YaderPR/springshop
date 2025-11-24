// components/Cart/Cart.tsx
import React from 'react';
// Importamos useCart del hook base. Si usas Context, recuerda cambiar el import a: 
// import { useCart } from '../../../context/CartContext'; 
import { useCart } from '../../../hooks/useCart'; 
import { ShoppingCart, Trash2, Plus, Minus, Loader2 } from 'lucide-react';

interface CartProps {
  cartId: number; // Propiedad necesaria si NO se usa CartProvider
}

export const Cart: React.FC<CartProps> = ({ cartId }) => {
  
  const { 
    cartItems, 
    total, 
    loading, 
    error, 
    removeItem, 
    updateItem, 
    clearCart 
  } = useCart(cartId); 

  const handleQuantityChange = async (itemId: number, productId: number, newQuantity: number) => {
    if (newQuantity < 1) {
      // Si la cantidad es menor a 1, remover el ítem.
      await removeItem(itemId);
    } else {
      // Actualizar la cantidad.
      await updateItem(itemId, { productId, quantity: newQuantity });
    }
  };

  // --- 1. Estados de Carga y Error ---
  if (loading && cartItems.length === 0) {
    return (
      <div className="flex justify-center items-center p-8">
        <Loader2 className="w-6 h-6 animate-spin" />
        <span className="ml-2">Cargando carrito...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4">
        <p className="text-red-800">Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold flex items-center">
          <ShoppingCart className="w-6 h-6 mr-2" />
          Mi Carrito
        </h2>
        {cartItems.length > 0 && (
          <button
            onClick={clearCart}
            disabled={loading}
            className="flex items-center text-red-600 hover:text-red-800 disabled:opacity-50"
          >
            <Trash2 className="w-4 h-4 mr-1" />
            Limpiar
          </button>
        )}
      </div>

      {/* --- 2. Contenido del Carrito --- */}
      {cartItems.length === 0 ? (
        <div className="text-center py-8">
          <ShoppingCart className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">Tu carrito está vacío</p>
        </div>
      ) : (
        <>
          <div className="space-y-4">
            {cartItems.map((item) => (
              <div 
                key={item.id} 
                className="flex items-center justify-between border-b pb-4 last:border-b-0"
              >
                
                {/* **Detalles del Producto (Actualizado)** */}
                <div className="flex items-center flex-1">
                  <img 
                    // Utilizamos la imagen del producto fusionado (FullCartItem)
                    src={item.product?.imageUrl || '/placeholder.png'} 
                    alt={item.product?.name || 'Producto'}
                    className="w-12 h-12 object-cover rounded mr-4"
                  />
                  <div>
                    {/* Utilizamos el nombre del producto fusionado (FullCartItem) */}
                    <h3 className="font-semibold">{item.product?.name || `Producto #${item.productId}`}</h3>
                    <p className="text-gray-600">${item.price.toFixed(2)} c/u</p>
                  </div>
                </div>
                
                {/* Control de Cantidad */}
                <div className="flex items-center space-x-2 mx-4">
                  <button
                    onClick={() => handleQuantityChange(item.id, item.productId, item.quantity - 1)}
                    disabled={loading || item.quantity <= 1} // Deshabilita si ya es 1
                    className="p-1 rounded-full hover:bg-gray-100 disabled:opacity-30"
                  >
                    <Minus className="w-4 h-4" />
                  </button>
                  
                  <span className="w-8 text-center font-medium">{item.quantity}</span>
                  
                  <button
                    onClick={() => handleQuantityChange(item.id, item.productId, item.quantity + 1)}
                    disabled={loading}
                    className="p-1 rounded-full hover:bg-gray-100 disabled:opacity-30"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>

                {/* Subtotal y Botón de Remover */}
                <div className="text-right min-w-20">
                  <p className="font-semibold">
                    ${(item.price * item.quantity).toFixed(2)}
                  </p>
                  <button
                    onClick={() => removeItem(item.id)}
                    disabled={loading}
                    className="text-red-600 hover:text-red-800 text-sm disabled:opacity-50"
                  >
                    <Trash2 className="w-4 h-4 mx-auto" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* --- 3. Total del Carrito --- */}
          <div className="mt-6 pt-4 border-t">
            <div className="flex justify-between items-center text-xl font-bold">
              <span>Total:</span>
              <span>${total.toFixed(2)}</span>
            </div>
          </div>
        </>
      )}
    </div>
  );
};