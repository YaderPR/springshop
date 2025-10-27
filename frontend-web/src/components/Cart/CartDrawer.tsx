import React, { useState } from "react";
import { useCart } from "../../context/CartContext";
import { X, Trash2, Plus, Minus } from "lucide-react";

export default function CartDrawer({ isCartOpen, closeCart }) {
  const { cartItems, removeFromCart, updateQuantity, totalPrice } = useCart();
  const [removingId, setRemovingId] = useState<number | null>(null);

  const handleRemove = (id: number) => {
    setRemovingId(id);

    // Esperamos el tiempo de la animación antes de eliminar del estado global
    setTimeout(() => {
      removeFromCart(id);
      setRemovingId(null);
    }, 300);
  };

  return (
    <>
      <div
        className={`fixed top-0 right-0 h-full w-full max-w-md bg-primary shadow-xl 
        transition-transform duration-300 ease-in-out z-50 flex flex-col 
        ${isCartOpen ? "translate-x-0" : "translate-x-full"}`}
      >
        <div className="flex justify-between items-center p-4 border-b border-gray-700">
          <h2 className="text-xl font-bold text-secondary">Tu Carrito</h2>
          <button onClick={closeCart} className="text-gray-400 hover:text-white">
            <X size={24} />
          </button>
        </div>

        {/* Contenido */}
        {cartItems.length === 0 ? (
          <div className="flex-1 flex flex-col justify-center items-center text-gray-400">
            <p className="text-lg">Tu carrito está vacío</p>
            <button
              onClick={closeCart}
              className="mt-4 bg-secondary text-primary font-bold py-2 px-4 rounded-full"
            >
              Seguir comprando
            </button>
          </div>
        ) : (
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {cartItems.map((item) => (
              <div
                key={item.id}
                className={`flex gap-4 items-center transition-all duration-300 ${
                  removingId === item.id
                    ? "opacity-0 translate-x-[-20px]"
                    : "opacity-100 translate-x-0"
                }`}
              >
                <img
                  src={item.product?.imageUrl || "/placeholder.png"}
                  alt={item.product?.name || "Producto"}
                  className="w-20 h-20 rounded-lg object-cover"
                />
                <div className="flex-1">
                  <h3 className="font-semibold text-white truncate">
                    {item.product?.name || "Producto sin nombre"}
                  </h3>
                  <p className="text-secondary font-bold">
                    ${item.price.toFixed(2)}
                  </p>

                  <div className="flex items-center gap-2 mt-2">
                    <button
                      onClick={() =>
                        updateQuantity(item.id, item.productId, item.quantity - 1)
                      }
                      disabled={item.quantity <= 1}
                      className="bg-gray-700 p-1 rounded-full text-white disabled:opacity-50"
                    >
                      <Minus size={16} />
                    </button>

                    <span className="text-white w-8 text-center">
                      {item.quantity}
                    </span>

                    <button
                      onClick={() =>
                        updateQuantity(item.id, item.productId, item.quantity + 1)
                      }
                      className="bg-gray-700 p-1 rounded-full text-white"
                    >
                      <Plus size={16} />
                    </button>
                  </div>
                </div>

                <button
                  onClick={() => handleRemove(item.id)}
                  className="text-red-500 hover:text-red-400 disabled:opacity-50"
                >
                  <Trash2 size={20} />
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Footer */}
        {cartItems.length > 0 && (
          <div className="p-4 border-t border-gray-700 space-y-4">
            <div className="flex justify-between text-lg font-bold text-white">
              <span>Subtotal:</span>
              <span>${totalPrice.toFixed(2)}</span>
            </div>
            <button className="w-full bg-secondary text-primary font-bold py-3 rounded-full hover:bg-lime-400 transition-all">
              Ir al Checkout
            </button>
          </div>
        )}
      </div>

      {/* Overlay */}
      <div
        onClick={closeCart}
        className={`fixed inset-0 bg-black/50 backdrop-blur-sm z-40 transition-opacity duration-300 ${
          isCartOpen ? "opacity-100" : "opacity-0 pointer-events-none"
        }`}
      />
    </>
  );
}
