import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import Navbar from "./components/Shared/layout/Navbar";
import Footer from "./components/Shared/layout/Footer";
import AdminLayout from "./Layouts/AdminLayout";
import { CartProvider } from "./context/CartContext";
import CartDrawer from "./components/Shop/Cart/CartDrawer"; 

import HomePage from "./pages/Shop/HomePage";
import CheckoutPage from "./pages/Shop/CheckoutPage";
import PagoExitoso from "./pages/Shop/PagoExitoso";

// Páginas de Admin
import AdminCategories from "./pages/Admin/AdminCategories";
import AdminDashboard from "./pages/Admin/AdminDashboard";
import AdminOrders from "./pages/Admin/AdminOrders";
import AdminProducts from "./pages/Admin/AdminProducts";
import AdminUsers from "./pages/Admin/AdminUsers";

// Autenticación
import ProtectedRoute from "./components/Auth/ProtectedRoute";


export default function App() {
  return (
    <Router>
      <CartProvider>
        <div className="min-h-screen bg-black bg-[radial-gradient(circle_at_center,#181e11_0%,#000000_60%)] ">
        
          <Navbar />
          <CartDrawer /> 
          <Routes>

            <Route path="/" element={<HomePage />} />
            <Route path="/checkout" element={<CheckoutPage />} />
            <Route path="/pago-exitoso" element={<PagoExitoso />} />

            <Route
              path="/admin"
              element={
                <ProtectedRoute role="admin">
                  <AdminLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<AdminDashboard />} /> 
              <Route path="products" element={<AdminProducts />} />
              <Route path="categories" element={<AdminCategories />} />
              <Route path="orders" element={<AdminOrders />} />
              <Route path="users" element={<AdminUsers />} />
            </Route>

            {/* ¡ARREGLO AQUÍ! La ruta duplicada de /admin se ha eliminado */}

          </Routes>
          
          <Footer />
        </div>
      </CartProvider>
    </Router>
  );
}