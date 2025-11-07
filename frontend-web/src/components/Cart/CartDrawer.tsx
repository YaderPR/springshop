import React from 'react';
import { X, Plus, Minus, Trash2, ShoppingCart } from 'lucide-react';
import { useCart } from '../../context/CartContext';
import { Link } from 'react-router-dom';
import CheckoutButton from '../Checkout/CheckoutButton';


interface CartDrawerProps {
  isCartOpen?: boolean;
  closeCart?: () => void;
}


const CartDrawer: React.FC<CartDrawerProps> = ({ isCartOpen = false, closeCart }) => {
  const { 
    cartItems, 
    total, 
    loading, 
    updateCartItem, 
    removeFromCart, 
    clearCart,
    cartId
  } = useCart();

  const handleQuantityChange = async (itemId: number, productId: number, newQuantity: number) => {
    if (newQuantity < 1) {
      await removeFromCart(itemId);
    } else {
      await updateCartItem(itemId, productId, newQuantity);
    }
  };

  const handleRemoveItem = async (itemId: number) => {
    await removeFromCart(itemId);
  };

  const handleClearCart = async () => {
    await clearCart();
  };


  if (!isCartOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex backdrop-blur-lg">
      <div 
        className={`fixed inset-0 bg-black transition-opacity duration-300 ease-in-out ${
          isCartOpen ? "opacity-50 backdrop-blur-sm" : "opacity-0 pointer-events-none"
        }`}
        onClick={closeCart}
      />

      <div 
        className={`fixed inset-y-0 right-0 w-full max-w-md bg-primary shadow-xl 
          transition-transform duration-300 ease-in-out z-50 flex flex-col
          ${isCartOpen ? "translate-x-0" : "translate-x-full"}`
        }
      >
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-6 bg-primary text-white">
          <div className="flex items-center space-x-2">
            <ShoppingCart className="w-6 h-6" />
            <h2 className="text-lg font-semibold">Mi Carrito</h2>
            {cartItems.length > 0 && (
              <span className="bg-secondary text-primary text-xs font-bold px-2 py-1 rounded-full">
                {cartItems.reduce((total, item) => total + item.quantity, 0)}
              </span>
            )}
          </div>
          <button
            onClick={closeCart}
            className="rounded-md p-2 text-white hover:text-secondary transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto">
          {loading ? (
            <div className="flex justify-center items-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
              <span className="ml-2 text-gray-600">Cargando...</span>
            </div>
          ) : cartItems.length === 0 ? (
            <div className="flex flex-col items-center justify-end py-12 px-4">
              <ShoppingCart className="w-16 h-16 text-gray-400 mb-4" />
              <p className="text-secondary text-lg mb-4">Tu carrito está vacío</p>
              <button
                onClick={closeCart}
                className="bg-secondary text-primary px-6 py-2 rounded-xl hover:bg-primary/90 hover:text-secondary ring-2 ring-secondary  transition-colors"
              >
                Seguir comprando
              </button>
            </div>
          ) : (
            <div className="p-4 space-y-4">
              {/* Cart Items */}
              {cartItems.map((item) => {
                const productName = item.product?.name || `Producto #${item.productId}`;
                const productImage = item.product?.imageUrl || '/images/placeholder-product.jpg';
                const productPrice = item.product?.price || item.price || 0;

                return (
                  <div key={item.id} className="flex items-center space-x-3 bg-gray-800 rounded-lg p-3">
                    {/* Product Image */}
                    <div className="flex-shrink-0 text-gray-50">
                      <img
                        src={productImage}
                        alt={productName}
                        className="w-16 h-16 object-cover rounded-md"
                      />
                    </div>
                    
                    {/* Product Info */}
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-secondary truncate">
                        {productName}
                      </p>
                      <p className="text-sm text-gray-50">${productPrice.toFixed(2)} c/u</p>
                      
                      {/* Quantity Controls */}
                      <div className="flex items-center space-x-2 mt-2">
                        <button
                          onClick={() => handleQuantityChange(item.id, item.productId, item.quantity - 1)}
                          disabled={loading}
                          className="p-1 rounded-full hover:bg-gray-300 text-secondary disabled:opacity-50 transition-colors"
                        >
                          <Minus className="w-3 h-3" />
                        </button>
                        
                        <span className="w-6 text-center text-gray-200 font-medium text-sm">
                          {item.quantity}
                        </span>
                        
                        <button
                          onClick={() => handleQuantityChange(item.id, item.productId, item.quantity + 1)}
                          disabled={loading} 
                          className="p-1 rounded-full hover:bg-gray-300 text-secondary disabled:opacity-50 transition-colors"
                        >
                          <Plus className="w-3 h-3" />
                        </button>
                      </div>
                    </div>

                    <div className="text-right">
                      <p className="font-semibold text-sm text-gray-50">
                        ${(item.price * item.quantity).toFixed(2)}
                      </p>
                      <button
                        onClick={() => handleRemoveItem(item.id)}
                        disabled={loading}
                        className="text-red-600 hover:text-red-800 text-xs disabled:opacity-50 transition-colors mt-1"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Footer */}
        {cartItems.length > 0 && (
          <div className="border-t border-gray-200 p-4 space-y-4">
            <div className="flex justify-between items-center text-lg font-semibold text-gray-50">
              <span>Total:</span>
              <span>${total.toFixed(2)}</span>
            </div>
            
            <div className="flex space-x-2">
              <button
                onClick={handleClearCart}
                disabled={loading}
                className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-300 disabled:opacity-50 transition-colors flex items-center justify-center text-sm"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                Limpiar
              </button>
              
              <Link
                to="/checkout"
                onClick={closeCart}
                className="flex-1 bg-secondary text-primary py-2 px-4 rounded-lg hover:bg-primary/90 hover:text-secondary ring-1 ring-secondary transition-colors text-center text-sm font-semibold"
              >
                Proceder al Pago
              </Link>
              
            </div>
            
            <p className="text-xs text-gray-500 text-center">
              ¿Necesitas ayuda? <a href="#" className="text-secondary hover:underline">Contáctanos</a>
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default CartDrawer;