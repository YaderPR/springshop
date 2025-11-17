import { Link } from "react-router-dom";

export default function ContactPage() {
  return (
    <section className=" py-14 px-6 sm:px-10 lg:px-20">
      <div className="container mx-auto bg-primary text-secondary shadow-[0_0_15px_rgba(137,254,0,.7)] p-8 sm:p-12 rounded-2xl">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
          
          {/* Columna Izquierda: Título y Formulario */}
          <div className="space-y-6">
            <h2 className="text-4xl sm:text-5xl font-bold">
              ¿Cómo podemos ayudarte?
            </h2>
            <div className="flex items-center ring-1 ring-secondary p-2 rounded-2xl border-2 border-primary max-w-sm">
              <textarea 
                type="email" 
                placeholder="Envíanos tu consulta y estaremos encantados de contactar contigo." 
                className="flex-grow bg-primary outline-none px-4 text-secondary"
              />
              
            </div>

            <button className="ring-1 ring-secondary hover:bg-secondary hover:text-primary p-2 rounded-md">
                Enviar
              </button>
          </div>

          {/* Columna Derecha: Texto descriptivo */}
          <div className="space-y-2 text-secondary/80">
            <p>
              <strong className="text-lg font-bold" >Contactanos</strong>
            </p>
            <p>
              Siempre puedes ir a Mis pedidos en Tu cuenta para hacer un seguimiento de tu historial de pedidos.   
               <strong> Springshop</strong> siempre tendrá el producto perfecto para tí.
            </p>
            
          </div>

        </div>
      </div>
    </section>
  );
}
