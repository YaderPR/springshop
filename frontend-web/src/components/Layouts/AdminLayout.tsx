import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { Package, LayoutGrid, ShoppingBag, Users, FolderTree } from 'lucide-react'; // Importamos iconos

// Helper para NavLink (para aplicar estilos al enlace activo)
const getLinkClass = ({ isActive }: { isActive: boolean }) => {
  return `flex items-center gap-3 rounded-lg px-3 py-2 transition-all
    ${
      isActive
        ? 'bg-secondary text-primary' // Estilo activo
        : 'text-gray-400 hover:text-white' // Estilo inactivo
    }`;
};

export default function AdminLayout() {
  return (
    <div className="flex h-screen bg-primary text-gray-100">
      
      {/* --- 1. BARRA DE NAVEGACIÓN LATERAL --- */}
      <aside className="w-64 flex-shrink-0 border-r border-gray-700 bg-primary p-4">
        <h2 className="text-2xl font-bold text-secondary mb-8">
          Admin Panel
        </h2>
        
        <nav className="flex flex-col gap-2">
          {/* Usamos NavLink para que resalte la ruta activa */}
          
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

          {/* Puedes añadir más enlaces aquí */}

        </nav>
      </aside>

      {/* --- 2. ÁREA DE CONTENIDO PRINCIPAL --- */}
      <main className="flex-1 overflow-y-auto p-8">
        {/* ¡Esta es la magia! 
          Aquí se renderizará AdminProducts, AdminCategories, etc.
        */}
        <Outlet />
      </main>
    </div>
  );
}