import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { 
  getApparels, 
  getSupplements, 
  getWorkoutAccessories 
} from '../../services/product/ProductService'; //
import type { AnyProduct } from '../../types/Product';
import ProductCard from '../../components/shop/product/ProductCard'; //
import { Loader2, AlertTriangle, PackageSearch } from 'lucide-react';

// Mapeamos los 'tipos' de la URL a sus títulos y funciones de carga de datos
const categoryLoaders = {
  apparel: {
    title: 'Ropa',
    loader: getApparels,
  },
  supplements: {
    title: 'Suplementos',
    loader: getSupplements,
  },
  accessories: {
    title: 'Accesorios',
    loader: getWorkoutAccessories,
  },
};

// Creamos un tipo para asegurar que la URL sea válida
type CategoryType = keyof typeof categoryLoaders;

export default function ProductGridPage() {
  // useParams() lee el ':type' de la URL (ej. 'apparel')
  const { type } = useParams<{ type: string }>(); 
  
  const [products, setProducts] = useState<AnyProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [title, setTitle] = useState('Tienda');

  useEffect(() => {
    // 1. Validar el 'type' de la URL
    if (!type || !(type in categoryLoaders)) {
      setError('Categoría no válida.');
      setLoading(false);
      return;
    }

    const categoryType = type as CategoryType;
    const { title, loader } = categoryLoaders[categoryType];
    
    setTitle(title); // Pone el título (ej. "Ropa")
    setLoading(true);
    setError(null);

    // 2. Llama a la función de carga correcta (ej. getApparels)
    loader()
      .then(data => {
        setProducts(data);
      })
      .catch(err => {
        console.error(`Error al cargar ${type}:`, err);
        setError(err.message || "Error al cargar productos.");
      })
      .finally(() => {
        setLoading(false);
      });

  }, [type]); // Este efecto se ejecuta cada vez que el 'type' en la URL cambia

  // --- Renderizado ---
  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-96 bg-red-900/20 border border-red-700 p-6 rounded-lg">
        <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
        <div>
          <h2 className="text-xl font-bold text-red-400">Error</h2>
          <p className="text-red-300">{error}</p>
        </div>
      </div>
    );
  }

  return (
    
    <div className="container mx-auto p-4 md:p-32">
      <h1 className="text-3xl font-bold text-secondary mb-6">{title}</h1>
      
      {products.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {products.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center h-64 text-gray-500">
          <PackageSearch size={48} />
          <p className="mt-4 text-lg">No se encontraron productos en esta categoría.</p>
        </div>
      )}
    </div>
  );
}