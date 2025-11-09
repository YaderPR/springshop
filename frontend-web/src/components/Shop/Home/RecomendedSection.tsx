import React, { useRef, useState, useEffect } from 'react';
import ProductCard from '../../Shop/Product/ProductCard';
import type { Product } from "../../../types/Product";
import { getProducts, getApparels } from '../../../services/product/ProductService';

export default function RecommendedSection() {
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const [productsData, apparelsData] = await Promise.all([
          getProducts(),
          getApparels(),
        ]);

        const apparelIds = new Set(apparelsData.map((a) => a.id));
        const genericProductsData = productsData.filter(product => !apparelIds.has(product.id));
        const allProducts = [...genericProductsData, ...apparelsData];

        setProducts(allProducts.slice(0, 5)); 

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
            <p className="text-white">Cargando productos...</p>
          ) : (
            products.map((product) => (
              <div key={product.id} className="flex-none w-72"> 
                
                <ProductCard product={product} /> 

              </div>
            ))
          )}
        </div>

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