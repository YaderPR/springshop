import Navbar from "./components/Navbar";
import Hero from "./components/Hero";
import ProductsSection from "./components/ProductSection";
import RecommendedSection from "./components/RecomendedSection";
import CtaSection from "./components/CtaSection";
import Footer from "./components/Footer";

export default function App() {
  return (
    <div className="bg-gray min-h-screen">
      <Navbar />
      <Hero />
      <ProductsSection />
      <RecommendedSection />
      <CtaSection />
      <Footer />
    </div>
  );
}
