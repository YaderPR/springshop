import { useEffect, useState } from "react";
import {
  getProducts,
  deleteProduct,
  getCategories,
  getApparels,
  deleteApparel,
} from "../../services/productService";
import type { Product, Category } from "../../types/Product";

interface Props {
  refreshSignal: boolean;
  onEdit: (product: Product) => void; // ?? nueva prop
}

export default function ProductList({ refreshSignal, onEdit }: Props) {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
       const [
        productsData,
        apparelsData,
        categoriesData, 
      ] = await Promise.all([
        getProducts(),
        getApparels(),
        getCategories(), 
      ]);

      const apparelIds = new Set(apparelsData.map((a) => a.id));
      const genericProductsData = productsData.filter(product => !apparelIds.has(product.id));
      const allProducts = [...genericProductsData, ...apparelsData];

      setProducts(allProducts);
      setCategories(categoriesData);

    } catch (error) {
      console.error("Error al cargar datos:", error);
    }
  };

  fetchData();
}, [refreshSignal]);

  const handleDelete = async (id: number) => {
    if (!confirm("¿Seguro que quieres eliminar este producto?")) return;
    try {
      await deleteApparel(id);
    } catch {
      try {
        await deleteProduct(id);
      } catch (err) {
        console.error("Falló al eliminar en ambos endpoints", err);
      }
    }
    setProducts(products.filter((p) => p.id !== id));
  };

  const getCategoryName = (id: number | undefined) => {
    if (id === undefined) return "Sin categoría";
    return categories.find((c) => c.id === id)?.name || "Sin categoría";
  };

  return (
    <div className="relative overflow-x-auto shadow-md sm:rounded-lg mb-10 mt-10 ml-10 mr-10">
      <table className="w-full text-sm text-left text-secondary dark:text-secondary">
        <thead className="text-xs uppercase dark:bg-secondary dark:text-primary">
          <tr>
            <th className="px-6 py-3">ID</th>
            <th className="px-6 py-3">Nombre</th>
            <th className="px-6 py-3">Precio</th>
            <th className="px-6 py-3">Stock</th>
            <th className="px-6 py-3">Marca</th>
            <th className="px-6 py-3">Color</th>
            <th className="px-6 py-3">Tamaño</th>
            <th className="px-6 py-3">Categoría</th>
            <th className="px-6 py-3">Imagen</th>
            <th className="px-6 py-3">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr
              key={p.id}
              className="bg-white border-b dark:bg-primary dark:border-gray-700 hover:bg-gray-800 dark:hover:bg-gray"
            >
              <td className="p-2">{p.id}</td>
              <td className="p-2">{p.name}</td>
              <td className="p-2">${p.price}</td>
              <td className="p-2">{p.stock}</td>
              <td className="p-2">{p.brand || "N/A"}</td>
              <td className="p-2">{p.color || "N/A"}</td>
              <td className="p-2">{p.size || "N/A"}</td>
              <td className="p-2">{getCategoryName(p.categoryId)}</td>
              <td className="p-2">
                {p.imageUrl ? (
                  <img
                    src={p.imageUrl}
                    alt={p.name}
                    className="w-16 h-16 object-cover rounded"
                  />
                ) : (
                  <span>No hay imagen</span>
                )}
              </td>
              <td className="p-2 flex flex-col gap-2">
                <button
                  onClick={() => handleDelete(p.id!)}
                  className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                >
                  Eliminar
                </button>
                <button
                  onClick={() => onEdit(p)} // ?? aquí llamamos la función
                  className="bg-green-700 text-white px-3 py-1 rounded hover:bg-green-600"
                >
                  Editar
                </button>
              </td>
            </tr>
          ))}

          {products.length === 0 && (
            <tr>
              <td colSpan={10} className="text-center p-4 text-gray-500">
                No hay productos aún
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
