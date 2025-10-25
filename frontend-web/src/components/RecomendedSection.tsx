import React, { useRef } from 'react'; 
import ProductCard from './ProductCard';

const recommendedProducts = [
  { id: 5, name: 'Mancuernas Hexagonales 5KG', price: 35.50, rating: 4.9, image: 'https://placehold.co/400x400/FFFFFF/000000?text=Mancuernas' },
  { id: 6, name: 'Banda de Resistencia Alta', price: 9.99, rating: 4.7, image: 'https://placehold.co/400x400/FFFFFF/000000?text=Banda' },
  { id: 7, name: 'Rueda Abdominal Pro', price: 18.00, rating: 4.8, image: 'https://placehold.co/400x400/FFFFFF/000000?text=Rueda' },
  { id: 8, name: 'Cuerda para Saltar de Velocidad', price: 14.90, rating: 5.0, image: 'https://placehold.co/400x400/FFFFFF/000000?text=Cuerda' },
  { id: 9, name: 'Foam Roller para Masaje', price: 22.00, rating: 4.6, image: 'https://placehold.co/400x400/FFFFFF/000000?text=Roller' },
];

export default function RecommendedSection() {
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  const scroll = (direction: 'left' | 'right') => {
    if (scrollContainerRef.current) {
      const scrollAmount = direction === 'left' ? -352 : 350; 
      scrollContainerRef.current.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  };

  return (
    <section className="bg-primary px-4 py-10 relative">
     
      <div className="container mx-auto px-2 sm:px-12 lg:px-20 relative">
        <h2 className="text-3xl font-bold text-white mb-8">Recomendado para ti</h2>
        
        {/* Contenedor con scroll horizontal */}
        <div
          ref={scrollContainerRef} 
          className="flex overflow-x-auto pb-4 no-scrollbar" 
        >
          {recommendedProducts.map((product) => (
            <div key={product.id} className="flex-none w-80 -mr-6 ">
              <ProductCard
                name={product.name}
                price={product.price}
                rating={product.rating}
                imageUrl={product.image}
              />
            </div>
          ))}
        </div>

        {/* Botones de Navegación */}
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