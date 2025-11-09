import React, { useState } from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { Package, LayoutGrid, ShoppingBag, Users, FolderTree, Menu, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

// Tu helper de clase no cambia
const getLinkClass = ({ isActive }: { isActive: boolean }) => {
  return `flex items-center gap-3 rounded-lg px-3 py-2 transition-all
    ${
      isActive
        ? 'bg-secondary text-primary' // Estilo activo
        : 'text-gray-400 hover:text-white' // Estilo inactivo
    }`;
};

export default function AdminLayout() {
  // --- 1. ESTADO PARA CONTROLAR EL DRAWER ---
  const [isSidebarOpen, setIsSidebarOpen] = useState(true); // Default: abierto

  return (
    <div className="flex h-screen bg-black bg-[radial-gradient(circle_at_center,#181e11_0%,#000000_60%)] text-gray-100">
      
      {/* --- 2. BARRA LATERAL (DRAWER) ANIMADA --- */}
      {/* AnimatePresence maneja la "salida" (aunque aquí no es estrictamente necesario, es buena práctica) */}
      <AnimatePresence>
        <motion.aside
          className="h-screen bg-primary border-r border-gray-700 overflow-hidden"
          // 'initial={false}' previene la animación al cargar la página
          initial={false} 
          // Anima la propiedad 'width'
          animate={{ width: isSidebarOpen ? 220 : 0 }} 
          transition={{ type: "spring", stiffness: 300, damping: 30 }}
        >
          {/* Este div interior tiene un ancho fijo (w-64 = 256px) 
              para que el contenido no se "apriete" durante la animación.
              La 'motion.aside' (padre) actúa como una máscara que se encoge.
          */}
          <div className="w-50 p-4 h-full flex flex-col">
            <h2 className="text-2xl font-bold text-secondary mb-8">
              Admin Panel
            </h2>
            
            <nav className="flex flex-col gap-2">
              <NavLink to="/admin" className={getLinkClass} end>
                <LayoutGrid size={20} />
                Dashboard
              </NavLink>
              <NavLink to="/admin/products" className={getLinkClass}>
                <Package size={20} />
                Productos
              </NavLink>
              <NavLink to="/admin/categories" className={getLinkClass}>
                <FolderTree size={20} />
                Categorías
              </NavLink>
              <NavLink to="/admin/orders" className={getLinkClass}>
                <ShoppingBag size={20} />
                Órdenes
              </NavLink>
              <NavLink to="/admin/users" className={getLinkClass}>
                <Users size={20} />
                Usuarios
              </NavLink>
            </nav>
          </div>
        </motion.aside>
      </AnimatePresence>

      {/* --- 3. ÁREA DE CONTENIDO PRINCIPAL --- */}
      <main className="flex-1 flex flex-col overflow-y-auto">
        
        {/* 3a. Header con el botón para "toggle" el drawer */}
        <header className="sticky top-0 bg-gray-950/80 backdrop-blur-md border-b border-gray-700 p-4 z-20">
          <button 
            onClick={() => setIsSidebarOpen(!isSidebarOpen)}
            className="text-gray-300 hover:text-secondary"
          > 
            {isSidebarOpen ? <X size={24} /> : <Menu size={24} />} 
          </button>
        </header>

        {/* 3b. El contenido de la página (Outlet) */}
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}