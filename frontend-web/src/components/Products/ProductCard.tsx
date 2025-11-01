// components/Product/ProductCard.tsx
import React, { useState } from "react";
import { useCart } from "../../context/CartContext";
import type { Product } from "../../types/Product";
import { Loader2 } from "lucide-react";

interface ProductCardProps {
  product: Product;
}

const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  if (!product) {
    return null; 
  }

  const { addToCart, cartItems, loading, isProductInCart } = useCart();
  const [isAdded, setIsAdded] = useState(false);
  const [isAdding, setIsAdding] = useState(false);

  const isInCart = isProductInCart(product.id);

  const handleAddToCart = async () => {
  if (isInCart || isAdding) return;

  if (!product.id) {
    console.error("Producto sin ID válido");
    return;
  }

  if (product.stock <= 0) {
    console.error("No hay stock disponible");
    return;
  }

  setIsAdding(true);
  try {
    await addToCart(product);
    setIsAdded(true);
    setTimeout(() => setIsAdded(false), 1500);
  } catch (error) {
    console.error('Error adding to cart:', error);
    // Mostrar feedback al usuario para error (toast, etc.)
  } finally {
    setIsAdding(false);
  }
};


  return (
    <div
      className="
        w-full
        bg-white/20 backdrop-blur-sm 
        border border-gray-700 
        rounded-xl      
        shadow-lg 
        p-3             
        space-y-2       
        ring-2 ring-lime-400/20 
        transition-all duration-300
        hover:ring-secondary
        hover:shadow-[0_0_15px_rgba(137,254,0,.7)] 
      "
    >
      <div className="aspect-square bg-white/90 rounded-xl overflow-hidden">
        <img
          src={product.imageUrl}
          alt={product.name}
          className="w-full h-full object-cover transform hover:scale-105 transition-transform duration-300"
          onError={(e) => {
            // Fallback si la imagen no carga
            (e.target as HTMLImageElement).src = "/images/placeholder-product.jpg";
          }}
        />
      </div>

      <h3 className="text-base font-bold text-secondary truncate">{product.name}</h3>

      {/* Información adicional del producto */}
      <div className="space-y-1">
        {/* <p className="text-sm text-gray-300 line-clamp-2">{product.description}</p> */}
        
        {(product.brand || product.color || product.size) && (
          <div className="flex flex-wrap gap-1 text-xs text-gray-400">
            {product.brand && (
              <span className="bg-gray-700 px-2 py-1 rounded">Marca: {product.brand}</span>
            )}
            {/* {product.color && (
              <span className="bg-gray-700 px-2 py-1 rounded"> {}</span>
            )}
            {product.size && (
              <span className="bg-gray-700 px-2 py-1 rounded"> {}</span>
            )} */}
          </div>
        )}
      </div>

      <div className="flex justify-between items-center text-gray-200">
        <div className="flex items-center gap-1">
          <svg 
            className="w-4 h-4 text-secondary" 
            fill="currentColor" 
            viewBox="0 0 20 20"
          >
            <path 
              fillRule="evenodd" 
              d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 01.75.75v2.5a.75.75 0 01-.75.75H2.75a.75.75 0 01-.75-.75v-2.5zM2.75 9a.75.75 0 00-.75.75v6c0 .414.336.75.75.75h14.5a.75.75 0 00.75-.75v-6a.75.75 0 00-.75-.75H2.75zM3.5 11a.5.5 0 01.5-.5h2a.5.5 0 01.5.5v.5a.5.5 0 01-.5.5h-2a.5.5 0 01-.5-.5v-.5z" 
              clipRule="evenodd" 
            />
          </svg>
         
          <span className="font-semibold text-xs"> 
            <strong>{product.stock}</strong> en stock
          </span>
        </div>
      
        <span className="text-base font-bold">${product.price.toFixed(2)}</span>
      </div>

      <div className="flex gap-1.5 text-xs font-bold">
        <button
          onClick={handleAddToCart}
          disabled={isAdded || isInCart || isAdding || loading || product.stock === 0}
          className={`
            w-full py-1 px-2 rounded-lg transition-all duration-300 flex items-center justify-center
            ${
              product.stock === 0
                ? "bg-gray-400 text-gray-800 cursor-not-allowed"
                : isAdded
                ? "bg-green-500 text-white"
                : isInCart
                ? "bg-gray-400 text-gray-800"
                : "bg-gray-200 text-gray-900 hover:bg-white"
            }
            ${(isAdding || loading) ? 'opacity-50 cursor-not-allowed' : ''}
          `}
        >
          {product.stock === 0 ? (
            "Agotado"
          ) : isAdding ? (
            <>
              <Loader2 className="w-3 h-3 animate-spin mr-1" />
              Agregando...
            </>
          ) : isAdded ? (
            "¡Añadido!"
          ) : isInCart ? (
            "En el carrito"
          ) : (
            "Add to cart"
          )}
        </button>
        <button
          disabled={product.stock === 0}
          className={`
            w-full bg-black text-white hover:text-primary 
            py-2 px-2 rounded-lg transition-colors
            ${product.stock === 0 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-secondary'}
          `}
        >
          {product.stock === 0 ? "Agotado" : "Buy Now"}
        </button>
      </div>
    </div>
  );
};

export default ProductCard;