import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar/Navbar";
import Footer from "./components/Footer";
import HomePage from "./pages/HomePage";
import AdminProducts from "./pages/AdminProducts";
import { CartProvider } from "./context/CartContext";
import CartDrawer from "./components/Cart/CartDrawer";
import CheckoutPage from "./pages/CheckoutPage";
import PagoExitoso from "./pages/PagoExitoso";

export default function App() {
  return (
    <Router>
      <CartProvider>
      <div className="min-h-screen bg-[radial-gradient(circle_at_600%_100%,_rgba(74,133,0,52)_50%,_rgba(0,2,1,1)_90%)] ">
          <Navbar />
            <CartDrawer />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/admin/products" element={<AdminProducts />} />
            <Route path="/Checkout" element={<CheckoutPage />} />
            <Route path="/pago-exitoso" element={<PagoExitoso />} />
          </Routes>

          <Footer />
        </div>
      </CartProvider>
    </Router>
  );
}
