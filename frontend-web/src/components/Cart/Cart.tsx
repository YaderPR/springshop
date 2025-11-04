// components/Cart/Cart.tsx
import React from 'react';
import { useCart } from '../../hooks/useCart';
import { ShoppingCart, Trash2, Plus, Minus, Loader2 } from 'lucide-react';

interface CartProps {
  cartId: number;
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
      await removeItem(itemId);
    } else {
      await updateItem(itemId, { productId, quantity: newQuantity });
    }
  };

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

      {cartItems.length === 0 ? (
        <div className="text-center py-8">
          <ShoppingCart className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">Tu carrito está vacío</p>
        </div>
      ) : (
        <>
          <div className="space-y-4">
            {cartItems.map((item) => (
              <div key={item.id} className="flex items-center justify-between border-b pb-4">
                <div className="flex-1">
                  <h3 className="font-semibold">Producto #{item.productId}</h3>
                  <p className="text-gray-600">${item.price.toFixed(2)} c/u</p>
                </div>
                
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleQuantityChange(item.id, item.productId, item.quantity - 1)}
                    disabled={loading}
                    className="p-1 rounded-full hover:bg-gray-100 disabled:opacity-50"
                  >
                    <Minus className="w-4 h-4" />
                  </button>
                  
                  <span className="w-8 text-center font-medium">{item.quantity}</span>
                  
                  <button
                    onClick={() => handleQuantityChange(item.id, item.productId, item.quantity + 1)}
                    disabled={loading}
                    className="p-1 rounded-full hover:bg-gray-100 disabled:opacity-50"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>

                <div className="text-right min-w-20">
                  <p className="font-semibold">
                    ${(item.price * item.quantity).toFixed(2)}
                  </p>
                  <button
                    onClick={() => removeItem(item.id)}
                    disabled={loading}
                    className="text-red-600 hover:text-red-800 text-sm disabled:opacity-50"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>

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