import React from "react";
import Hero from "../../components/Shop/Home/Hero";
import ProductsSection from "../../components/Shop/Product/ProductSection";
import RecommendedSection from "../../components/Shop/Home/RecomendedSection";
import CtaSection from "../../components/Shop/Home/CtaSection";
import type { Product } from "../../types/Product";
import { useCart } from "../../context/CartContext";

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