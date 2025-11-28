import { useRef, useState, useEffect } from 'react';
import ProductCard from '../../Shop/Product/ProductCard';
// 1. Importamos AnyProduct para el tipado correcto
import type { AnyProduct } from "../../../types/Product"; 
// 2. Importamos los servicios específicos para traer la data completa
import { 
  getApparels, 
  getSupplements, 
  getWorkoutAccessories 
} from '../../../services/product/ProductService';

export default function RecommendedSection() {
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  
  // 3. CORRECCIÓN DE TIPO: Usamos AnyProduct[] en lugar de Product[]
  const [products, setProducts] = useState<AnyProduct[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        // 4. CORRECCIÓN DE DATA: Traemos todo de los endpoints específicos
        // Esto asegura que 'flavor', 'material', etc., vengan en la respuesta
        const [apparelsData, supplementsData, accessoriesData] = await Promise.all([
          getApparels(),
          getSupplements(),
          getWorkoutAccessories(),
        ]);

        // Combinamos todos los productos
        const allProducts = [
            ...apparelsData, 
            ...supplementsData, 
            ...accessoriesData
        ];

        // Opcional: Mezclarlos aleatoriamente para que no salgan siempre en el mismo orden
        // allProducts.sort(() => Math.random() - 0.5);

        // Tomamos los primeros 5 o los que necesites
        setProducts(allProducts.slice(0, 6)); 

      } catch (error) {
        console.error("Error al cargar productos recomendados:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []); 

  const scroll = (direction: 'left' | 'right') => {
    if (scrollContainerRef.current) {
      const scrollAmount = direction === 'left' ? -300 : 300; 
      scrollContainerRef.current.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  };

  return (
    <section className="py-20 ">
      <div className="container mx-auto px-2 sm:px-12 lg:px-20 relative">
        <h2 className="text-3xl font-bold text-white mb-8">Recomendado para ti</h2>
        
        <div
          ref={scrollContainerRef} 
          className="flex overflow-x-auto pb-6 pt-6 pl-6 no-scrollbar gap-6" 
        >
          {isLoading ? (
            <div className="flex gap-4">
                 {/* Skeleton loading simple */}
                 {[1,2,3].map(i => <div key={i} className="w-72 h-96 bg-gray-800 rounded-xl animate-pulse"></div>)}
            </div>
          ) : (
            products.map((product) => (
              <div key={`${product.id}-${product.name}`} className="flex-none w-72"> 
                {/* Ahora 'product' es de tipo AnyProduct, compatible con ProductCard */}
                <ProductCard product={product} /> 
              </div>
            ))
          )}
        </div>

        {/* Botones de Scroll */}
        <button 
          onClick={() => scroll('left')}
          className="absolute top-1/2 left-0 sm:left-4 lg:left-12 transform -translate-y-1/2 bg-white/20 hover:bg-white/40 backdrop-blur-sm text-white p-3 rounded-full z-10 transition-colors"
          aria-label="Scroll Left"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" /></svg>
        </button>
        <button 
          onClick={() => scroll('right')}
          className="absolute top-1/2 right-0 sm:right-4 lg:right-12 transform -translate-y-1/2 bg-white/20 hover:bg-white/40 backdrop-blur-sm text-white p-3 rounded-full z-10 transition-colors"
          aria-label="Scroll Right"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>
        </button>
      </div>
    </section>
  );
}