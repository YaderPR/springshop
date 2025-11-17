import React from 'react';
import { LayoutGrid, Shirt, Sparkles, Dumbbell } from 'lucide-react';
// Importamos el tipo desde HomePage
import type { CategoryFilter } from '../../../pages/shop/HomePage';

interface Props {
  selectedCategory: CategoryFilter;
  onSelectCategory: (category: CategoryFilter) => void;
}

// Helper para los estilos de los botones
const getLinkClass = (isActive: boolean) => {
  return `flex items-center gap-3 px-4 py-3 rounded-lg text-lg transition-colors w-full text-left
    ${
      isActive
        ? 'bg-secondary text-primary font-bold' // Estilo activo
        : 'text-gray-300 hover:bg-gray-800 hover:text-white' // Estilo inactivo
    }`;
};

export default function CategorySidebar({ selectedCategory, onSelectCategory }: Props) {
  return (
    <aside className="w-full md:w-64 flex-shrink-0">
      <h2 className="text-3xl font-bold text-secondary mb-6">
        Categorías
      </h2>
      <nav className="flex flex-col gap-2">
        {/* Ya no son NavLinks, son botones que llaman a la función del padre */}
        <button 
          onClick={() => onSelectCategory('all')} 
          className={getLinkClass(selectedCategory === 'all')}
        >
          <LayoutGrid size={22} />
          Todos los productos
        </button>
        <button 
          onClick={() => onSelectCategory('apparel')}
          className={getLinkClass(selectedCategory === 'apparel')}
        >
          <Shirt size={22} />
          Ropa (Apparel)
        </button>
        <button 
          onClick={() => onSelectCategory('supplements')}
          className={getLinkClass(selectedCategory === 'supplements')}
        >
          <Sparkles size={22} />
          Suplementos
        </button>
        <button 
          onClick={() => onSelectCategory('accessories')}
          className={getLinkClass(selectedCategory === 'accessories')}
        >
          <Dumbbell size={22} />
          Accesorios
        </button>
      </nav>
    </aside>
  );
}