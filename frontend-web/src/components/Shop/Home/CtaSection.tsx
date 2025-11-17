
import React from 'react';

export default function CtaSection() {
  return (
    <section className=" py-14 px-6 sm:px-10 lg:px-20">
      <div className="container mx-auto bg-primary text-secondary shadow-[0_0_15px_rgba(137,254,0,.7)] p-8 sm:p-12 rounded-2xl">
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
              
            </div>
          </div>

          {/* Columna Derecha: Texto descriptivo */}
          <div className="space-y-2 text-secondary/80">
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