import React, { useState, useEffect } from 'react'; 
import CategorySidebar from "../../Shared/Layout/CategorySidebar";
import type { AnyProduct } from "../../../types/Product"; // <-- Usamos AnyProduct
import { 
  getProducts, // (Lo mantendremos para el caso 'all', aunque con un bug)
  getApparels, 
  getSupplements, 
  getWorkoutAccessories 
} from '../../../services/product/ProductService'; 
import ProductCard from './ProductCard'; 
// Importamos el tipo desde HomePage
import type { CategoryFilter } from '../../../pages/Shop/HomePage';

interface Props {
  selectedCategory: CategoryFilter;
  onSelectCategory: (category: CategoryFilter) => void;
}

export default function ProductsSection({ selectedCategory, onSelectCategory }: Props) {
  // Usamos AnyProduct para que acepte Apparel, Supplement, etc.
  const [products, setProducts] = useState<AnyProduct[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  
  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      
      try {
        let productsData: AnyProduct[] = [];

        // --- ¡NUEVA LÓGICA DE FILTRADO! ---
        if (selectedCategory === 'apparel') {
          productsData = await getApparels();
        } else if (selectedCategory === 'supplements') {
          productsData = await getSupplements();
        } else if (selectedCategory === 'accessories') {
          productsData = await getWorkoutAccessories();
        } else {
          // Caso 'all': Cargamos todo
          // (Tu lógica original para 'all' es compleja y filtra productos genéricos,
          // la reemplazaremos cargando todo en paralelo, que es lo que el usuario espera)
          const [apparels, supplements, accessories] = await Promise.all([
            getApparels(),
            getSupplements(),
            getWorkoutAccessories(),
          ]);
          productsData = [...apparels, ...supplements, ...accessories];
        }
        
        setProducts(productsData); 

      } catch (error) {
        console.error("Error al cargar productos:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [selectedCategory]); // <-- ¡El useEffect ahora depende de la prop!

  return (
    <section className=" py-16 px-6 sm:px-12 lg:px-20">
      
      <div className="flex flex-col md:flex-row justify-between items-center mb-12">
        {/* ... (Tu título de sección "Equípate...") ... */}
      </div>

      
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-12">
        
        <div className="lg:col-span-1 ">
          {/* Pasamos el estado y la función al sidebar */}
          <CategorySidebar 
            selectedCategory={selectedCategory}
            onSelectCategory={onSelectCategory}
          />
        </div>

        
        <div className="lg:col-span-3">
          
          {isLoading ? (
            <div className="text-center text-secondary">Cargando productos...</div>
          ) : (
            <div className="grid gap-8 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-4">
              
              {products.map((product) => (
                
                <ProductCard key={product.id} product={product} />

              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  );
}