// src/components/admin/ProductReport.tsx (NUEVO ARCHIVO)
import React, { useState, useEffect, useMemo } from 'react';
import {
  getApparels,
  getSupplements,
  getWorkoutAccessories,
  getCategories,
  getApparelCategories,
  getWorkoutAccessoryCategories,
} from '../../services/product/ProductService';
import type { AnyProduct, Category, Apparel, Supplement, WorkoutAccessory } from '../../types/Product';
import { Loader2, AlertTriangle, PackageSearch } from 'lucide-react';
import ExportButtons from '../Shared/Common/ExportButtons';

// Un tipo para nuestra lista unificada de categorías
interface CombinedCategory {
  id: string;   // ej. "apparel-1", "generic-2"
  name: string; // ej. "Ropa: Camisetas"
}

// Definición de columnas para el reporte (lo usaremos para filtrar y exportar)
const reportColumns = [
  { header: 'ID', accessor: 'id' },
  { header: 'Producto', accessor: 'name' },
  { header: 'Stock', accessor: 'stock' },
  { header: 'Precio', accessor: 'price' },
  // Añadiremos una columna 'Categoría' personalizada
];

export default function ProductReport() {
  const [allProducts, setAllProducts] = useState<AnyProduct[]>([]);
  const [allCategories, setAllCategories] = useState<CombinedCategory[]>([]);
  const [filteredProducts, setFilteredProducts] = useState<AnyProduct[]>([]);
  
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // --- 1. Carga de datos ---
  useEffect(() => {
    const loadAllData = async () => {
      setLoading(true);
      setError(null);
      try {
        // Cargamos todos los productos Y todas las categorías en paralelo
        const [
          apparels,
          supplements,
          accessories,
          genericCats,
          apparelCats,
          workoutCats,
        ] = await Promise.all([
          getApparels(),
          getSupplements(),
          getWorkoutAccessories(),
          getCategories(),
          getApparelCategories(),
          getWorkoutAccessoryCategories(),
        ]);

        // Combinamos todos los productos
        const products: AnyProduct[] = [...apparels, ...supplements, ...accessories];
        setAllProducts(products);
        setFilteredProducts(products); // Al inicio, mostramos todos

        // Combinamos todas las categorías en una sola lista para el dropdown
        const combined: CombinedCategory[] = [
          ...genericCats.map(c => ({ id: `generic-${c.id}`, name: `Suplemento: ${c.name}` })),
          ...apparelCats.map(c => ({ id: `apparel-${c.id}`, name: `Ropa: ${c.name}` })),
          ...workoutCats.map(c => ({ id: `workout-${c.id}`, name: `Accesorio: ${c.name}` })),
        ];
        setAllCategories(combined.sort((a, b) => a.name.localeCompare(b.name)));

      } catch (err: any) {
        setError(err.message || "Error al cargar los datos del reporte.");
      } finally {
        setLoading(false);
      }
    };

    loadAllData();
  }, []);

  // --- 2. Lógica de Filtrado ---
  // Este 'useEffect' se ejecuta cada vez que 'selectedCategory' cambia
  useEffect(() => {
    if (selectedCategory === 'all') {
      setFilteredProducts(allProducts);
      return;
    }

    // Parseamos el ID (ej. "apparel-1")
    const [type, idStr] = selectedCategory.split('-');
    const id = parseInt(idStr, 10);

    const filtered = allProducts.filter(p => {
      if (type === 'apparel' && 'apparelCategoryId' in p) {
        return (p as Apparel).apparelCategoryId === id;
      }
      if (type === 'workout' && 'workoutAccessoryCategoryId' in p) {
        return (p as WorkoutAccessory).workoutAccessoryCategoryId === id;
      }
      if (type === 'generic' && 'categoryId' in p) {
        return (p as Supplement).categoryId === id;
      }
      return false;
    });

    setFilteredProducts(filtered);
  }, [selectedCategory, allProducts]);

  // --- 3. Datos para la Exportación ---
  // Usamos 'useMemo' para preparar los datos de exportación solo cuando cambien
  const exportData = useMemo(() => {
    // Necesitamos añadir el nombre de la categoría a los datos
    return filteredProducts.map(p => {
      let categoryName = "N/A";
      if ('apparelCategoryId' in p) {
        categoryName = allCategories.find(c => c.id === `apparel-${p.apparelCategoryId}`)?.name || "Ropa s/n";
      } else if ('workoutAccessoryCategoryId' in p) {
        categoryName = allCategories.find(c => c.id === `workout-${p.workoutAccessoryCategoryId}`)?.name || "Accesorio s/n";
      } else if ('categoryId' in p) {
        categoryName = allCategories.find(c => c.id === `generic-${p.categoryId}`)?.name || "Suplemento s/n";
      }
      return {
        id: p.id,
        name: p.name,
        stock: p.stock,
        price: p.price,
        category: categoryName,
      };
    });
  }, [filteredProducts, allCategories]);


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
    <div className="space-y-4">
      
      {/* --- Controles del Reporte (Filtro y Exportación) --- */}
      <div className="flex flex-col md:flex-row justify-between items-center gap-4">
        {/* Filtro de Categoría */}
        <div className="w-full md:w-1/3">
          <label htmlFor="categoryFilter" className="block text-sm font-medium text-gray-400 mb-1">
            Filtrar por Categoría
          </label>
          <select
            id="categoryFilter"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="w-full p-3 rounded bg-gray-800 border border-gray-600 text-white"
          >
            <option value="all">-- Todas las Categorías --</option>
            {allCategories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>

        {/* Botones de Exportación */}
        <div className="self-end">
          <ExportButtons 
            data={exportData} // Exportamos los datos filtrados
            filename={`reporte_productos_${selectedCategory}`}
            columns={[...reportColumns, { header: 'Categoría', accessor: 'category' }]}
          />
        </div>
      </div>

      {/* --- Tabla de Resultados --- */}
      <div className="bg-primary border border-gray-700 rounded-lg shadow-md overflow-hidden">
        <table className="w-full text-sm text-left text-gray-300">
          <thead className="text-xs uppercase bg-gray-700 text-gray-400">
            <tr>
              <th className="px-6 py-3">ID</th>
              <th className="px-6 py-3">Producto</th>
              <th className="px-6 py-3">Stock</th>
              <th className="px-6 py-3">Precio</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.length > 0 ? (
              filteredProducts.map((product) => (
                <tr key={product.id} className="border-b border-gray-700 hover:bg-gray-800">
                  <td className="px-6 py-4 font-medium">{product.id}</td>
                  <td className="px-6 py-4 text-white">{product.name}</td>
                  <td className={`px-6 py-4 font-bold ${product.stock <= 10 ? 'text-red-500' : ''}`}>
                    {product.stock}
                  </td>
                  <td className="px-6 py-4 text-secondary">${product.price.toFixed(2)}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="text-center p-8 text-gray-500">
                  <div className="flex flex-col items-center gap-2">
                    <PackageSearch size={48} />
                    <span className="text-lg">No se encontraron productos para este filtro.</span>
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