import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar/Navbar";
import Footer from "./components/Footer";
import HomePage from "./pages/HomePage"; 
import AdminProducts from "./pages/AdminProducts";
 

export default function App() {
    return (
       <Router>
            <div className="bg-primary min-h-screen "> 
                <Navbar />
                
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/admin/products" element={<AdminProducts />} />
                </Routes>
                
                <Footer />
            </div>
        </Router>
    );
}