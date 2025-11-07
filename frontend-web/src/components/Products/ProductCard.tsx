import React, { useState } from "react";
import { useCart } from "../../context/CartContext";
import type { 
  AnyProduct, 
  Apparel, 
  Supplement, 
  WorkoutAccessory 
} from "../../types/Product"; // Usamos los tipos correctos
import { Loader2 } from "lucide-react";

interface ProductCardProps {
  product: AnyProduct; // Tipo actualizado
}

// --- ¡NUEVO HELPER MEJORADO! ---
// Este componente decide qué detalle específico mostrar,
// con fallbacks para mostrar 'brand' si es lo único que hay.
const ProductSpecificDetail: React.FC<{ product: AnyProduct }> = ({ product }) => {
  let label = "";
  let value: string | undefined = "";

  // 1. Comprobamos las propiedades ESPECÍFICAS primero
  if ('flavor' in product && (product as Supplement).flavor) {
    label = "Sabor";
    value = (product as Supplement).flavor;
  } else if ('material' in product && (product as WorkoutAccessory).material) {
    label = "Material";
    value = (product as WorkoutAccessory).material;
  } 
  // 2. FALLBACK: Si no se encontró nada, comprobamos 'brand'.
  //    Esto funcionará para Apparel Y para productos genéricos
  //    que solo tengan la propiedad 'brand' (como tu "Proteina").
  else if ('brand' in product && (product as any).brand) {
    label = "Marca";
    value = (product as any).brand;
  }

  // Si no se encontró ningún detalle en absoluto, renderizamos un espacio
  // para mantener la altura de la tarjeta.
  if (!label || !value) {
    return <span className="bg-transparent px-2 py-1 rounded">&nbsp;</span>;
  }

  return (
    <span className="bg-gray-700 px-2 py-1 rounded">
      {label}: {value}
    </span>
  );
};


const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  if (!product) {
    return null; 
  }

  const { addToCart, cartItems, loading, isProductInCart } = useCart();
  const [isAdded, setIsAdded] = useState(false);
  const [isAdding, setIsAdding] = useState(false);

  const isInCart = isProductInCart(product.id);

  const handleAddToCart = async () => {
    // ... (Tu lógica de handleAddToCart está perfecta)
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
        {/* --- CAMBIO --- */}
        {/* Volvemos a usar <img> normal, como pediste */}
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

      {/* --- CAMBIO --- */}
      {/* Este div ahora usa el helper corregido */}
      <div className="space-y-1 min-h-[26px]"> {/* Forzar la altura */}
        <div className="flex flex-wrap gap-1 text-xs text-gray-400">
          <ProductSpecificDetail product={product} />
        </div>
      </div>

      {/* ... (El resto de tu JSX de precio, stock y botón de carrito está perfecto) ... */}
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
            w-full py-2 px-2 rounded-lg transition-all duration-300 flex items-center justify-center
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
            "Agregar al carrito"
          )}
        </button>
       
      </div>
    </div>
  );
};

export default ProductCard;