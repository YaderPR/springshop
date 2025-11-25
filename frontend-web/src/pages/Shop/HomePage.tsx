import React, { useState } from "react";
import Hero from "../../components/Shop/Home/Hero";
import ProductsSection from "../../components/Shop/Product/ProductSection";
import RecommendedSection from "../../components/Shop/Home/RecomendedSection";
import CtaSection from "../../components/Shop/Home/CtaSection";

export type CategoryFilter = 'all' | 'apparel' | 'supplements' | 'accessories';

const HomePage: React.FC = () => {

  const [selectedCategory, setSelectedCategory] = useState<CategoryFilter>('all');

  return (
    <main>
      <Hero />
      <ProductsSection 
        selectedCategory={selectedCategory}
        onSelectCategory={setSelectedCategory}
      />
      
      <div className="no-scrollbar">
        <RecommendedSection />
      </div>
      <CtaSection />
    </main>
  );
};

export default HomePage;