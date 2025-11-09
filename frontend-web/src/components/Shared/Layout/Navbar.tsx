import { useState } from "react";
import { ShoppingCart, Menu, X, Search, LogIn, LogOut } from "lucide-react"; 
import { Link, NavLink } from "react-router-dom";
import { useCart } from "../../../context/CartContext";
import CartDrawer from "../../Shop/Cart/CartDrawer";
import { useKeycloak } from "@react-keycloak/web"; 


export default function Navbar() {
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isOpen, setIsOpen] = useState(false); 
  const { cartItems } = useCart();

  const { keycloak, initialized } = useKeycloak();

  const openCart = () => setIsCartOpen(true);
  const closeCart = () => setIsCartOpen(false);

  const totalItems = cartItems?.reduce((total, item) => total + item.quantity, 0) ?? 0;

  
  const handleLogin = () => {
    keycloak.login();
  };

  const handleLogout = () => {
    keycloak.logout({ redirectUri: window.location.origin });
  };
  
  const isAdmin = initialized && keycloak.authenticated && keycloak.hasRealmRole("admin");

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
                Shop
              </Link>

              {/* --- NUEVO: Ocultamos el link de Admin si no eres admin --- */}
              {isAdmin && (
                <Link to="/admin" className="hover:text-secondary transition-colors">
                  Panel Admin
                </Link>
              )}

              <a href="#" className="hover:text-secondary transition-colors">
                Categorías
              </a>

              <a href="#" className="hover:text-secondary transition-colors">
                Contacto
              </a>
            </div>

            {/* Sección derecha */}
            <div className="flex items-center gap-4">
             
              <button
                onClick={openCart}
                className="relative text-white hover:text-secondary transition-colors"
              >
                <ShoppingCart size={24} />
                {totalItems > 0 && (
                  <span className="absolute -top-2 -right-3 ...">
                    {totalItems}
                  </span>
                )}
              </button>

              {/* --- NUEVO: Lógica de Login/Logout --- */}
              <div className="flex items-center gap-4">
                {!initialized ? (
                  // Muestra un placeholder mientras Keycloak se inicializa
                  <div className="w-24 h-8 bg-gray-700 rounded-full animate-pulse"></div>
                ) : !keycloak.authenticated ? (
                  // --- ESTADO: No Autenticado ---
                  <button
                    onClick={handleLogin}
                    className="bg-secondary text-primary font-semibold px-4 py-1.5 rounded-full hover:bg-lime-400 transition-colors flex items-center gap-2"
                  >
                    <LogIn size={16} />
                    Login
                  </button>
                ) : (
                  // --- ESTADO: Autenticado ---
                  <div className="flex items-center gap-3">
                    <span className="text-sm text-gray-300 hidden sm:block">
                      Hola, {keycloak.tokenParsed?.preferred_username || 'Usuario'}
                    </span>
                    <button
                      onClick={handleLogout}
                      className="bg-gray-600 text-white font-semibold px-4 py-1.5 rounded-full hover:bg-gray-500 transition-colors flex items-center gap-2"
                    >
                      <LogOut size={16} />
                      Logout
                    </button>
                  </div>
                )}
              </div>

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
                Shop
              </Link>
              
              {/* --- NUEVO: Lógica de Admin en Móvil --- */}
              {isAdmin && (
                <Link to="/admin" className="hover:text-secondary transition-colors">
                  Panel Admin
                </Link>
              )}
              
              <a href="#" className="hover:text-secondary transition-colors">
                Categorías
              </a>
              <a href="#" className="hover:text-secondary transition-colors">
                Contacto
              </a>
              
              {/* --- NUEVO: Lógica de Login/Logout en Móvil --- */}
              {!initialized ? (
                <div className="h-8 bg-gray-700 rounded-full animate-pulse"></div>
              ) : !keycloak.authenticated ? (
                <button
                  onClick={handleLogin}
                  className="bg-secondary text-primary font-semibold px-4 py-1.5 rounded-full hover:bg-lime-400 transition-colors"
                >
                  Login
                </button>
              ) : (
                <button
                  onClick={handleLogout}
                  className="bg-gray-600 text-white font-semibold px-4 py-1.5 rounded-full hover:bg-gray-500 transition-colors"
                >
                  Logout ({keycloak.tokenParsed?.preferred_username})
                </button>
              )}
            </div>
          </div>
        )}
      </nav>

      {/* Drawer del carrito */}
      <CartDrawer isCartOpen={isCartOpen} closeCart={closeCart} />
    </>
  );
}