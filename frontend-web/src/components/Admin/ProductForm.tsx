// src/components/Products/ProductForm.tsx (ACTUALIZADO CON LÓGICA DE EDICIÓN)
import React, { useState, useEffect } from "react";
import {
  // Creación
  createApparel,
  createSupplement,
  createWorkoutAccessory,
  // ¡NUEVO! Actualización
  updateApparel,
  updateSupplement,
  updateWorkoutAccessory,
  // Categorías
  getCategories,
  getApparelCategories,
  getWorkoutAccessoryCategories,
} from "../../services/product/ProductService";
import { uploadImage } from "../../services/storage/StorageService";
import type { 
  Category, 
  ApparelCategory, 
  WorkoutAccessoryCategory, 
  AnyProduct,
  Apparel,
  Supplement,
  WorkoutAccessory
} from "../../types/Product";

// Definimos los 3 Categorías de producto
type ProductType = "APPAREL" | "SUPPLEMENT" | "WORKOUT_ACCESSORY";

interface Props {
  onCreated: () => void;
  productToEdit?: AnyProduct | null;
  onCancelEdit: () => void;
}

// Estado inicial para los campos comunes
const initialBaseState = {
  name: "",
  description: "",
  price: 0,
  stock: 0,
  imageFile: null,
  imageUrl: "",
};

// Estado para los campos específicos
const initialSpecificState = {
  // Apparel
  size: "",
  color: "",
  brand: "",
  apparelCategoryId: undefined,
  // Supplement
  flavor: "",
  ingredients: "",
  usageInstructions: "",
  warnings: "",
  categoryId: undefined, // Genérico
  // WorkoutAccessory
  material: "",
  dimensions: "",
  weight: 0,
  workoutAccessoryCategoryId: undefined,
};

type CategoryLists = {
  generic: Category[];
  apparel: ApparelCategory[];
  workoutAccessory: WorkoutAccessoryCategory[];
};

