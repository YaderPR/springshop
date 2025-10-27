import React from "react";
import Hero from "../components/Hero";
import ProductsSection from "../components/ProductSection";
import RecommendedSection from "../components/RecomendedSection";
import CtaSection from "../components/CtaSection";
import type { Product } from "../types/Product";
import { useCart } from "../context/CartContext";

// Este es el componente que será renderizado en la ruta "/"
const HomePage: React.FC = () => {
    return (
        <main>
            <Hero />
            <ProductsSection />
            <RecommendedSection  className="no-scrollbar"/>
            <CtaSection />
        </main>
    );
};

export default HomePage;