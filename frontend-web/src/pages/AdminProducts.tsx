import { useState } from "react";
import ProductForm from "../components/Products/ProductForm";
import ProductList from "../components/Products/ProductList";
import type { Product } from "../types/Product";

export default function AdminProducts() {
  const [refresh, setRefresh] = useState(false);
  const [productToEdit, setProductToEdit] = useState<Product | null>(null);

  const handleRefresh = () => setRefresh(!refresh);
  const handleEdit = (product: Product) => {
    setProductToEdit(product);
    window.scrollTo({ top: 0, behavior: "smooth" }); 
  };

  const handleCancelEdit = () => setProductToEdit(null);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Administrar Productos</h1>

      <ProductForm
        onCreated={handleRefresh}
        productToEdit={productToEdit}
        onCancelEdit={handleCancelEdit}
      />

      <ProductList
        refreshSignal={refresh}
        onEdit={handleEdit} // ?? pasamos esta función
      />
    </div>
  );
}
