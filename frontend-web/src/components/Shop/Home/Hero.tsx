import React from "react";
import heroBg from "../../../assets/hero-bg.png"; // asegúrate que la ruta sea correcta

export default function Hero() {
  return (
    <section
      className="relative bg-cover bg-center bg-no-repeat min-h-[70vh] flex items-center justify-center text-center text-white"
      style={{ backgroundImage: `url(${heroBg})` }}
    >
      {/* Capa oscura encima del fondo para mejorar el contraste */}
      <div className="absolute inset-0 bg-black/50"></div>

      {/* Contenido centrado */}
      <div className="relative z-10 max-w-3xl px-6 space-y-6">
        <h1 className="text-4xl sm:text-6xl text-secondary font-bold leading-tight drop-shadow-md">
          Tu tienda en línea moderna y vibrante
        </h1>
        <p className=" text-lg sm:text-xl text-gray-200 drop-shadow">
          Encuentra productos increíbles con descuentos exclusivos. Todo lo que
          necesitas, en un solo lugar.
        </p>
        <div className="flex flex-col sm:flex-row justify-center gap-4 mt-6">
          <button className="bg-secondary text-primary px-8 py-3 rounded-lg font-semibold hover:bg-primary hover:text-white transition-colors">
            Comprar ahora
          </button>
          <button className="border border-white text-white px-8 py-3 rounded-lg font-semibold hover:bg-white hover:text-black transition-colors">
            Ver catálogo
          </button>
        </div>
      </div>
    </section>
  );
}
