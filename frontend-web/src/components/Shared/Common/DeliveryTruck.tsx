import { motion } from "framer-motion";
import { Truck } from "lucide-react";

export default function DeliveryTruck() {
  return (
    <div className="absolute bottom-0 left-0 w-full h-24 overflow-hidden">
      {/* La carretera (una línea sutil) */}
      <div className="absolute bottom-10 w-full h-0.5 bg-gray-700" />
      
      {/* El camión animado */}
      <motion.div
        className="absolute bottom-10"
        initial={{ x: "-200px" }} // Empieza fuera de la pantalla (izquierda)
        animate={{ x: "100vw" }}   // Termina fuera de la pantalla (derecha)
        transition={{
          duration: 5,          // Duración de 10 segundos
          ease: "linear",
          repeat: Infinity,       // Repite para siempre
          repeatDelay: 2,         // Espera 2 segundos antes de repetir
        }}
      >
        <Truck size={48} className="text-gray-500" />
      </motion.div>
    </div>
  );
}