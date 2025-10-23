import { useState } from "react";
import { ShoppingCart, Menu, X, Search, User } from "lucide-react";
import { Link } from "react-router-dom";

export default function Navbar() {
  const [isOpen, setIsOpen] = useState(false);

  // const isLoggedIn = false; 

  return (
    <nav className="bg-primary shadow-sm sticky top-0 z-50 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <div className="flex-shrink-0 text-2xl font-bold text-secondary">
            SpringShop
          </div>

          {/* Desktop menu */}
          <div className="hidden md:flex space-x-8 text-gray-100 font-medium">
            {/* INICIO */}
            <Link to="/" className="hover:text-secondary transition-colors">
              Inicio
            </Link>
            {/* PRODUCTOS (Aquí puedes usar Link o <a> si es una URL externa) */}
            <a href="#" className="hover:text-secondary transition-colors">
              Productos
            </a>
            {/* CATEGORÍAS */}
            <a href="#" className="hover:text-secondary transition-colors">
              Categorías
            </a>
            {/* NUEVA RUTA: AÑADIR PRODUCTO */}
            <Link 
              to="/products/new" 
              className="hover:text-secondary transition-colors"
            >
              Añadir Producto
            </Link>
            {/* CONTACTO */}
            <a href="#" className="hover:text-secondary transition-colors">
              Contacto
            </a>
          </div>

          {/* Search + Icons */}
          <div className="flex items-center gap-4">
            {/* Search bar */}
            <div className="hidden sm:flex items-center bg-white rounded-full px-3 py-1">
              <Search className="w-5 h-5 text-gray-500" />
              <input
                type="text"
                placeholder="Buscar..."
                className="ml-2 outline-none border-none bg-transparent text-gray-700 text-sm"
              />
            </div>

            {/* Cart */}
            <button className="relative p-2 text-gray-200 hover:text-secondary">
              <ShoppingCart className="w-6 h-6" />
              <span className="absolute -top-1 -right-1 bg-secondary text-primary text-xs rounded-full w-4 h-4 flex items-center justify-center">
                3
              </span>
            </button>

            {/* User / Login */}
            {/* {isLoggedIn ? (
              <div className="relative">
                <img
                  src="/path/to/avatar.jpg"
                  alt="Avatar"
                  className="w-8 h-8 rounded-full border-2 border-secondary cursor-pointer"
                />
              </div>
            ) : ( */}
              <button className="bg-secondary text-primary font-semibold px-4 py-1.5 rounded-full hover:bg-primary hover:text-accent transition-colors">
                Login
              </button>
            {/* )} */}

            {/* Mobile menu button */}
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="md:hidden p-2 text-gray-200 hover:text-secondary"
            >
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {isOpen && (
        <div className="md:hidden bg-primary text-gray-100 shadow-inner">
          <div className="flex flex-col px-4 py-3 space-y-2 font-medium">
            <a href="#" className="hover:text-secondary transition-colors">
              Inicio
            </a>
            <a href="#" className="hover:text-secondary transition-colors">
              Productos
            </a>
            <a href= "#" classNamve="hover:text-secondary transtion-colors">
              Add products
            </a>
            <a href="#" className="hover:text-secondary transition-colors">
              Categorías
            </a>
            <a href="#" className="hover:text-secondary transition-colors">
              Contacto
            </a>
            <button className="bg-secondary text-primary font-semibold px-4 py-1.5 rounded-full hover:bg-green-400 transition-colors">
              Login
            </button>
          </div>
        </div>
      )}
    </nav>
  );
}
