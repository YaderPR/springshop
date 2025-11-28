import { useState } from 'react';
import CategoryManager from '../../components/Admin/CategoryManager';
import * as ProductService from '../../services/product/ProductService'; 
type CategoryTab = 'apparel' | 'workout' | 'generic';

export default function AdminCategories() {
  const [activeTab, setActiveTab] = useState<CategoryTab>('apparel');

  // Helper para las clases de las pestañas
  const getTabClass = (tabName: CategoryTab) => {
    return `py-2 px-4 font-semibold rounded-t-lg transition-colors
      ${
        activeTab === tabName
          ? 'bg-secondary text-primary'
          : 'text-gray-400 hover:text-secondary'
      }`;
  };

  return (
    <div>
      <h1 className="text-3xl font-bold text-secondary mb-6">
        Administrar Subcategorías
      </h1>

      {/* --- 1. Contenedor de Pestañas --- */}
      <div className="flex border-b border-gray-700 mb-6">
        <button
          onClick={() => setActiveTab('apparel')}
          className={getTabClass('apparel')}
        >
          Ropa (Apparel)
        </button>
        <button
          onClick={() => setActiveTab('workout')}
          className={getTabClass('workout')}
        >
          Accesorios (Workout)
        </button>
        
      </div>

      {/* --- 2. Contenido de Pestañas (Renderizado Condicional) --- */}
      <div className="p-4 bg-primary/30 rounded-lg text-secondary/70 ">
        {activeTab === 'apparel' && (
          <CategoryManager
            title="Subcategorías de Ropa"
            fetchCategories={ProductService.getApparelCategories}
            createCategory={ProductService.createApparelCategory}
            updateCategory={ProductService.updateApparelCategory}
            deleteCategory={ProductService.deleteApparelCategory}
          />
        )}
        
        {activeTab === 'workout' && (
          <CategoryManager
            title="Subcategorías para Accesorios de Gimnasio"
            fetchCategories={ProductService.getWorkoutAccessoryCategories}
            createCategory={ProductService.createWorkoutAccessoryCategory}
            updateCategory={ProductService.updateWorkoutAccessoryCategory}
            deleteCategory={ProductService.deleteWorkoutAccessoryCategory}
          />
        )}

        
      </div>
    </div>
  );
}