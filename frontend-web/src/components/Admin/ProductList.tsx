// src/components/Products/ProductList.tsx (¡REDISEÑADO!)
import { useEffect, useState } from "react";
import {
  getApparels,
  getSupplements,
  getWorkoutAccessories,
  deleteApparel,
  deleteSupplement,
  deleteWorkoutAccessory,
  getCategories,
  getApparelCategories,
  getWorkoutAccessoryCategories,
} from "../../services/product/ProductService";
import type {
  AnyProduct,
  Category,
  ApparelCategory,
  WorkoutAccessoryCategory,
  Apparel,
  Supplement,
  WorkoutAccessory,
} from "../../types/Product";
import ProductDetailModal from "./ProductDetailModal"; // <-- ¡Importamos el nuevo modal!
// import SecureImage from "../Common/SecureImage";
import { Edit, MoreVertical, AlertTriangle } from "lucide-react"; // Iconos

interface Props {
  refreshSignal: boolean;
  onEdit: (product: AnyProduct) => void;
}

// Estado para las categorías
interface AllCategories {
  generic: Map<number, string>;
  apparel: Map<number, string>;
  workoutAccessory: Map<number, string>;
}

export default function ProductList({ refreshSignal, onEdit }: Props) {
  const [products, setProducts] = useState<AnyProduct[]>([]);
  const [categories, setCategories] = useState<AllCategories>({
    generic: new Map(),
    apparel: new Map(),
    workoutAccessory: new Map(),
  });

  // --- NUEVO ESTADO PARA EL MODAL ---
  const [modalProduct, setModalProduct] = useState<AnyProduct | null>(null);

  // Cargar todos los datos (igual que antes)
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [
          apparelsData,
          supplementsData,
          workoutAccessoriesData,
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

        const allProducts = [
          ...apparelsData,
          ...supplementsData,
          ...workoutAccessoriesData,
        ];
        allProducts.sort((a, b) => a.id - b.id);
        setProducts(allProducts);

        setCategories({
          generic: new Map(genericCats.map((c) => [c.id, c.name])),
          apparel: new Map(apparelCats.map((c) => [c.id, c.name])),
          workoutAccessory: new Map(workoutCats.map((c) => [c.id, c.name])),
        });
      } catch (error) {
        console.error("Error al cargar datos:", error);
      }
    };

    fetchData();
  }, [refreshSignal]);

  // --- NUEVOS MANEJADORES DE MODAL ---
  const handleOpenDetails = (product: AnyProduct) => {
    setModalProduct(product);
  };

  const handleCloseModal = () => {
    setModalProduct(null);
  };

  // --- LÓGICA DE BORRADO (AHORA LLAMADA DESDE EL MODAL) ---
  const handleDelete = async (product: AnyProduct) => {
    if (!product) return;

    try {
      if ("flavor" in product) {
        await deleteSupplement(product.id);
      } else if ("material" in product) {
        await deleteWorkoutAccessory(product.id);
      } else {
        await deleteApparel(product.id);
      }

      setProducts(products.filter((p) => p.id !== product.id));
      handleCloseModal(); // Cierra el modal después de borrar
    } catch (err) {
      console.error("Falló al eliminar", err);
      // (En el futuro, podrías mostrar un error en el modal)
    }
  };

  // --- FUNCIONES HELPER (igual que antes) ---
  const getCategoryName = (product: AnyProduct): string => {
    if ("flavor" in product) {
      // Supplement
      const p = product as Supplement;
      return categories.generic.get(p.categoryId || -1) || "Sin categoría";
    }
    if ("material" in product) {
      // WorkoutAccessory
      const p = product as WorkoutAccessory;
      return (
        categories.workoutAccessory.get(p.workoutAccessoryCategoryId || -1) ||
        "Sin categoría"
      );
    }
    if ("size" in product) {
      // Apparel
      const p = product as Apparel;
      return (
        categories.apparel.get(p.apparelCategoryId || -1) || "Sin categoría"
      );
    }
    return "N/A";
  };

  const getSpecificInfo = (product: AnyProduct) => {
    if ("flavor" in product) {
      return `Marca: ${product.brand} | Sabor: ${product.flavor}`;
    }
    if ("material" in product) {
      return `Material: ${product.material} | Color: ${product.color}`;
    }
    if ("size" in product) {
      return `Marca: ${product.brand} | Color: ${product.color} | Talla: ${product.size}`;
    }
    return "";
  };

  return (
    <div className="mt-6 rounded-lg">
      <div className="bg-gray-950/40 rounded-2xl  ring-1 ring-secondary/50">
        <h2 className="text-xl p-6 font-bold text-center  text-secondary">
          Listado de Productos
        </h2>
        <div className="space-y-4 p-8 max-h-[500px] overflow-y-auto no-scrollbar">
          {products.length > 0 ? (
            products.map((p) => (
              <div
                key={p.id}
                className="
              w-full
            bg-gray-900/90 
              backdrop-blur-lm 
              border border-gray-700 
              rounded-xl      
              shadow-lg
              p-2             
              space-y-2       
              ring-1 ring-lime-400/20 
              transition-all duration-300
              hover:ring-secondary
              hover:shadow-[0_0_15px_rgba(137,254,0,.7)] flex items-center gap-4 text-gray-100"
              >
                <img
                  src={p.imageUrl}
                  alt={p.name}
                  className="w-24 h-24 object-cover rounded-lg flex-shrink-0"
                />

                {/* 2. DETALLES (flex-grow para tomar el espacio) */}
                <div className="flex-grow space-y-1">
                  <div className="flex justify-between items-center">
                    <h3 className="text-lg font-bold text-secondary">
                      {p.name}
                    </h3>
                    <span className="text-xl font-semibold text-lime-400">
                      ${p.price.toFixed(2)}
                    </span>
                  </div>
                  <p className="text-sm text-gray-400">{getCategoryName(p)}</p>
                  <p className="text-xs text-gray-500 truncate">
                    {getSpecificInfo(p)}
                  </p>
                  <div className="text-sm">
                    {p.stock > 10 ? (
                      <span className="text-green-500">{p.stock} en stock</span>
                    ) : (
                      <span className="text-red-500 font-bold flex items-center gap-1">
                        <AlertTriangle size={14} /> ¡Solo {p.stock} en stock!
                      </span>
                    )}
                  </div>
                </div>

                {/* 3. ACCIONES (Tu petición) */}
                <div className="flex flex-col gap-2 flex-shrink-0">
                  <button
                    onClick={() => onEdit(p)}
                    className="flex items-center justify-center gap-2 text-green-700 hover:text-white border border-green-700 hover:bg-green-800 focus:ring-4 focus:outline-none focus:ring-green-300 font-medium rounded-lg text-sm px-4 py-2 text-center dark:border-green-500 dark:text-green-500 dark:hover:text-white dark:hover:bg-green-600 dark:focus:ring-green-800"
                  >
                    <Edit size={16} />
                    Editar
                  </button>
                  <button
                    onClick={() => handleOpenDetails(p)}
                    className="flex items-center justify-center gap-2 text-gray-400 hover:text-white border border-gray-500 hover:bg-gray-600 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-4 py-2 text-center"
                  >
                    <MoreVertical size={16} />
                    Detalles
                  </button>
                </div>
              </div>
            ))
          ) : (
            <p className="text-center text-gray-500 py-10">
              No hay productos aún.
            </p>
          )}
        </div>
      </div>

      {/* ¡RENDERIZAMOS EL MODAL!
        Solo es visible si 'modalProduct' no es nulo.
      */}
      <ProductDetailModal
        product={modalProduct}
        onClose={handleCloseModal}
        onDelete={handleDelete}
        categoryName={modalProduct ? getCategoryName(modalProduct) : ""}
        specificInfo={modalProduct ? getSpecificInfo(modalProduct) : ""}
      />
    </div>
  );
}
