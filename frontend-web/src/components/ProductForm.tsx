import React, { useState, useEffect } from "react";

interface Category {
  id: number;
  name: string;
}

interface ProductFormData {
  name: string;
  description: string;
  price: number;
  stock: number;
  color?: string;
  brand?: string;
  size?: string;
  categoryId?: number;
  imageFile?: File | null;
}

export default function ProductForm() {
  const [formData, setFormData] = useState<ProductFormData>({
    name: "",
    description: "",
    price: 0,
    stock: 0,
    color: "",
    brand: "",
    size: "",
    categoryId: undefined,
    imageFile: null,
  });

  const [categories, setCategories] = useState<Category[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [loadingCategories, setLoadingCategories] = useState(true);

  // URLs base de los microservicios
  const STORAGE_URL = "http://localhost:8083/files/upload";
  const PRODUCT_URL = "http://localhost:8081/api/products/apparel"; // endpoint de productos
  const CATEGORY_URL = "http://localhost:8081/api/products/categories"; // ajusta según tu backend

  // ?? Obtener categorías desde la API
 useEffect(() => {
  const fetchCategories = async () => {
    try {
      const res = await fetch(CATEGORY_URL);
      if (!res.ok) throw new Error("Error al cargar categorías");
      const data = await res.json();

      console.log("? Categorías recibidas desde backend:", data);

      // Comprueba si viene como arreglo o dentro de 'content' u otra clave
      if (Array.isArray(data)) {
        setCategories(data);
      } else if (data.content && Array.isArray(data.content)) {
        setCategories(data.content);
      } else {
        console.warn("?? Estructura inesperada de categorías:", data);
        setCategories([]);
      }
    } catch (err) {
      console.error("? Error cargando categorías:", err);
    } finally {
      setLoadingCategories(false);
    }
  };

  fetchCategories();
}, []);

  // ?? Manejo de cambios en inputs
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]:
        name === "price" || name === "stock" || name === "categoryId"
          ? Number(value)
          : value,
    }));
  };

  // ?? Manejo del archivo
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) => ({ ...prev, imageFile: file }));
  };

  // ?? Enviar formulario
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setMessage("");

    try {
      let imageUrl = "";

      // 1?? Subir imagen al storage-service
      if (formData.imageFile) {
        const fileData = new FormData();
        fileData.append("file", formData.imageFile);

        const uploadResponse = await fetch(STORAGE_URL, {
          method: "POST",
          body: fileData,
        });

        if (!uploadResponse.ok) throw new Error("Error subiendo imagen");

        const uploadedFile = await uploadResponse.json();
        imageUrl = `http://localhost:8083/files/${uploadedFile.filename}`;
      }

      // 2?? Guardar producto en product-service
      const productPayload = {
        name: formData.name,
        description: formData.description,
        price: formData.price,
        stock: formData.stock,
        color: formData.color,
        brand: formData.brand,
        size: formData.size,
        imageUrl,
        category: formData.categoryId ? { id: formData.categoryId } : undefined,
      };

      const response = await fetch(PRODUCT_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(productPayload),
      });

      if (!response.ok) throw new Error("Error guardando producto");

      setMessage("? Producto guardado correctamente");
      setFormData({
        name: "",
        description: "",
        price: 0,
        stock: 0,
        color: "",
        brand: "",
        size: "",
        categoryId: undefined,
        imageFile: null,
      });
    } catch (error) {
      console.error(error);
      setMessage("? Error al guardar el producto");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-primary backdrop-blur-md p-8 rounded-2xl shadow-lg border border-gray-700 text-gray-100">
      <h2 className="text-2xl font-bold mb-6 text-secondary">
        Registrar nuevo producto
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

        <div className="grid grid-cols-3 gap-4">
          <input
            name="color"
            placeholder="Color"
            value={formData.color}
            onChange={handleChange}
            className="p-3 rounded bg-gray-800 border border-gray-600"
          />
          <input
            name="brand"
            placeholder="Marca"
            value={formData.brand}
            onChange={handleChange}
            className="p-3 rounded bg-gray-800 border border-gray-600"
          />
          <input
            name="size"
            placeholder="Tamaño"
            value={formData.size}
            onChange={handleChange}
            className="p-3 rounded bg-gray-800 border border-gray-600"
          />
        </div>

        {/* ?? Selector de categoría */}
        <div>
          <label className="block mb-2 text-sm text-gray-400">
            Categoría del producto
          </label>
          {loadingCategories ? (
            <p className="text-gray-400 text-sm">Cargando categorías...</p>
          ) : (
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
          )}
        </div>

        <div>
          <label className="block mb-2">Imagen del producto:</label>
          <input
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            className="block w-full text-sm text-gray-300 file:mr-4 file:py-2 file:px-4 
              file:rounded-full file:border-0 file:text-sm file:font-semibold 
              file:bg-secondary file:text-primary hover:file:bg-lime-400"
          />
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full bg-secondary text-primary font-bold py-3 rounded-full hover:bg-lime-400 transition-all"
        >
          {isSubmitting ? "Guardando..." : "Guardar producto"}
        </button>
      </form>

      {message && (
        <p className="mt-4 text-center text-lg font-medium">{message}</p>
      )}
    </div>
  );
}
