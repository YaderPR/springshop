//import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./components/Shared/Layout/Navbar";
import Footer from "./components/Shared/Layout/Footer";
import { CartProvider } from "./context/CartContext";
import CartDrawer from "./components/shop/cart/CartDrawer"; 
import HomePage from "./pages/Shop/HomePage";
import CheckoutPage from "./pages/Shop/CheckoutPage";
import PagoExitoso from "./pages/Shop/PagoExitoso";

import ContactPage from "./pages/Shop/ContactPage"
import AdminCategories from "./pages/Admin/AdminCategories";
import AdminDashboard from "./pages/Admin/AdminDashboard";
import AdminOrders from "./pages/Admin/AdminOrders";
import AdminProducts from "./pages/Admin/AdminProducts";
import AdminUsers from "./pages/Admin/AdminUsers";
import ProtectedRoute from "./components/Auth/ProtectedRoute";
import MyOrderPage from "./components/Shop/Checkout/MyOrderPage";
import ProductDetail from "./components/Shop/Product/ProductDetail";
import FAQPage from "./pages/Shop/FaqPage";

export default function App() {
  return (
    <Router>
      <CartProvider>
        <div className="min-h-screen bg-black bg-[radial-gradient(circle_at_center,#181e11_0%,#000000_60%)] flex flex-col">
          
          <Navbar />
          <CartDrawer /> 
          
          {/* ¡ARREGLO! Quitamos 'container mx-auto' de aquí */}
          <main className="flex-1 p-4 container mx-auto"> 

            {/* ¡ARREGLO! Solo hay UN <Routes> que envuelve TODO */}
            <Routes>
              <Route path="/" element={<HomePage />}>
                
              </Route>

              {/* --- 2. RUTAS PÚBLICAS (Independientes) --- */}
              <Route path="/product/:type/:id" element={<ProductDetail />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/pago-exitoso" element={<PagoExitoso />} />
              <Route path="/contacto" element={<ContactPage />} />
              <Route path="/faq" element={<FAQPage />} />

              <Route 
                path="/mis-pedidos" 
                element={
                  <ProtectedRoute> 
                    <MyOrderPage />
                  </ProtectedRoute>
                } 
              />

              {/* --- 3. RUTAS DE ADMIN (Protegidas) --- */}
              <Route 
                path="/admin" 
                element={<ProtectedRoute role="admin"><AdminDashboard /></ProtectedRoute>} 
              />
              <Route 
                path="/admin/products" 
                element={<ProtectedRoute role="admin"><AdminProducts /></ProtectedRoute>} 
              />
              <Route 
                path="/admin/categories" 
                element={<ProtectedRoute role="admin"><AdminCategories /></ProtectedRoute>} 
              />
              <Route 
                path="/admin/orders" 
                element={<ProtectedRoute role="admin"><AdminOrders /></ProtectedRoute>} 
              />
              <Route 
                path="/admin/users" 
                element={<ProtectedRoute role="admin"><AdminUsers /></ProtectedRoute>} 
              />

            </Routes>
          </main>
          
          <Footer />
        </div>
      </CartProvider>
    </Router>
  );
}