import { useState, useRef, useEffect } from "react"; 
import {
  ShoppingCart, Menu, X, LogOut, LogIn,
  LayoutGrid, Package, FolderTree, ShoppingBag, Users, ChevronDown
} from "lucide-react";
import { Link, NavLink } from "react-router-dom";
import { useCart } from "../../../context/CartContext";
import CartDrawer from "../../Shop/Cart/CartDrawer"; 
import { useKeycloak } from "@react-keycloak/web";
import { motion, AnimatePresence } from "framer-motion"; 

const useClickOutside = (ref: React.RefObject<HTMLDivElement>, handler: () => void) => {
  useEffect(() => {
    const listener = (event: MouseEvent | TouchEvent) => {
      if (!ref.current || ref.current.contains(event.target as Node)) {
        return;
      }
      handler();
    };
    document.addEventListener("mousedown", listener);
    document.addEventListener("touchstart", listener);
    return () => {
      document.removeEventListener("mousedown", listener);
      document.removeEventListener("touchstart", listener);
    };
  }, [ref, handler]);
};

export default function Navbar() {
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const { cartItems } = useCart();
  const { keycloak, initialized } = useKeycloak();
  const [isAdminMenuOpen, setIsAdminMenuOpen] = useState(false);
  const adminMenuRef = useRef<HTMLDivElement>(null);
  //useClickOutside(adminMenuRef, () => setIsAdminMenuOpen(false)); 
  const [isCategoryMenuOpen, setIsCategoryMenuOpen] = useState(false);
  const categoryMenuRef = useRef<HTMLDivElement>(null);
  //useClickOutside(categoryMenuRef, () => setIsCategoryMenuOpen(false));
  const openCart = () => setIsCartOpen(true);
  const closeCart = () => setIsCartOpen(false);

  const closeAllMenus = () => {
    setIsMobileMenuOpen(false);
    setIsAdminMenuOpen(false);
    
  };

  const handleLogin = () => keycloak.login();
  const handleLogout = () => keycloak.logout({ redirectUri: window.location.origin });
  const isAdmin = initialized && keycloak.authenticated && keycloak.hasRealmRole("admin");
  const isUser = initialized && keycloak.authenticated && !isAdmin;

  const totalItems = cartItems?.reduce((total, item) => total + item.quantity, 0) ?? 0;

  return (
    <>
      <nav className="bg-primary/70 backdrop-blur-lg sticky top-0 z-50 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-3 items-center h-16">        
            <div className="flex justify-start">
              <Link to="/" className="flex-shrink-0 text-2xl font-bold text-secondary">
                SpringShop
              </Link>
            </div>
            
            <div className="hidden md:flex justify-center space-x-6 text-gray-100 font-medium">
              <Link to="/" className="hover:text-secondary transition-colors">
                Shop
              </Link>

              <div className="relative" ref={categoryMenuRef}>
                <Link to="/mis-pedidos" className="flex items-center gap-1 hover:text-secondary transition-colors">
                  Mis pedidos
                </Link>
              
              </div>

              <Link to="/contacto" className="hover:text-secondary transition-colors">
                Contactanos
              </Link>

              {/* Dropdown de Admin */}
              {isAdmin && (
                <div className="relative" ref={adminMenuRef}>
                  <button
                    onClick={() => setIsAdminMenuOpen(!isAdminMenuOpen)}
                    className="flex items-center gap-1 font-medium text-secondary hover:text-lime-400"
                  >
                    Panel Admin
                    <ChevronDown size={16} className={`transition-transform ${isAdminMenuOpen ? 'rotate-180' : ''}`} />
                  </button>
                  
                  {/* El Menú Desplegable */}
                  <AnimatePresence>
                    {isAdminMenuOpen && (
                      <motion.div
                        initial={{ opacity: 0, y: -10 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: -10 }}
                        className="absolute top-full left-0 mt-2 w-30 bg-primary border border-gray-700 rounded-lg shadow-lg z-50"
                      >
                        <nav className="flex flex-col gap-1 p-2">
                          <NavLink to="/admin" className={getLinkClass} onClick={() => setIsAdminMenuOpen(false)} end>
                            <LayoutGrid size={18} /> Dashboard
                          </NavLink>
                          <NavLink to="/admin/products" className={getLinkClass} onClick={() => setIsAdminMenuOpen(false)}>
                            <Package size={18} /> Productos
                          </NavLink>
                          <NavLink to="/admin/categories" className={getLinkClass} onClick={() => setIsAdminMenuOpen(false)}>
                            <FolderTree size={18} /> Categorías
                          </NavLink>
                          <NavLink to="/admin/orders" className={getLinkClass} onClick={() => setIsAdminMenuOpen(false)}>
                            <ShoppingBag size={18} /> Órdenes
                          </NavLink>
                          <NavLink to="/admin/users" className={getLinkClass} onClick={() => setIsAdminMenuOpen(false)}>
                            <Users size={18} /> Usuarios
                          </NavLink>
                        </nav>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
              )}
            </div>

  
            <div className="flex justify-end items-center gap-6">
              {/* Carrito */}
              <button
                onClick={openCart}
                className="relative text-white hover:text-secondary transition-colors"
              >
                <ShoppingCart size={24} />
                {totalItems > 0 && (
                  <span className="absolute -top-2 -right-3 ring-2 ring-secondary bg-primary rounded-full w-6 h-6">
                    {totalItems}
                  </span>
                )}
              </button>

              {/* Lógica de Login/Logout */}
              <div className="hidden sm:flex items-center gap-4">
                {!initialized ? (
                  <div className="w-24 h-8 bg-gray-700 rounded-full animate-pulse"></div>
                ) : !keycloak.authenticated ? (
                  <button onClick={handleLogin} className="bg-secondary text-primary font-semibold px-4 py-1.5 rounded-full hover:bg-lime-400 transition-colors flex items-center gap-2">
                    <LogIn size={16} /> Login
                  </button>
                ) : (
                  <div className="flex items-center gap-3">
                    <span className="text-sm text-gray-300 hidden sm:block">
                      Hola, {keycloak.tokenParsed?.preferred_username || 'Usuario'}
                    </span>
                    <button onClick={handleLogout} className="bg-gray-600 text-white font-semibold px-4 py-1.5 rounded-full hover:bg-gray-500 transition-colors flex items-center gap-2">
                      <LogOut size={16} /> Logout
                    </button>
                  </div>
                )}
              </div>

              {/* Menú móvil (botón) */}
              <button
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                className="md:hidden p-2 text-gray-200 hover:text-secondary"
              >
                {isMobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
              </button>
            </div>
            
          </div>
        </div>

 
        <AnimatePresence>
          {isMobileMenuOpen && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              className="md:hidden bg-primary text-gray-100 shadow-inner overflow-hidden"
            >

            </motion.div>
          )}
        </AnimatePresence>
      </nav>

      {/* Drawer del carrito */}
      <CartDrawer isCartOpen={isCartOpen} closeCart={closeCart} />
    </>
  );
}


const getLinkClass = ({ isActive }: { isActive: boolean }) => {
  return `flex items-center gap-3 rounded-lg px-3 py-2 transition-all text-sm
    ${
      isActive
        ? 'bg-secondary text-primary' 
        : 'text-gray-400 hover:text-white' 
    }`;
};