import React, { useState, useEffect } from "react";
import {
  createApparel,
  updateApparel,
  updateProduct,
  getCategories,
} from "../../services/productService";
import { uploadImage } from "../../services/StorageService";
import type { Product, Category } from "../../types/Product";

interface Props {
  onCreated: () => void;
  productToEdit?: Product | null;
  onCancelEdit: () => void;
}

interface ProductFormData {
  id?: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  color: string;
  brand: string;
  size: string;
  categoryId?: number;
  imageFile: File | null;
  imageUrl?: string;
}

const initialState: ProductFormData = {
  name: "",
  description: "",
  price: 0,
  stock: 0,
  color: "",
  brand: "",
  size: "",
  categoryId: undefined,
  imageFile: null,
  imageUrl: "",
};

export default function ProductForm({ onCreated, productToEdit, onCancelEdit }: Props) {
  const [formData, setFormData] = useState<ProductFormData>(initialState);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState("");

  // ? Cargar categorías
  useEffect(() => {
    getCategories().then(setCategories).catch(console.error);
  }, []);

  // ? Rellenar el formulario si hay un producto para editar
  useEffect(() => {
    if (productToEdit) {
      setFormData({
        ...productToEdit,
        imageFile: null,
      });
    } else {
      setFormData(initialState);
    }
  }, [productToEdit]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]:
        ["price", "stock", "categoryId"].includes(name)
          ? Number(value)
          : value,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) => ({ ...prev, imageFile: file }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setMessage("");

    try {
      let imageUrl = formData.imageUrl || "";

      // Si hay nueva imagen, súbela
      if (formData.imageFile) {
        imageUrl = await uploadImage(formData.imageFile);
      }

      const payload: Product = {
        ...formData,
        imageUrl,
      };

      if (productToEdit) {
        // Modo edición
        await updateApparel(formData.id!, payload).catch(async () => {
          await updateProduct(formData.id!, payload);
        });
        setMessage("Producto actualizado correctamente ");
      } else {
        // Modo creación
        await createApparel(payload);
        setMessage("Producto creado correctamente ");
      }

      onCreated();
      onCancelEdit();
      setFormData(initialState);
    } catch (error) {
      console.error(error);
      setMessage("Error al guardar el producto");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-primary backdrop-blur-md p-8 rounded-2xl shadow-lg border border-gray-700 text-gray-100">
      <h2 className="text-2xl text-center font-bold mb-6 text-secondary">
        {productToEdit ? "Editar producto" : "Registrar nuevo producto"}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          name="name"
          placeholder="Nombre del producto"
          value={formData.name}
          onChange={handleChange}
          className="w-full p-3 rounded bg-gray-800 border border-gray-600"
          required
        />

        <textarea
          name="description"
          placeholder="Descripción"
          value={formData.description}
          onChange={handleChange}
          className="w-full p-3 rounded bg-gray-800 border border-gray-600"
        />

        <div className="grid grid-cols-2 gap-4">
          <input
            name="price"
            type="number"
            placeholder="Precio"
            value={formData.price}
            onChange={handleChange}
            className="p-3 rounded bg-gray-800 border border-gray-600"
            required
          />
          <input
            name="stock"
            type="number"
            placeholder="Stock"
            value={formData.stock}
            onChange={handleChange}
            className="p-3 rounded bg-gray-800 border border-gray-600"
            required
          />
        </div>

        {/* Color, marca, tamaño */}
        <div className="grid grid-cols-3 gap-4">
          <input name="color" placeholder="Color" value={formData.color} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
          <input name="brand" placeholder="Marca" value={formData.brand} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
          <input name="size" placeholder="Tamaño" value={formData.size} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
        </div>

        {/* Categoría */}
        <select
          name="categoryId"
          value={formData.categoryId || ""}
          onChange={handleChange}
          className="w-full p-3 rounded bg-gray-800 border border-gray-600"
          required
        >
          <option value="">Seleccione una categoría</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>

        {/* Imagen */}
        <input
          type="file"
          name="imageFile"
          accept="image/*"
          onChange={handleFileChange}
          className="block w-full text-sm text-gray-300 file:mr-4 file:py-2 file:px-4
            file:rounded-full file:border-0 file:text-sm file:font-semibold
            file:bg-secondary file:text-primary hover:file:bg-lime-400"
        />

        {formData.imageUrl && (
          <img
            src={formData.imageUrl}
            alt="Vista previa"
            className="w-32 h-32 object-cover rounded mx-auto"
          />
        )}

        <div className="flex gap-4">
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex-1 bg-secondary text-primary font-bold py-3 rounded-full hover:bg-lime-400 transition-all disabled:bg-gray-500"
          >
            {isSubmitting
              ? "Guardando..."
              : productToEdit
              ? "Actualizar producto"
              : "Guardar producto"}
          </button>

          {productToEdit && (
            <button
              type="button"
              onClick={onCancelEdit}
              className="flex-1 bg-gray-600 text-white py-3 rounded-full hover:bg-gray-500"
            >
              Cancelar
            </button>
          )}
        </div>
      </form>

      {message && (
        <p className="mt-4 text-center text-lg font-medium">{message}</p>
      )}
    </div>
  );
}
