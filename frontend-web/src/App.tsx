import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar/Navbar";
import Footer from "./components/Footer";
import HomePage from "./pages/HomePage";
import AdminProducts from "./pages/AdminProducts";
import { CartProvider } from "./context/CartContext";
import CartDrawer from "./components/Cart/CartDrawer";

export default function App() {
  return (
    <Router>
      <CartProvider>
        <div className="bg-primary min-h-screen ">
          <Navbar />
            <CartDrawer />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/admin/products" element={<AdminProducts />} />
          </Routes>

          <Footer />
        </div>
      </CartProvider>
    </Router>
  );
}
