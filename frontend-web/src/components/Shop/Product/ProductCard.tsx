import React, { useState } from "react";
import { useCart } from "../../../context/CartContext";
import type { 
  AnyProduct, 
  Apparel, 
  Supplement, 
  WorkoutAccessory 
} from "../../../types/Product"; 
import { Loader2, Eye } from "lucide-react"; // Importamos un ícono para ver detalles
import { Link } from "react-router-dom";

interface ProductCardProps {
  product: AnyProduct;
}

// Helper para obtener el detalle específico
const ProductSpecificDetail: React.FC<{ product: AnyProduct }> = ({ product }) => {
  let label = "";
  let value: string | undefined = "";

  if ('flavor' in product && (product as Supplement).flavor) {
    label = "Sabor";
    value = (product as Supplement).flavor;
  } else if ('material' in product && (product as WorkoutAccessory).material) {
    label = "Material";
    value = (product as WorkoutAccessory).material;
  } else if ('brand' in product && (product as any).brand) {
    label = "Marca";
    value = (product as any).brand;
  }

  if (!label || !value) {
    return <span className="bg-transparent px-2 py-1 rounded">&nbsp;</span>;
  }

  return (
    <span className="bg-gray-700 px-2 py-1 rounded">
      {label}: {value}
    </span>
  );
};

// --- HELPER PARA DEDUCIR EL TIPO DE PRODUCTO PARA LA URL ---
const getProductType = (product: AnyProduct): string => {
    if ('size' in product && 'apparelCategoryId' in product) return 'apparel';
    if ('flavor' in product && 'ingredients' in product) return 'supplement';
    if ('dimensions' in product && 'material' in product) return 'accessory';
    return 'general';
};

const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  if (!product) return null;

  const { addToCart, isProductInCart } = useCart();
  const [isAdded, setIsAdded] = useState(false);
  const [isAdding, setIsAdding] = useState(false);

  const isInCart = isProductInCart(product.id);
  const productType = getProductType(product); // Obtenemos el tipo para el link

  const handleAddToCart = async () => {
    if (isInCart || isAdding) return;
    if (!product.id || product.stock <= 0) return;

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
        w-full bg-white/20 backdrop-blur-sm border border-gray-700 rounded-xl shadow-lg p-3 space-y-2 ring-2 ring-lime-400/20 transition-all duration-300 hover:ring-secondary hover:shadow-[0_0_15px_rgba(137,254,0,.7)] flex flex-col justify-between
      "
    >
      {/* Contenido Superior */}
      <div>
        <div className="aspect-square bg-white/90 rounded-xl overflow-hidden mb-2">
          <Link to={`/product/${productType}/${product.id}`}>
             <img
              src={product.imageUrl}
              alt={product.name}
              className="w-full h-full object-cover transform hover:scale-105 transition-transform duration-300 cursor-pointer"
              onError={(e) => {
                (e.target as HTMLImageElement).src = "/images/placeholder-product.jpg";
              }}
            />
          </Link>
        </div>

        <h3 className="text-base font-bold text-secondary truncate mb-1">
            <Link to={`/product/${productType}/${product.id}`} className="hover:underline">
                {product.name}
            </Link>
        </h3>

        <div className="space-y-1 min-h-[26px]">
          <div className="flex flex-wrap gap-1 text-xs text-gray-400">
            <ProductSpecificDetail product={product} />
          </div>
        </div>
      </div>

      {/* Contenido Inferior (Precio y Botones) */}
      <div className="space-y-2 mt-2">
        <div className="flex justify-between items-center text-gray-200">
            <div className="flex items-center gap-1">
            <svg className="w-4 h-4 text-secondary" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 01.75.75v2.5a.75.75 0 01-.75.75H2.75a.75.75 0 01-.75-.75v-2.5zM2.75 9a.75.75 0 00-.75.75v6c0 .414.336.75.75.75h14.5a.75.75 0 00.75-.75v-6a.75.75 0 00-.75-.75H2.75zM3.5 11a.5.5 0 01.5-.5h2a.5.5 0 01.5.5v.5a.5.5 0 01-.5.5h-2a.5.5 0 01-.5-.5v-.5z" clipRule="evenodd" />
            </svg>
            <span className="font-semibold text-xs"> 
                <strong>{product.stock}</strong> en stock
            </span>
            </div>
            <span className="text-base font-bold">${product.price.toFixed(2)}</span>
        </div>

        {/* GRUPO DE BOTONES CON GRID PARA QUE SEAN IGUALES */}
        <div className="grid grid-cols-2 gap-2 text-xs font-bold">
            {/* Botón 1: Agregar al carrito */}
            <button
            onClick={handleAddToCart}
            disabled={isAdded || isInCart || isAdding || product.stock === 0}
            className={`
                w-full py-2 px-2 rounded-lg transition-all duration-300 flex items-center justify-center
                ${
                product.stock === 0
                    ? "bg-gray-400 text-gray-800 cursor-not-allowed col-span-2" // Si no hay stock, ocupa todo el ancho
                    : isAdded
                    ? "bg-green-500 text-white"
                    : isInCart
                    ? "bg-gray-400 text-gray-800"
                    : "bg-gray-200 text-gray-900 hover:bg-white"
                }
                ${(isAdding) ? 'opacity-50 cursor-not-allowed' : ''}
            `}
            >
            {product.stock === 0 ? (
                "Agotado"
            ) : isAdding ? (
                <Loader2 className="w-3 h-3 animate-spin" />
            ) : isAdded ? (
                "¡Añadido!"
            ) : isInCart ? (
                "En carrito"
            ) : (
                "Agregar"
            )}
            </button>

            {/* Botón 2: Ver Detalles (Solo se muestra si hay stock, sino el otro ocupa todo) */}
            {product.stock > 0 && (
                <Link 
                    to={`/product/${productType}/${product.id}`}
                    className="w-full py-2 px-2 rounded-lg border border-secondary text-secondary hover:bg-secondary hover:text-primary transition-all duration-300 flex items-center justify-center gap-1"
                >
                    <span/> Detalles
                </Link>
            )}
        </div>
      </div>
    </div>
  );
};

export default ProductCard;