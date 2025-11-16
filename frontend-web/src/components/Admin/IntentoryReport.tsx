import React, { useState, useEffect } from 'react';
import { getApparels, getSupplements, getWorkoutAccessories } from '../../services/product/ProductService';
import type { AnyProduct } from '../../types/Product';
import { Loader2, AlertTriangle, PackageSearch } from 'lucide-react';
import { useKeycloak } from '@react-keycloak/web'; 
import ExportButtons from '../Shared/Common/ExportButtons'; 
// import { div } from 'framer-motion/client'; // <-- Esta línea no es necesaria y puede causar errores

const LOW_STOCK_THRESHOLD = 100;

const reportColumns = [
  { header: 'ID', accessor: 'id'},
  { header: 'Producto', accessor: 'name'},
  { header: 'Stock Restante', accessor: 'stock'}
];

export default function InventoryReport() {
  const { keycloak, initialized } = useKeycloak();
  const [lowStockProducts, setLowStockProducts] = useState<AnyProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // ... (Tu lógica de useEffect para cargar datos está perfecta)
    if (!initialized) return;
    
    const loadInventory = async () => {
      setLoading(true);
      setError(null);
      try {
        const [
          apparelsData,
          supplementsData,
          workoutAccessoriesData,
        ] = await Promise.all([
          getApparels(),
          getSupplements(),
          getWorkoutAccessories(),
        ]);

        const allProducts: AnyProduct[] = [
          ...apparelsData, 
          ...supplementsData, 
          ...workoutAccessoriesData
        ];

        const filtered = allProducts
          .filter(p => p.stock <= LOW_STOCK_THRESHOLD)
          .sort((a, b) => a.stock - b.stock); 

        setLowStockProducts(filtered);

      } catch (err: any) {
        setError(err.message || "Error al cargar el inventario.");
      } finally {
        setLoading(false);
      }
    };

    loadInventory();
  }, [initialized]);

  // --- Renderizado de Carga ---
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  // --- ARREGLO 1: Renderizado de Error ---
  // Ahora solo devuelve UN elemento div
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

  // --- ARREGLO 2: Renderizado de Éxito ---
  // Envolvemos todo en un div padre (o un Fragment <>)
  // y añadimos los ExportButtons AQUÍ, en la vista de éxito.
  return (
    <div className="space-y-4"> {/* <-- Div padre añadido */}
      
      {/* Botones de Exportación */}
      <div className="flex justify-end">
        <ExportButtons 
          data={lowStockProducts}
          filename='reporte_inventario_bajo'
          columns={reportColumns}
        />
      </div>

      {/* Tabla (tu código original) */}
      <div className="bg-primary border border-gray-700 rounded-lg shadow-md">
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
    </div>
  );
}