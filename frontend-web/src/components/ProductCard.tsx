import React from 'react';

interface ProductCardProps {
  name: string;
  price: number;
  rating: number;
  imageUrl: string;
}

const ProductCard: React.FC<ProductCardProps> = ({ name, price, rating, imageUrl }) => {
  return (
    // Contenedor principal de la tarjeta
    <div className="
      w-full max-w-sm 
      bg-white/20 backdrop-blur-sm 
      border border-gray-700 
      rounded-2xl 
      shadow-lg 
      p-4 
      space-y-4
      ring-2 ring-lime-400/20 hover:ring-secondary transition-all duration-300
    ">
      
      <div className="aspect-square bg-white/90 rounded-xl overflow-hidden">
        <img src={imageUrl} alt={name} className="w-full h-full object-cover" />
      </div>

      <h3 className="text-2xl font-bold text-secondary truncate">{name}</h3>
      
      {/* Sección de Rating y Precio */}
      <div className="flex justify-between items-center text-gray-200">
        <div className="flex items-center gap-1">
          
          <svg className="w-5 h-5 text-secondary" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.957a1 1 0 00.95.69h4.162c.969 0 1.371 1.24.588 1.81l-3.367 2.445a1 1 0 00-.364 1.118l1.287 3.957c.3.921-.755 1.688-1.54 1.118l-3.367-2.445a1 1 0 00-1.175 0l-3.367 2.445c-.784.57-1.838-.197-1.54-1.118l1.287-3.957a1 1 0 00-.364-1.118L2.07 9.384c-.783-.57-.38-1.81.588-1.81h4.162a1 1 0 00.95-.69L9.049 2.927z"></path>
          </svg>
          <span className="font-semibold">{rating.toFixed(1)}</span>
        </div>
        <span className="text-xl font-bold">${price.toFixed(2)}</span>
      </div>

      {/* Botones de Acción */}
      <div className="flex gap-3 text-sm font-bold">
        <button className="
          w-full 
          bg-gray-200 text-gray-900 
          py-3 
          rounded-full 
          hover:bg-white transition-colors
        ">
          Add to cart
        </button>
        <button className="
          w-full 
          bg-black text-white 
          py-3 
          rounded-full 
          hover:bg-gray-900 transition-colors
        ">
          Buy Now
        </button>
      </div>
    </div>
  );
};

export default ProductCard;