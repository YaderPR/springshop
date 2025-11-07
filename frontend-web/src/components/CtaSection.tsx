// src/components/CtaSection.tsx

import React from 'react';

export default function CtaSection() {
  return (
    <section className=" py-16 px-6 sm:px-12 lg:px-20">
      <div className="container mx-auto bg-secondary text-primary p-8 sm:p-12 rounded-2xl">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
          
          {/* Columna Izquierda: Título y Formulario */}
          <div className="space-y-6">
            <h2 className="text-4xl sm:text-5xl font-bold">
              Listo para recibir nuestras novedades?
            </h2>
            <div className="flex items-center bg-white p-2 rounded-full border-2 border-primary max-w-sm">
              <input 
                type="email" 
                placeholder="Your email" 
                className="flex-grow bg-transparent outline-none px-4 text-gray-700"
              />
              <button className="bg-secondary text-primary font-bold py-2 px-6 rounded-full hover:bg-black hover:text-white transition-colors">
                Search
              </button>
            </div>
          </div>

          {/* Columna Derecha: Texto descriptivo */}
          <div className="space-y-2 text-primary/90">
            <p>
              <strong className="text-lg font-bold" >Products, accessories and more for your fitness life</strong>
            </p>
            <p>
              The best products, the best quality supplements, the best clothing 
              for your fitness life, are at <strong className="font-bold">Spring Shop</strong>
            </p>
          </div>

        </div>
      </div>
    </section>
  );
}