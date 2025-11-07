// src/pages/AdminProducts.tsx (Sin cambios, solo para contexto)
import { useState } from "react";
// ¡Usamos el nuevo formulario dinámico de la respuesta anterior!
import ProductForm from "../components/Products/ProductForm";
import ProductList from "../components/Products/ProductList";
import type { AnyProduct } from "../types/Product";

export default function AdminProducts() {
  const [refresh, setRefresh] = useState(false);
  const [productToEdit, setProductToEdit] = useState<AnyProduct | null>(null);

  const handleRefresh = () => setRefresh(!refresh);

  const handleEdit = (product: AnyProduct) => {
    setProductToEdit(product);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleCancelEdit = () => setProductToEdit(null);

  return (
    <div className="flex items-center ">
      <h1 className="text-2xl font-bold ">Administrar Productos</h1>
      <div className="shadow-accent">
        <ProductForm
          onCreated={handleRefresh}
          productToEdit={productToEdit}
          onCancelEdit={handleCancelEdit}
        />
      </div>

      <div>
        <ProductList refreshSignal={refresh} onEdit={handleEdit} />
      </div>
    </div>
  );
}
