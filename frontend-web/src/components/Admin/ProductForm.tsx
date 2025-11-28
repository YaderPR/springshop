import React, { useState, useEffect } from "react";
import {
  createApparel,
  createSupplement,
  createWorkoutAccessory,
  updateApparel,
  updateSupplement,
  updateWorkoutAccessory,
  getCategories,
  getApparelCategories,
  getWorkoutAccessoryCategories,
} from "../../services/product/ProductService";
import { uploadImage } from "../../services/storage/StorageService";
import type { 
  Category, 
  ApparelCategory, 
  workoutAccessoryCategory, 
  AnyProduct,
  Apparel,
  Supplement,
  WorkoutAccessory
} from "../../types/Product";

type ProductType = "APPAREL" | "SUPPLEMENT" | "WORKOUT_ACCESSORY";

interface Props {
  onCreated: () => void;
  productToEdit?: AnyProduct | null;
  onCancelEdit: () => void;
}

// Interfaz explícita para el formulario específico
// Esto soluciona los errores de tipos "undefined vs number"
interface SpecificFormData {
  // Apparel
  size: string;
  color: string;
  brand: string;
  apparelCategoryId?: number;
  // Supplement
  flavor: string;
  ingredients: string;
  usageInstructions: string;
  warnings: string;
  categoryId?: number; // Generic ID usado en supplements
  // Accessory
  material: string;
  dimensions: string;
  weight: number;
  workoutAccessoryCategoryId?: number;
}

const initialBaseState = {
  name: "",
  description: "",
  price: 0,
  stock: 0,
  imageFile: null as File | null,
  imageUrl: "",
};

const initialSpecificState: SpecificFormData = {
  size: "",
  color: "",
  brand: "",
  apparelCategoryId: undefined,
  flavor: "",
  ingredients: "",
  usageInstructions: "",
  warnings: "",
  categoryId: undefined,
  material: "",
  dimensions: "",
  weight: 0,
  workoutAccessoryCategoryId: undefined,
};

type CategoryLists = {
  generic: Category[];
  apparel: ApparelCategory[];
  workoutAccessory: workoutAccessoryCategory[];
};

