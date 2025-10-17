import { Search } from "lucide-react";
import CategorySidebar from "./CategorySidebar";

export default function ProductsSection() {
  const products = [
    
    { id: 1, name: "Guantes de Entrenamiento Pro", price: 25.99, rating: 4.9, image: "https://siman.vtexassets.com/arquivos/ids/367551-800-800?v=637250956243030000&width=800&height=800&aspect=true/400x400/FFFFFF/000000?text=Guantes" },
    { id: 2, name: "Botella de Agua Deportiva", price: 12.5, rating: 5.0, image: "https://www.amazon.com/-/es/Botellas-gimnasio-mezcladora-prote%C3%ADnas-agitador/dp/B09SHVS4L5/400x400/FFFFFF/000000?text=Botella" },
    { id: 3, name: "Esterilla de Yoga Antideslizante", price: 32.0, rating: 4.8, image: "https://placehold.co/400x400/FFFFFF/000000?text=Esterilla" },
    { id: 4, name: "Pesas Ajustables 10KG", price: 49.99, rating: 4.7, image: "https://placehold.co/400x400/FFFFFF/000000?text=Pesas" },
  ];

  return (
    <section className="bg-primary py-16 px-6 sm:px-12 lg:px-20">
      
      <div className="flex flex-col md:flex-row justify-between items-center mb-12">
        <div className="text-left space-y-2">
          <h2 className="text-3xl font-bold text-secondary">Equípate para tu entrenamiento</h2>
          <p className="text-gray-200">
            Encuentra los mejores accesorios y equipos de gimnasio, seleccionados para ti.
          </p>
        </div>
        <div className="mt-6 md:mt-0 flex items-center bg-white rounded-full shadow px-4 py-2 w-full md:w-80">
          <Search className="w-5 h-5 text-gray-500" />
          <input
            type="text"
            placeholder="Buscar producto..."
            className="ml-2 flex-1 outline-none border-none bg-transparent text-gray-700 text-sm"
          />
        </div>
      </div>

      
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-12">
        
        
        <div className="lg:col-span-1">
          <CategorySidebar />
        </div>

        
        <div className="lg:col-span-3">
          <div className="grid gap-8 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3">
            {products.map((product) => (
             <div
            key={product.id}
            className="
              w-full
              bg-white/20 backdrop-blur-sm 
              border border-gray-700 
              rounded-2xl 
              shadow-lg 
              p-4 
              space-y-4
              ring-2 ring-lime-400/20 hover:ring-secondary transition-all duration-300
            "
          >
                
            <div className="aspect-square bg-white/90 rounded-xl overflow-hidden">
              <img src={product.image} alt={product.name} className="w-full h-full object-cover transform hover:scale-105 transition-transform duration-300" />
            </div>

            
            <h3 className="text-xl font-bold text-lime-400 truncate">{product.name}</h3>
            
            {/* Sección de Rating y Precio */}
            <div className="flex justify-between items-center text-gray-200">
              <div className="flex items-center gap-1">
                <svg className="w-5 h-5 text-secondary" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.957a1 1 0 00.95.69h4.162c.969 0 1.371 1.24.588 1.81l-3.367 2.445a1 1 0 00-.364 1.118l1.287 3.957c.3.921-.755 1.688-1.54 1.118l-3.367-2.445a1 1 0 00-1.175 0l-3.367 2.445c-.784.57-1.838-.197-1.54-1.118l1.287-3.957a1 1 0 00-.364-1.118L2.07 9.384c-.783-.57-.38-1.81.588-1.81h4.162a1 1 0 00.95-.69L9.049 2.927z"></path>
                </svg>
                <span className="font-semibold">{product.rating.toFixed(1)}</span>
              </div>
              <span className="text-xl font-bold">${product.price.toFixed(2)}</span>
            </div>

            {/* Botones de Acción */}
            <div className="flex gap-3 text-sm font-bold">
              <button className="w-full bg-gray-200 text-gray-900 py-3 rounded-full hover:bg-white transition-colors">
                Add to cart
              </button>
              <button className="w-full bg-black text-white py-3 rounded-full hover:bg-secondary hover:text-primary transition-colors">
                Buy Now
              </button>
            </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}