export default function ProductForm({ onCreated, productToEdit, onCancelEdit }: Props) {
  // --- ESTADOS ---
  const [productType, setProductType] = useState<ProductType>("APPAREL");
  const [baseFormData, setBaseFormData] = useState(initialBaseState);
  const [specificFormData, setSpecificFormData] = useState(initialSpecificState);
  
  const [categories, setCategories] = useState<CategoryLists>({
    generic: [],
    apparel: [],
    workoutAccessory: [],
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState("");

  // --- EFECTOS ---
  
  // Cargar TODAS las categorías al montar
  useEffect(() => {
    Promise.all([
      getCategories(),
      getApparelCategories(),
      getWorkoutAccessoryCategories(),
    ]).then(([generic, apparel, workoutAccessory]) => {
      setCategories({ generic, apparel, workoutAccessory });
    }).catch(console.error);
  }, []);

  // --- ¡LÓGICA DE EDICIÓN AÑADIDA AQUÍ! ---
  useEffect(() => {
    if (productToEdit) {
      // 1. Detectar el Categoría de producto
      let type: ProductType = "APPAREL"; // Default
      if ('flavor' in productToEdit) {
        type = "SUPPLEMENT";
      } else if ('material' in productToEdit) {
        type = "WORKOUT_ACCESSORY";
      }
      setProductType(type);

      // 2. Rellenar el formulario base
      const { name, description, price, stock, imageUrl } = productToEdit;
      setBaseFormData({
        name,
        description,
        price,
        stock,
        imageUrl: imageUrl || "",
        imageFile: null,
      });

      // 3. Rellenar el formulario específico
      setSpecificFormData(initialSpecificState); // Reseteamos primero
      
      if (type === "APPAREL") {
        const apparel = productToEdit as Apparel;
        setSpecificFormData(prev => ({
          ...prev,
          size: apparel.size,
          color: apparel.color,
          brand: apparel.brand,
          apparelCategoryId: apparel.apparelCategoryId,
        }));
      } else if (type === "SUPPLEMENT") {
        const supplement = productToEdit as Supplement;
        setSpecificFormData(prev => ({
          ...prev,
          brand: supplement.brand,
          flavor: supplement.flavor,
          size: supplement.size,
          ingredients: supplement.ingredients,
          usageInstructions: supplement.usageInstructions,
          warnings: supplement.warnings,
          categoryId: supplement.categoryId,
        }));
      } else if (type === "WORKOUT_ACCESSORY") {
        const accessory = productToEdit as WorkoutAccessory;
        setSpecificFormData(prev => ({
          ...prev,
          material: accessory.material,
          dimensions: accessory.dimensions,
          weight: accessory.weight,
          color: accessory.color,
          workoutAccessoryCategoryId: accessory.workoutAccessoryCategoryId,
        }));
      }
    } else {
      // Limpiamos los formularios si no hay producto para editar
      setBaseFormData(initialBaseState);
      setSpecificFormData(initialSpecificState);
      setProductType("APPAREL"); // Reset type
    }
  }, [productToEdit]);


  // --- MANEJADORES ---
  const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setProductType(e.target.value as ProductType);
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    const numericFields = ["price", "stock", "weight", "categoryId", "apparelCategoryId", "workoutAccessoryCategoryId"];

    // Permitir vaciar los campos numéricos sin que se conviertan en 0
    const processedValue = numericFields.includes(name) 
      ? (value === "" ? "" : Number(value)) 
      : value;

    if (name in baseFormData) {
      setBaseFormData((prev) => ({ ...prev, [name]: processedValue }));
    } else if (name in specificFormData) {
      setSpecificFormData((prev) => ({ ...prev, [name]: processedValue }));
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setBaseFormData((prev) => ({ ...prev, imageFile: file }));
  };

  // --- ¡LÓGICA DE EDICIÓN AÑADIDA AQUÍ! ---
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
        ...baseFormData,
        imageUrl,
      };
      delete (basePayload as any).imageFile; // No enviar el archivo

      let payload: any = {};
      let createFunction: (data: any) => Promise<any>;
      let updateFunction: (id: number, data: any) => Promise<any>;

      // Asignamos el payload y las funciones correctas según el Categoría
      switch (productType) {
        case "APPAREL":
          payload = {
            ...basePayload,
            size: specificFormData.size,
            color: specificFormData.color,
            brand: specificFormData.brand,
            apparelCategoryId: specificFormData.apparelCategoryId || null, // Enviar null si está vacío
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
            categoryId: specificFormData.categoryId || null, // Enviar null si está vacío
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
            workoutAccessoryCategoryId: specificFormData.workoutAccessoryCategoryId || null, // Enviar null
          };
          createFunction = createWorkoutAccessory;
          updateFunction = updateWorkoutAccessory;
          break;
      }

      // Ejecutamos la acción correcta
      if (productToEdit) {
        await updateFunction(productToEdit.id, payload);
        setMessage("Producto actualizado correctamente");
      } else {
        await createFunction(payload);
        setMessage("Producto creado correctamente");
      }

      onCreated();
      onCancelEdit();
      // Limpiamos los formularios
      setBaseFormData(initialBaseState);
      setSpecificFormData(initialSpecificState);
    } catch (error) {
      console.error(error);
      setMessage(`Error al guardar el producto: ${error instanceof Error ? error.message : "Error desconocido"}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  // --- RENDERIZADO DINÁMICO ---

  const renderSpecificFields = () => {
    switch (productType) {
      case "APPAREL":
        return (
          <>
            <div className="grid grid-cols-3 gap-4">
              <input name="color" placeholder="Color" value={specificFormData.color} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
              <input name="brand" placeholder="Marca" value={specificFormData.brand} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
              <input name="size" placeholder="Tamaño (S, M, L)" value={specificFormData.size} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
            </div>
            <select name="apparelCategoryId" value={specificFormData.apparelCategoryId || ""} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600">
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
              <input name="brand" placeholder="Marca" value={specificFormData.brand} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
              <input name="flavor" placeholder="Sabor" value={specificFormData.flavor} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
              <input name="size" placeholder="Tamaño (ej. 500g)" value={specificFormData.size} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
            </div>

            <div className="grid grid-cols-3 gap-4">
              <textarea name="ingredients" placeholder="Ingredientes" value={specificFormData.ingredients} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" />
              <textarea name="usageInstructions" placeholder="Instrucciones de uso" value={specificFormData.usageInstructions} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" />
              <textarea name="warnings" placeholder="Advertencias" value={specificFormData.warnings} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" />
            </div>
            
            {/* <select name="categoryId" value={specificFormData.categoryId || ""} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600">
              <option value="">Seleccione una categoría Genérica</option>
              {categories.generic.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select> */}
          </>
        );
      case "WORKOUT_ACCESSORY":
        return (
          <>
            <div className="grid grid-cols-3 gap-4">
              <input name="material" placeholder="Material" value={specificFormData.material} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
              <input name="dimensions" placeholder="Dimensiones" value={specificFormData.dimensions} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
              <input name="weight" type="number" placeholder="Peso (kg)" value={specificFormData.weight} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
            </div>
            <input name="color" placeholder="Color" value={specificFormData.color} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" />
            <select name="workoutAccessoryCategoryId" value={specificFormData.workoutAccessoryCategoryId || ""} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600">
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
    
    <div className=" items-center w-full bg-gray-950/40 backdrop-blur-md p-8 rounded-2xl shadow-lg border border-secondary/50 text-gray-100">
      <h2 className="text-xl text-center font-bold mb-2  text-secondary">
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
          //disabled={!!productToEdit} 
        >
          <option value="APPAREL">Categoría: Ropa </option>
          <option value="SUPPLEMENT">Categoría: Suplemento </option>
          <option value="WORKOUT_ACCESSORY">Categoría: Accesorio </option>
        </select>

        {/* --- CAMPOS COMUNES --- */}
        <div className="grid grid-cols-2 gap-4">
          <input name="name" placeholder="Nombre del producto" value={baseFormData.name} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" required />
          <textarea name="description" placeholder="Descripción" value={baseFormData.description} onChange={handleChange} className="w-full p-3 rounded bg-gray-800 border border-gray-600" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <input name="price" type="number" step="0.01" placeholder="Precio" value={baseFormData.price} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
          <input name="stock" type="number" step="1" placeholder="Stock" value={baseFormData.stock} onChange={handleChange} className="p-3 rounded bg-gray-800 border border-gray-600" required />
        </div>

        {/* --- CAMPOS ESPECÍFICOS --- */}
        {renderSpecificFields()}

        {/* --- IMAGEN Y BOTONES --- */}
        <input type="file" name="imageFile" accept="image/*" onChange={handleFileChange} className="block w-full text-sm text-gray-300 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-secondary file:text-primary hover:file:bg-lime-400" />
        {baseFormData.imageUrl && <img src={baseFormData.imageUrl} alt="Vista previa" className="w-32 h-32 object-cover rounded mx-auto" />}

        <div className="flex gap-4">
          <button type="submit" disabled={isSubmitting} className="flex-1 bg-secondary text-primary font-bold py-3 rounded-full hover:bg-lime-400 transition-all disabled:bg-gray-500">
            {isSubmitting ? "Guardando..." : (productToEdit ? "Actualizar producto" : "Guardar producto")}
          </button>
          {productToEdit && <button type="button" onClick={onCancelEdit} className="flex-1 bg-gray-600 text-white py-3 rounded-full hover:bg-gray-500">Cancelar</button>}
        </div>
      </form>
      </div>

      

      {message && <p className="mt-4 text-center text-lg font-medium">{message}</p>}
    </div>
  );
}