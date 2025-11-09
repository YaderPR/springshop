
import { useState } from "react";

import ProductForm from "../../components/Admin/ProductForm";
import ProductList from "../../components/Admin/ProductList";
import type { AnyProduct } from "../../types/Product";

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
    <div className="  ">
      <h1 className="text-3xl font-bold text-secondary mb-4">Administrar de productos</h1>

      <div className="">
          <ProductForm onCreated={handleRefresh} productToEdit={productToEdit} onCancelEdit={handleCancelEdit} />
          <ProductList refreshSignal={refresh} onEdit={handleEdit} />
      </div>
      
    </div>
  );
}
