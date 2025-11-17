// src/components/Products/ProductDetailModal.tsx (CORREGIDO)
import React, { useState, useEffect } from 'react'; // <-- Importamos useState/useEffect
import type { AnyProduct } from '../../types/Product';
import { X, Trash2, AlertTriangle } from 'lucide-react'; // <-- Importamos AlertTriangle
// import SecureImage from '../Common/SecureImage';

interface Props {
  product: AnyProduct | null;
  onClose: () => void;
  onDelete: (product: AnyProduct) => void;
  categoryName: string;
  specificInfo: string;
}

export default function ProductDetailModal({ product, onClose, onDelete, categoryName, specificInfo }: Props) {
  
  // --- ESTADO PARA LA CONFIRMACIÓN DE BORRADO ---
  const [isConfirmingDelete, setIsConfirmingDelete] = useState(false);

  // Resetea la confirmación si el producto cambia (cuando el modal se cierra/reabre)
  useEffect(() => {
    if (product) {
      setIsConfirmingDelete(false);
    }
  }, [product]);

  if (!product) {
    return null; // No renderizar nada si no hay producto
  }

  return (
    // Overlay (fondo oscuro)
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center shadow-sm bg-primary bg-opacity-10 backdrop-blur-sm p-4" // <-- Padding para móviles
      onClick={onClose} 
    >
      {/* Contenedor del Modal */}
      <div
        className="relative w-full max-w-3xl p-6 bg-primary rounded-2xl border border-secondary text-gray-100 shadow-[0_0_15px_rgba(137,254,0,.7)]" // <-- max-w-3xl (más ancho)
        onClick={(e) => e.stopPropagation()} 
      >
        {/* Botón de Cerrar */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-white z-10"
        >
          <X size={24} />
        </button>

        {/* Contenido del Modal */}
        {/* --- ¡AQUÍ ESTÁ EL ARREGLO DE LAYOUT! --- */}
        <div className="flex flex-col md:flex-row gap-6">
          
          {/* --- COLUMNA 1: IMAGEN (40% de ancho en desktop)  esto fue disque corregido, aquí es el control z--- */}
          <div className="w-full md:w-2/5 flex-shrink-0">
            <img
              src={product.imageUrl}
              alt={product.name}
              className="w-full h-auto object-cover rounded-lg" // <-- w-full para llenar su div padre
            />
          </div>

          {/* --- COLUMNA 2: INFORMACIÓN (60% de ancho en desktop) --- */}
          <div className="w-full md:w-3/5 flex-1 space-y-3">
            <h2 className="text-3xl font-bold text-secondary">{product.name}</h2>
            <p className="text-lg text-gray-300">{product.description}</p>
            
            <div className="grid grid-row gap-2 text-lg">
              <span className="font-semibold text-lime-400">${product.price.toFixed(2)}</span>
              <span className="text-gray-400">{product.stock} en stock</span>
            </div>

            <div className="text-sm text-gray-400 space-y-1 pt-2 border-t border-gray-700">
              <p><strong>Categoría:</strong> {categoryName}</p>
              <p><strong>Detalles:</strong> {specificInfo}</p>
              <p><strong>ID Producto:</strong> {product.id}</p>
            </div>
            
            {/* --- ¡AQUÍ ESTÁ EL ARREGLO DE UX (BORRADO)! --- */}
            <div className="pt-4 border-t border-gray-700">

              {/* 1. Botón de Eliminar (se muestra por defecto) */}
              {!isConfirmingDelete && (
                <button
                  onClick={() => setIsConfirmingDelete(true)} // <-- Muestra la confirmación
                  className="w-full flex items-center justify-center gap-2 bg-red-700 hover:bg-red-800 text-white font-bold py-3 rounded-lg focus:outline-none transition-all"
                >
                  <Trash2 size={18} />
                  Eliminar Producto
                </button>
              )}

              {/* 2. Confirmación (se muestra al hacer clic) */}
              {isConfirmingDelete && (
                <div className="p-4 rounded-lg bg-red-900 bg-opacity-30 border border-red-700">
                  <h4 className="flex items-center gap-2 text-lg font-semibold text-red-400">
                    <AlertTriangle size={20} />
                    Acción Permanente
                  </h4>
                  <p className="text-sm text-red-200 mt-2 mb-4">
                    ¿Estás seguro? Esta acción no se puede deshacer.
                  </p>
                  <div className="flex gap-4">
                    <button
                      onClick={() => setIsConfirmingDelete(false)} 
                      className="flex-1 bg-gray-600 text-white py-2 rounded-lg hover:bg-gray-500"
                    >
                      Cancelar
                    </button>
                    <button
                      onClick={() => onDelete(product)}
                      className="flex-1 flex items-center justify-center  gap-2 bg-red-700 hover:bg-red-800 text-white font-bold py-2 rounded-lg"
                    >
                      Sí, Confirmar
                    </button>
                  </div>
                </div>
              )}
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}