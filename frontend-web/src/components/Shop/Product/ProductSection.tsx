import React, { useState, useEffect } from 'react'; 
import { Search } from "lucide-react";
import CategorySidebar from "../../Shared/Layout/CategorySidebar";
import type { Product } from "../../../types/Product";
import { getProducts, getApparels } from '../../../services/product/ProductService'; 
import ProductCard from './ProductCard'; 

export default function ProductsSection() {
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
        
        setProducts(allProducts); 

      } catch (error) {
        console.error("Error al cargar productos:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []); 

  return (
    <section className=" py-16 px-6 sm:px-12 lg:px-20">
      
      <div className="flex flex-col md:flex-row justify-between items-center mb-12">

        <div className="text-left space-y-2">
          <h2 className="text-3xl font-bold text-secondary">Equípate para tu entrenamiento</h2>
          <p className="text-gray-200">
            Encuentra los mejores accesorios y equipos de gimnasio, seleccionados para ti.
          </p>
        </div>
        
      </div>

      
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-12">
        
        <div className="lg:col-span-1 ">
          <CategorySidebar />
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