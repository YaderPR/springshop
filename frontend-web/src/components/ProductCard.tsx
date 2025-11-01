// import React, { useState } from "react";
// import { useCart } from "../context/CartContext";
// import type { Product } from "../types/Product";

// interface ProductCardProps {
//   product: Product;
// }

// const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
//   if (!product) {
//     return null; 
//   }

//   const { addToCart, cartItems } = useCart();
//   const [isAdded, setIsAdded] = useState(false);

//   const { cart } = useCart();
//   const isInCart = cartItems?.some((item) => item.id === product.id);

//   const handleAddToCart = () => {
//     addToCart(product);
//     setIsAdded(true);
//     setTimeout(() => {
//       setIsAdded(false);
//     }, 1500);
//   };

//   return (
//     <div
//       className="
//         w-full
//         bg-white/20 backdrop-blur-sm 
//         border border-gray-700 
//         rounded-xl      
//         shadow-lg 
//         p-3             
//         space-y-2       
//         ring-2 ring-lime-400/20 
//         transition-all duration-300
//         hover:ring-secondary
//         hover:shadow-[0_0_15px_rgba(137,254,0,.7)] 
//       "
//     >
//       <div className="aspect-square bg-white/90 rounded-xl overflow-hidden">
//         <img
//           src={product.imageUrl}
//           alt={product.name}
//           className="w-full h-full object-cover transform hover:scale-105 transition-transform duration-300"
//         />
//       </div>

//       <h3 className="text-base font-bold text-secondary truncate">{product.name}</h3>

//       <div className="flex justify-between items-center text-gray-200">
//         <div className="flex items-center gap-1">
//           <svg 
//             className="w-4 h-4 text-secondary" 
//             fill="currentColor" 
//             viewBox="0 0 20 20"
//           >
//             <path 
//               fillRule="evenodd" 
//               d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 01.75.75v2.5a.75.75 0 01-.75.75H2.75a.75.75 0 01-.75-.75v-2.5zM2.75 9a.75.75 0 00-.75.75v6c0 .414.336.75.75.75h14.5a.75.75 0 00.75-.75v-6a.75.75 0 00-.75-.75H2.75zM3.5 11a.5.5 0 01.5-.5h2a.5.5 0 01.5.5v.5a.5.5 0 01-.5.5h-2a.5.5 0 01-.5-.5v-.5z" 
//               clipRule="evenodd" 
//             />
//           </svg>
         
//           <span className="font-semibold text-xs"> <strong>{product.stock}</strong> en stock</span>
//         </div>
      
//         <span className="text-base font-bold">${product.price.toFixed(2)}</span>
//       </div>

//       <div className="flex gap-1.5 text-xs font-bold">
//         <button
//           onClick={handleAddToCart}
//           disabled={isAdded || isInCart}
//           className={`
//             w-full py-1 px-2 rounded-lg transition-all duration-300
//             ${
//               isAdded
//                 ? "bg-green-500 text-white"
//                 : isInCart
//                 ? "bg-gray-400 text-gray-800"
//                 : "bg-gray-200 text-gray-900 hover:bg-white"
//             }
//           `}
//         >
//           {isAdded ? "¡Añadido!" : isInCart ? "En el carrito" : "Add to cart"}
//         </button>
//         <button
//           className="
//             w-full bg-black text-white hover:text-primary 
//             py-2 px-2 rounded-lg 
//             hover:bg-secondary transition-colors
//           "
//         >
//           Buy Now
//         </button>
//       </div>
//     </div>
//   );
// };

// export default ProductCard;