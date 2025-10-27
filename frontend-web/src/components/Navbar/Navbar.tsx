import { useState } from "react";
import { ShoppingCart, Menu, X, Search } from "lucide-react";
import { Link } from "react-router-dom";
import { useCart } from "../../context/CartContext";
import CartDrawer from "../Cart/CartDrawer";

export default function Navbar() {
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isOpen, setIsOpen] = useState(false); 
  const { cartItems } = useCart();

  const openCart = () => setIsCartOpen(true);
  const closeCart = () => setIsCartOpen(false);

  const totalItems = cartItems.reduce((total, item) => total + item.quantity, 0);

  // const isLoggedIn = false;

  return (
    <>
      <nav className="bg-primary shadow-sm sticky top-0 z-50 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            
            {/* Logo */}
            <div className="flex-shrink-0 text-2xl font-bold text-secondary">
              SpringShop
            </div>

            {/* Links de navegación */}
            <div className="hidden md:flex space-x-8 text-gray-100 font-medium">
              <Link to="/" className="hover:text-secondary transition-colors">
                Inicio
              </Link>

              <Link to="/admin/products" className="hover:text-secondary transition-colors">
                Productos
              </Link>

              <a href="#" className="hover:text-secondary transition-colors">
                Categorías
              </a>

              <a href="#" className="hover:text-secondary transition-colors">
                Contacto
              </a>
            </div>

            {/* Sección derecha */}
            <div className="flex items-center gap-4">
              {/* Buscador */}
              <div className="hidden sm:flex items-center bg-white rounded-full px-3 py-1">
                <Search className="w-5 h-5 text-gray-500" />
                <input
                  type="text"
                  placeholder="Buscar..."
                  className="ml-2 outline-none border-none bg-transparent text-gray-700 text-sm"
                />
              </div>

              {/* Carrito */}
              <button
                onClick={openCart}
                className="relative text-white hover:text-secondary transition-colors"
              >
                <ShoppingCart size={24} />
                {totalItems > 0 && (
                  <span
                    className="
                      absolute -top-2 -right-3 
                      bg-red-600 text-white 
                      text-xs font-bold 
                      w-5 h-5 
                      rounded-full 
                      flex items-center justify-center
                    "
                  >
                    {totalItems}
                  </span>
                )}
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
               {/* Login */}
              <button className="bg-secondary text-primary font-semibold px-4 py-1.5 rounded-full hover:bg-primary hover:text-accent transition-colors">
                Login
              </button>

              {/* Menú móvil */}
              <button
                onClick={() => setIsOpen(!isOpen)}
                className="md:hidden p-2 text-gray-200 hover:text-secondary"
              >
                {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
              </button>
            </div>
          </div>
        </div>

        {/* Menú móvil */}
        {isOpen && (
          <div className="md:hidden bg-primary text-gray-100 shadow-inner">
            <div className="flex flex-col px-4 py-3 space-y-2 font-medium">
              <Link to="/" className="hover:text-secondary transition-colors">
                Inicio
              </Link>
              <Link to="/admin/products" className="hover:text-secondary transition-colors">
                Productos
              </Link>
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

      {/* Drawer del carrito */}
      <CartDrawer isCartOpen={isCartOpen} closeCart={closeCart} />
    </>
  );
}
