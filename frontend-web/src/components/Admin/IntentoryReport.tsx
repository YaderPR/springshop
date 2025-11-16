import React, { useState, useEffect } from 'react';
import { getApparels, getSupplements, getWorkoutAccessories } from '../../services/product/ProductService';
import type { AnyProduct } from '../../types/Product';
import { Loader2, AlertTriangle, PackageSearch } from 'lucide-react';
import { useKeycloak } from '@react-keycloak/web'; 

const LOW_STOCK_THRESHOLD = 100;

export default function InventoryReport() {
  const { keycloak, initialized } = useKeycloak();
  const [lowStockProducts, setLowStockProducts] = useState<AnyProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    
    if (!initialized) return;

    // NOTA: Asumimos que los endpoints GET de productos son públicos
    // Si fallan por 401 (No Autorizado), tendremos que pasar el keycloak.token
    // a getApparels(token), getSupplements(token), etc.
    
    const loadInventory = async () => {
      setLoading(true);
      setError(null);
      try {
        // 1. Cargar todos los tipos de productos en paralelo
        const [
          apparelsData,
          supplementsData,
          workoutAccessoriesData,
        ] = await Promise.all([
          getApparels(),
          getSupplements(),
          getWorkoutAccessories(),
        ]);

        // 2. Combinarlos en una sola lista
        const allProducts: AnyProduct[] = [
          ...apparelsData, 
          ...supplementsData, 
          ...workoutAccessoriesData
        ];

        // 3. Filtrar por bajo stock y ordenar
        const filtered = allProducts
          .filter(p => p.stock <= LOW_STOCK_THRESHOLD)
          .sort((a, b) => a.stock - b.stock); // Mostrar los más bajos primero

        setLowStockProducts(filtered);

      } catch (err: any) {
        setError(err.message || "Error al cargar el inventario.");
      } finally {
        setLoading(false);
      }
    };

    loadInventory();
  }, [initialized]); // Dependemos solo de 'initialized'

  // --- Renderizado ---

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
        <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
        <div>
          <h2 className="text-xl font-bold text-red-400">Error al Cargar Reporte</h2>
          <p className="text-red-300">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-primary border border-gray-700 rounded-lg shadow-md">
      {/* Encabezado de la tabla */}
      <table className="w-full text-sm text-left text-gray-300">
        <thead className="text-xs uppercase bg-gray-700 text-gray-400">
          <tr>
            <th scope="col" className="px-6 py-3">ID</th>
            <th scope="col" className="px-6 py-3">Producto</th>
            <th scope="col" className="px-6 py-3">Stock Restante</th>
          </tr>
        </thead>
        <tbody>
          {lowStockProducts.length > 0 ? (
            lowStockProducts.map((product) => (
              <tr key={product.id} className="border-b border-gray-700 hover:bg-gray-800">
                <td className="px-6 py-4 font-medium">{product.id}</td>
                <td className="px-6 py-4 text-white">{product.name}</td>
                <td className="px-6 py-4">
                  <span className="text-red-500 font-bold text-lg">
                    {product.stock}
                  </span>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={3} className="text-center p-8 text-gray-500">
                <div className="flex flex-col items-center gap-2">
                  <PackageSearch size={48} />
                  <span className="text-lg">¡Buen trabajo! No hay productos con bajo stock.</span>
                </div>
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}