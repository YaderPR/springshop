import React, { useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import AnimatedCheck from '../../components/Shared/Common/AnimatedChecked';
import DeliveryTruck from '../../components/Shared/Common/DeliveryTruck';

export default function PagoExitoso() {
  // Opcional: Hook para leer los parámetros de la URL (ej. ?session_id=...)
  const [searchParams] = useSearchParams();
  
  useEffect(() => {
    const sessionId = searchParams.get('session_id');
    if (sessionId) {
      console.log("Pago completado para la sesión:", sessionId);
      // Aquí podrías (opcionalmente) llamar a un endpoint 
      // para verificar la sesión, aunque el webhook ya hizo el trabajo principal.
    }
  }, [searchParams]);

  return (
    // Contenedor principal a pantalla completa
    <div className=" flex items-start pt-16 justify-center min-h-screen w-full bg-primary text-gray-100 overflow-hidden">
      
      {/* El camión animado (en el fondo) */}
      <DeliveryTruck />

      {/* Tarjeta de Éxito (la centramos) */}
      <div 
        className="relative z-10 w-full max-w-md p-8 text-center bg-primary rounded-2xl border border-secondary shadow-[0_0_15px_rgba(137,254,0,.7)]"
      >
        {/* 1. El Check Animado */}
        <div className="flex justify-center mb-4">
          <AnimatedCheck />
        </div>

        {/* 2. El Mensaje */}
        <h1 className="text-3xl font-bold text-secondary mb-4">
          ¡Pago Exitoso!
        </h1>
        <p className="text-lg text-gray-300 mb-8">
          Gracias por tu compra. Hemos recibido tu pago y tu orden está siendo procesada.
        </p>

        {/* 3. Botón para volver al inicio */}
        <Link
          to="/"
          className="inline-block bg-secondary text-primary font-bold py-3 px-8 rounded-full hover:bg-lime-400 transition-all"
        >
          Volver a la Tienda
        </Link>
      </div>
    </div>
  );
}