export default function ProductForm({ onCreated, productToEdit, onCancelEdit }: Props) {
  const [productType, setProductType] = useState<ProductType>("APPAREL");
  const [baseFormData, setBaseFormData] = useState(initialBaseState);
  
  // SOLUCIÓN: Tipado explícito aquí
  const [specificFormData, setSpecificFormData] = useState<SpecificFormData>(initialSpecificState);
  
  const [categories, setCategories] = useState<CategoryLists>({
    generic: [],
    apparel: [],
    workoutAccessory: [],
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    Promise.all([
      getCategories(),
      getApparelCategories(),
      getWorkoutAccessoryCategories(),
    ]).then(([generic, apparel, workoutAccessory]) => {
      setCategories({ generic, apparel, workoutAccessory });
    }).catch(console.error);
  }, []);

  // --- LÓGICA DE EDICIÓN ---
  useEffect(() => {
    if (productToEdit) {
      // 1. Detectar tipo
      let type: ProductType = "APPAREL";
      // Chequeo de propiedades únicas para determinar el tipo
      if ('flavor' in productToEdit) {
        type = "SUPPLEMENT";
      } else if ('material' in productToEdit) {
        type = "WORKOUT_ACCESSORY";
      }
      setProductType(type);

      // 2. Rellenar base
      const { name, description, price, stock, imageUrl } = productToEdit;
      setBaseFormData({
        name,
        description,
        price,
        stock,
        imageUrl: imageUrl || "",
        imageFile: null,
      });

      // 3. Rellenar específico
      // IMPORTANTE: Creamos un nuevo objeto basado en initialSpecificState para limpiar campos de otros tipos
      const newSpecificData: SpecificFormData = { ...initialSpecificState };

      if (type === "APPAREL") {
        const p = productToEdit as Apparel;
        newSpecificData.size = p.size;
        newSpecificData.color = p.color;
        newSpecificData.brand = p.brand;
        newSpecificData.apparelCategoryId = p.apparelCategoryId;
      } else if (type === "SUPPLEMENT") {
        const p = productToEdit as Supplement;
        newSpecificData.brand = p.brand;
        newSpecificData.flavor = p.flavor;
        newSpecificData.size = p.size;
        newSpecificData.ingredients = p.ingredients;
        newSpecificData.usageInstructions = p.usageInstructions;
        newSpecificData.warnings = p.warnings;
        newSpecificData.categoryId = p.categoryId;
      } else if (type === "WORKOUT_ACCESSORY") {
        const p = productToEdit as WorkoutAccessory;
        newSpecificData.material = p.material;
        newSpecificData.dimensions = p.dimensions;
        newSpecificData.weight = p.weight;
        newSpecificData.color = p.color;
        newSpecificData.workoutAccessoryCategoryId = p.workoutAccessoryCategoryId;
      }

      setSpecificFormData(newSpecificData);

    } else {
      // Reset completo si no hay edición
      setBaseFormData(initialBaseState);
      setSpecificFormData(initialSpecificState);
      setProductType("APPAREL");
    }
  }, [productToEdit]);


  const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setProductType(e.target.value as ProductType);
    // Opcional: limpiar specificData al cambiar de tipo manualmente
    setSpecificFormData(initialSpecificState);
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    // Lista de campos que deben ser numéricos
    const numericFields = ["price", "stock", "weight", "categoryId", "apparelCategoryId", "workoutAccessoryCategoryId"];

    const processedValue = numericFields.includes(name) 
      ? (value === "" ? undefined : Number(value)) // Usar undefined si está vacío para evitar "0" visual
      : value;

    // Actualización segura de estado
    if (name in baseFormData) {
      setBaseFormData((prev) => ({ ...prev, [name]: processedValue }));
    } else {
      // TypeScript ya sabe que name es keyof SpecificFormData (o string compatible)
      setSpecificFormData((prev) => ({ ...prev, [name]: processedValue }));
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setBaseFormData((prev) => ({ ...prev, imageFile: file }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setMessage("");

    try {
      let imageUrl = baseFormData.imageUrl || "";
      if (baseFormData.imageFile) {
        imageUrl = await uploadImage(baseFormData.imageFile);
      }

      const basePayload = {
        name: baseFormData.name,
        description: baseFormData.description,
        price: baseFormData.price,
        stock: baseFormData.stock,
        imageUrl,
      };

      let createFunction: (data: any) => Promise<any>;
      let updateFunction: (id: number, data: any) => Promise<any>;
      let payload: any = {};

      switch (productType) {
        case "APPAREL":
          payload = {
            ...basePayload,
            size: specificFormData.size,
            color: specificFormData.color,
            brand: specificFormData.brand,
            apparelCategoryId: specificFormData.apparelCategoryId,
          };
          createFunction = createApparel;
          updateFunction = updateApparel;
          break;
        
        case "SUPPLEMENT":
          payload = {
            ...basePayload,
            brand: specificFormData.brand,
            flavor: specificFormData.flavor,
            size: specificFormData.size,
            ingredients: specificFormData.ingredients,
            usageInstructions: specificFormData.usageInstructions,
            warnings: specificFormData.warnings,
            categoryId: specificFormData.categoryId,
          };
          createFunction = createSupplement;
          updateFunction = updateSupplement;
          break;

        case "WORKOUT_ACCESSORY":
          payload = {
            ...basePayload,
            material: specificFormData.material,
            dimensions: specificFormData.dimensions,
            weight: specificFormData.weight,
            color: specificFormData.color,
            workoutAccessoryCategoryId: specificFormData.workoutAccessoryCategoryId,
          };
          createFunction = createWorkoutAccessory;
          updateFunction = updateWorkoutAccessory;
          break;
      }

      if (productToEdit) {
        await updateFunction(productToEdit.id, payload);
        setMessage("Producto actualizado correctamente");
      } else {
        await createFunction(payload);
        setMessage("Producto creado correctamente");
        // Reset solo si es creación
        setBaseFormData(initialBaseState);
        setSpecificFormData(initialSpecificState);
      }

      onCreated();
      if(productToEdit) onCancelEdit();
      
    } catch (error) {
      console.error(error);
      setMessage(`Error: ${error instanceof Error ? error.message : "Error desconocido"}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderSpecificFields = () => {
    switch (productType) {
      case "APPAREL":
        return (
          <>
            <div className="grid grid-cols-3 gap-4">
              <input name="color" placeholder="Color" value={specificFormData.color} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
              <input name="brand" placeholder="Marca" value={specificFormData.brand} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
              <input name="size" placeholder="Tamaño (S, M, L)" value={specificFormData.size} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
            </div>
            <select name="apparelCategoryId" value={specificFormData.apparelCategoryId ?? ""} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600">
              <option value="">Seleccione una categoría de Ropa</option>
              {categories.apparel.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </>
        );
      case "SUPPLEMENT":
        return (
          <>
            <div className="grid grid-cols-3 gap-4">
              <input name="brand" placeholder="Marca" value={specificFormData.brand} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
              <input name="flavor" placeholder="Sabor" value={specificFormData.flavor} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
              <input name="size" placeholder="Tamaño (ej. 500g)" value={specificFormData.size} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
            </div>

            <div className="grid grid-cols-3 gap-4">
              <textarea name="ingredients" placeholder="Ingredientes" value={specificFormData.ingredients} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" required />
              <textarea name="usageInstructions" placeholder="Instrucciones de uso" value={specificFormData.usageInstructions} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" required />
              <textarea name="warnings" placeholder="Advertencias" value={specificFormData.warnings} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" />
            </div>
            
            <select name="categoryId" value={specificFormData.categoryId ?? ""} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600">
              <option value="">Seleccione una categoría Genérica</option>
              {categories.generic.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </>
        );
      case "WORKOUT_ACCESSORY":
        return (
          <>
            <div className="grid grid-cols-3 gap-4">
              <input name="material" placeholder="Material" value={specificFormData.material} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
              <input name="dimensions" placeholder="Dimensiones" value={specificFormData.dimensions} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
              <input name="weight" type="number" step="0.01" placeholder="Peso (kg)" value={specificFormData.weight || ""} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
            </div>
            <input name="color" placeholder="Color" value={specificFormData.color} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
            <select name="workoutAccessoryCategoryId" value={specificFormData.workoutAccessoryCategoryId ?? ""} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600">
              <option value="">Seleccione una categoría de Accesorio</option>
              {categories.workoutAccessory.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <div className="items-center w-full bg-gray-950/40 backdrop-blur-md p-8 rounded-2xl shadow-lg border border-secondary/50 text-gray-100">
      <h2 className="text-xl text-center font-bold mb-2 text-secondary">
        {productToEdit ? "Editar producto" : "Registrar nuevo producto"}
      </h2>
      <div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <select
            name="productType"
            value={productType}
            onChange={handleTypeChange}
            className="w-full p-3 rounded bg-gray-800 border border-gray-600 font-bold disabled:opacity-50 disabled:cursor-not-allowed"
            required
            disabled={!!productToEdit} // Deshabilitar cambio de tipo al editar
          >
            <option value="APPAREL">Categoría: Ropa</option>
            <option value="SUPPLEMENT">Categoría: Suplemento</option>
            <option value="WORKOUT_ACCESSORY">Categoría: Accesorio</option>
          </select>

          {/* --- CAMPOS COMUNES --- */}
          <div className="grid grid-cols-2 gap-4">
            <input name="name" placeholder="Nombre del producto" value={baseFormData.name} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" required />
            <textarea name="description" placeholder="Descripción" value={baseFormData.description} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <input name="price" type="number" step="0.01" placeholder="Precio" value={baseFormData.price || ""} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
            <input name="stock" type="number" step="1" placeholder="Stock" value={baseFormData.stock || ""} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
          </div>

          {/* --- CAMPOS ESPECÍFICOS --- */}
          {renderSpecificFields()}

          {/* --- IMAGEN Y BOTONES --- */}
          <input type="file" name="imageFile" accept="image/*" onChange={handleFileChange} className="block w-full text-sm text-gray-300 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-secondary file:text-primary hover:file:bg-lime-400" />
          
          {baseFormData.imageUrl && (
            <div className="text-center">
                <p className="text-xs text-gray-400 mb-1">Imagen Actual:</p>
                <img src={baseFormData.imageUrl} alt="Vista previa" className="w-32 h-32 object-cover rounded mx-auto border border-gray-600" />
            </div>
          )}

          <div className="flex gap-4 mt-4">
            <button type="submit" disabled={isSubmitting} className="flex-1 bg-secondary text-primary font-bold py-3 rounded-full hover:bg-lime-400 transition-all disabled:bg-gray-500">
              {isSubmitting ? "Guardando..." : (productToEdit ? "Actualizar producto" : "Guardar producto")}
            </button>
            {productToEdit && <button type="button" onClick={onCancelEdit} className="flex-1 bg-gray-600 text-white py-3 rounded-full hover:bg-gray-500">Cancelar</button>}
          </div>
        </form>
      </div>

      {message && <p className="mt-4 text-center text-lg font-medium text-secondary">{message}</p>}
    </div>
  );
}