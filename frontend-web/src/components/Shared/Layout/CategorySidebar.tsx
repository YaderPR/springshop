import React from 'react';
import { LayoutGrid, Flame, Percent } from 'lucide-react'; 

export default function CategorySidebar() {
  const categories = [
    { name: 'Todos los productos', icon: LayoutGrid },
    { name: 'Lo más vendido', icon: Flame },
    { name: 'En descuento', icon: Percent },
  ];

  return (
    <aside className="space-y-6">
      <h2 className="text-4xl font-bold text-secondary">Categorias</h2>
      <ul className="space-y-3">
        {categories.map((category) => (
          <li key={category.name}>
            <a
              href="#"
              className="
                flex items-center gap-4 
                text-white text-lg font-semibold 
                hover:text-secondary
                transition-colors duration-200
              "
            >
              <category.icon className="w-6 h-6" />
              <span>{category.name}</span>
            </a>
          </li>
        ))}
      </ul>
    </aside>
  );
}