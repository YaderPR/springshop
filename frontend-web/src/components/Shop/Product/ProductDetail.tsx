import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShoppingCart, ArrowLeft, AlertTriangle, Check, Info, Ruler, Truck } from 'lucide-react';
import { useCart } from '../../../context/CartContext'; 
import * as ProductService from '../../../services/product/ProductService'; 
import type { AnyProduct, Apparel, Supplement, WorkoutAccessory } from '../../../types/Product';

type ProductType = 'general' | 'apparel' | 'supplement' | 'accessory';

const ProductDetail: React.FC = () => {
  const { type, id } = useParams<{ type: ProductType; id: string }>();
  const navigate = useNavigate();
  // Asumiendo que addToCart solo recibe (product) basado en tu error
  const { addToCart } = useCart(); 

  const [product, setProduct] = useState<AnyProduct | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [quantity, setQuantity] = useState<number>(1);
  const [isAdding, setIsAdding] = useState(false); // Estado visual para el botón

  useEffect(() => {
    const fetchProduct = async () => {
      if (!id || !type) return;
      setLoading(true);
      setError(null);
      const productId = parseInt(id);

      try {
        let data: AnyProduct;

        switch (type) {
          case 'apparel':
            data = await ProductService.getApparelById(productId);
            break;
          case 'supplement':
            data = await ProductService.getSupplementById(productId);
            break;
          case 'accessory':
            data = await ProductService.getWorkoutAccessoryById(productId);
            break;
          default:
            // CORRECCIÓN 1: Casteamos a AnyProduct para evitar el error de tipo
            data = await ProductService.getProductById(productId) as AnyProduct;
        }
        setProduct(data);
      } catch (err) {
        console.error(err);
        setError("No se pudo cargar el producto. Verifica tu conexión.");
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id, type]);

  // --- Manejadores ---
  const handleAddToCart = async () => {
    if (product) {
      setIsAdding(true);
      try {
        // CORRECCIÓN 2: El error decía que solo acepta 1 argumento.
        // Si tu Context no soporta (product, quantity), hacemos un bucle temporalmente
        // o simplemente enviamos el producto una vez.
        
        // Opción A: Si tu addToCart es inteligente y suma:
        // await addToCart(product, quantity); <--- Esto daba error
        
        // Opción B (Solución Parche para que compile): Llamamos n veces (no es óptimo pero funciona si el context no soporta qty)
        // O idealmente, actualiza tu CartContext para aceptar qty.
        // Aquí asumo que lo llamamos una vez y tú arreglarás el context luego,
        // o hacemos un bucle rápido:
        for (let i = 0; i < quantity; i++) {
           await addToCart(product);
        }
        
      } catch (error) {
        console.error("Error adding to cart", error);
      } finally {
        setIsAdding(false);
      }
    }
  };

  const incrementQty = () => setQuantity(prev => prev + 1);
  const decrementQty = () => setQuantity(prev => (prev > 1 ? prev - 1 : 1));

  // --- Renderizado Condicional ---
  if (loading) return (
    <div className="flex justify-center items-center min-h-screen bg-gray-900">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-secondary"></div>
    </div>
  );

  if (error || !product) return (
    <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center">
      <AlertTriangle className="w-16 h-16 text-red-500 mb-4" />
      <h2 className="text-2xl font-bold mb-2">Producto no encontrado</h2>
      <p className="text-gray-400 mb-6">{error || "El producto que buscas no existe."}</p>
      <button onClick={() => navigate(-1)} className="text-secondary hover:underline flex items-center gap-2">
        <ArrowLeft className="w-4 h-4" /> Volver
      </button>
    </div>
  );

  // --- TYPE GUARDS ---
  const isApparel = (p: AnyProduct): p is Apparel => 'size' in p && 'apparelCategoryId' in p;
  const isSupplement = (p: AnyProduct): p is Supplement => 'flavor' in p && 'ingredients' in p;
  const isAccessory = (p: AnyProduct): p is WorkoutAccessory => 'dimensions' in p && 'material' in p;

  return (
    <div className="min-h-screen bg-gray-500/20 text-gray-100 py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <button onClick={() => navigate(-1)} className="mb-6 flex items-center text-gray-400 hover:text-secondary transition-colors">
          <ArrowLeft className="w-5 h-5 mr-2" />
          Volver al catálogo
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Imagen */}
          <div className="space-y-4">
            <div className="aspect-square w-full overflow-hidden rounded-2xl bg-gray-800 border border-gray-700 shadow-xl">
              <img
                src={product.imageUrl || "/api/placeholder/600/600"}
                alt={product.name}
                className="h-full w-full object-cover object-center transform hover:scale-105 transition-transform duration-500"
              />
            </div>
          </div>

          {/* Información */}
          <div className="flex flex-col">
            <div className="mb-4">
              <span className="text-secondary font-semibold tracking-wider text-sm uppercase">
                {product.categoryName || type?.toUpperCase()}
              </span>
              <h1 className="text-3xl sm:text-4xl font-extrabold text-white mt-2 tracking-tight">
                {product.name}
              </h1>
            </div>

            <div className="flex items-end gap-4 mb-6 border-b border-gray-800 pb-6">
              <p className="text-3xl font-bold text-white">${product.price.toFixed(2)}</p>
              {product.stock > 0 ? (
                <span className="flex items-center text-green-400 text-sm font-medium mb-1 bg-green-400/10 px-2 py-1 rounded">
                  <Check className="w-4 h-4 mr-1" /> En Stock ({product.stock})
                </span>
              ) : (
                <span className="text-red-400 text-sm font-medium mb-1 bg-red-400/10 px-2 py-1 rounded">
                  Agotado
                </span>
              )}
            </div>

            <div className="prose prose-invert mb-8">
              <p className="text-gray-300 leading-relaxed">{product.description}</p>
            </div>

            {/* Renderizado específico según tipo */}
            {isApparel(product) && (
              <div className="bg-gray-800/50 rounded-xl p-6 mb-8 border border-gray-700 space-y-4">
                <h3 className="font-semibold text-white flex items-center gap-2">
                  <Info className="w-5 h-5 text-secondary" /> Detalles de Prenda
                </h3>
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-gray-800 p-3 rounded-lg">
                    <span className="block text-xs text-gray-400">Marca</span>
                    <span className="font-medium text-white">{product.brand}</span>
                  </div>
                  <div className="bg-gray-800 p-3 rounded-lg">
                    <span className="block text-xs text-gray-400">Color</span>
                    <div className="flex items-center gap-2">
                      <span className="w-4 h-4 rounded-full border border-gray-500" style={{ backgroundColor: product.color.toLowerCase() }}></span>
                      <span className="font-medium text-white">{product.color}</span>
                    </div>
                  </div>
                  <div className="col-span-2 bg-gray-800 p-3 rounded-lg flex justify-between items-center">
                    <span className="text-sm text-gray-400">Talla Seleccionada</span>
                    <span className="font-bold text-xl text-secondary">{product.size}</span>
                  </div>
                </div>
              </div>
            )}

            {isSupplement(product) && (
              <div className="space-y-6 mb-8">
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-gray-800 p-4 rounded-xl border border-gray-700">
                    <span className="block text-xs text-gray-400 mb-1">Sabor</span>
                    <span className="font-bold text-lg text-white">{product.flavor}</span>
                  </div>
                  <div className="bg-gray-800 p-4 rounded-xl border border-gray-700">
                    <span className="block text-xs text-gray-400 mb-1">Presentación</span>
                    <span className="font-bold text-lg text-white">{product.size}</span>
                  </div>
                </div>
                <div className="bg-gray-800/50 rounded-xl p-5 border border-gray-700">
                  <h4 className="font-semibold text-secondary mb-2">Ingredientes</h4>
                  <p className="text-sm text-gray-300 mb-4 leading-relaxed">{product.ingredients}</p>
                  <h4 className="font-semibold text-secondary mb-2">Instrucciones de Uso</h4>
                  <p className="text-sm text-gray-300">{product.usageInstructions}</p>
                </div>
                {product.warnings && (
                  <div className="bg-red-900/20 border border-red-900/50 p-4 rounded-xl flex gap-3">
                    <AlertTriangle className="w-6 h-6 text-red-500 flex-shrink-0" />
                    <div>
                      <h4 className="text-red-400 font-bold text-sm">Precauciones</h4>
                      <p className="text-xs text-red-200/80 mt-1">{product.warnings}</p>
                    </div>
                  </div>
                )}
              </div>
            )}

            {isAccessory(product) && (
              <div className="bg-gray-800/50 rounded-xl p-6 mb-8 border border-gray-700">
                 <h3 className="font-semibold text-white flex items-center gap-2 mb-4">
                  <Ruler className="w-5 h-5 text-secondary" /> Especificaciones Técnicas
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-y-4 gap-x-8 text-sm">
                  <div className="flex justify-between border-b border-gray-700 pb-2">
                    <span className="text-gray-400">Material</span>
                    <span className="text-white font-medium">{product.material}</span>
                  </div>
                  <div className="flex justify-between border-b border-gray-700 pb-2">
                    <span className="text-gray-400">Dimensiones</span>
                    <span className="text-white font-medium">{product.dimensions}</span>
                  </div>
                  <div className="flex justify-between border-b border-gray-700 pb-2">
                    <span className="text-gray-400">Peso</span>
                    <span className="text-white font-medium">{product.weight} kg</span>
                  </div>
                  <div className="flex justify-between border-b border-gray-700 pb-2">
                    <span className="text-gray-400">Color</span>
                    <span className="text-white font-medium">{product.color}</span>
                  </div>
                </div>
              </div>
            )}

            {/* Controles de Carrito */}
            <div className="mt-auto">
              <div className="flex items-center gap-4 mb-4">
                <div className="flex items-center bg-gray-800 rounded-lg border border-gray-700">
                  <button 
                    onClick={decrementQty}
                    className="px-4 py-3 text-gray-400 hover:text-white hover:bg-gray-700 rounded-l-lg transition-colors"
                  >
                    -
                  </button>
                  <span className="w-12 text-center text-white font-bold">{quantity}</span>
                  <button 
                    onClick={incrementQty}
                    className="px-4 py-3 text-gray-400 hover:text-white hover:bg-gray-700 rounded-r-lg transition-colors"
                  >
                    +
                  </button>
                </div>
                <button
                  onClick={handleAddToCart}
                  disabled={product.stock === 0 || isAdding}
                  className="flex-1 bg-secondary text-primary font-bold py-3 px-6 rounded-lg hover:bg-lime-400 transition-all shadow-[0_0_15px_rgba(137,254,0,0.4)] disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none flex items-center justify-center gap-2"
                >
                  <ShoppingCart className="w-5 h-5" />
                  {isAdding ? "Agregando..." : (product.stock > 0 ? "Agregar al Carrito" : "Sin Stock")}
                </button>
              </div>
              
              <p className="flex items-center justify-center text-xs text-gray-500 gap-2 mt-4">
                <Truck className="w-4 h-4" /> Envío calculado al finalizar la compra
              </p>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;