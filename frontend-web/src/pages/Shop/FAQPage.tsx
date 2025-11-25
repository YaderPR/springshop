import React, { useState } from 'react';
import { ChevronDown, ChevronUp, HelpCircle, Truck, CreditCard, RefreshCw, ShieldCheck, Mail } from 'lucide-react';
import { Link } from 'react-router-dom';

// --- DATA: Aquí definimos las preguntas y respuestas ---
const faqData = [
  {
    category: "Envíos y Entregas",
    icon: Truck,
    items: [
      {
        question: "¿A qué departamentos de Nicaragua realizan envíos?",
        answer: "Realizamos envíos a todo el territorio nacional. En Managua contamos con mensajería express (entrega el mismo día o al día siguiente) y para los departamentos utilizamos CargoTrans o transporte interlocales seguros."
      },
      {
        question: "¿Cuál es el costo del envío?",
        answer: "El envío en Managua tiene un costo fijo de $3.00. Para los departamentos, el costo varía entre $4.00 y $6.00 dependiendo del peso del paquete y la ubicación exacta."
      },
      {
        question: "¿Cuánto tarda en llegar mi pedido?",
        answer: "En Managua: 24 horas hábiles. Departamentos: 24 a 48 horas hábiles una vez confirmado el pago."
      }
    ]
  },
  {
    category: "Pagos y Facturación",
    icon: CreditCard,
    items: [
      {
        question: "¿Qué métodos de pago aceptan?",
        answer: "Aceptamos pagos con tarjeta de crédito/débito directamente en la web, transferencias bancarias (BAC, Banpro, Lafise) y pagos en efectivo contra entrega (solo disponible en Managua)."
      },
      {
        question: "¿Es seguro pagar con mi tarjeta en la web?",
        answer: "Totalmente. Utilizamos una pasarela de pago encriptada y no almacenamos los datos completos de tu tarjeta. Tu seguridad es nuestra prioridad."
      }
    ]
  },
  {
    category: "Cambios y Devoluciones",
    icon: RefreshCw,
    items: [
      {
        question: "¿Puedo cambiar una prenda si no me queda?",
        answer: "¡Sí! Tienes 3 días hábiles después de recibir tu pedido para solicitar un cambio por talla. La prenda debe estar con sus etiquetas originales y sin uso. Los costos de envío por cambio corren por cuenta del cliente."
      },
      {
        question: "¿Los suplementos tienen devolución?",
        answer: "Por seguridad e higiene, no aceptamos devoluciones en suplementos o vitaminas una vez que el sello de seguridad ha sido abierto o alterado."
      }
    ]
  },
  {
    category: "Productos y Garantía",
    icon: ShieldCheck,
    items: [
      {
        question: "¿Los suplementos son originales?",
        answer: "Garantizamos al 100% la autenticidad de todos nuestros productos. Trabajamos directamente con distribuidores autorizados de marcas como Optimum Nutrition, Dymatize, etc."
      },
      {
        question: "¿Los accesorios de gimnasio tienen garantía?",
        answer: "Sí, los cinturones, correas y accesorios tienen una garantía de 30 días por defectos de fábrica (costuras, hebillas, etc.)."
      }
    ]
  }
];

// --- COMPONENTE INDIVIDUAL DE PREGUNTA ---
const FAQItem = ({ question, answer, isOpen, onClick }: { question: string, answer: string, isOpen: boolean, onClick: () => void }) => {
  return (
    <div className="border border-gray-700 rounded-lg bg-gray-800/50 overflow-hidden mb-3 transition-all duration-300 hover:border-secondary/50">
      <button
        onClick={onClick}
        className="w-full flex justify-between items-center p-4 text-left focus:outline-none"
      >
        <span className={`font-medium ${isOpen ? 'text-secondary' : 'text-gray-200'}`}>
          {question}
        </span>
        {isOpen ? (
          <ChevronUp className="w-5 h-5 text-secondary transition-transform" />
        ) : (
          <ChevronDown className="w-5 h-5 text-gray-500 transition-transform" />
        )}
      </button>
      
      <div 
        className={`
          transition-all duration-300 ease-in-out overflow-hidden
          ${isOpen ? 'max-h-96 opacity-100' : 'max-h-0 opacity-0'}
        `}
      >
        <div className="p-4 pt-0 text-gray-400 text-sm leading-relaxed border-t border-gray-700/50 mt-2">
          {answer}
        </div>
      </div>
    </div>
  );
};

// --- PÁGINA PRINCIPAL ---
export default function FAQPage() {
  // Estado para controlar qué pregunta está abierta (solo una a la vez por categoría o global)
  // Usamos un string tipo "categoryIndex-itemIndex" para identificar cuál está abierto
  const [openId, setOpenId] = useState<string | null>(null);

  const toggleItem = (id: string) => {
    setOpenId(openId === id ? null : id);
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        
        {/* ENCABEZADO */}
        <div className="text-center mb-16">
          <div className="flex justify-center mb-4">
            <div className="p-3 bg-secondary/10 rounded-full">
              <HelpCircle className="w-10 h-10 text-secondary" />
            </div>
          </div>
          <h1 className="text-4xl font-extrabold text-white mb-4">Preguntas Frecuentes</h1>
          <p className="text-gray-400 max-w-2xl mx-auto">
            Aquí encontrarás respuestas a las dudas más comunes sobre tus compras en SpringShop. 
            Si no encuentras lo que buscas, contáctanos.
          </p>
        </div>

        {/* SECCIONES DE FAQ */}
        <div className="space-y-12">
          {faqData.map((section, sectionIdx) => (
            <div key={sectionIdx} className="animate-fade-in-up">
              <div className="flex items-center gap-3 mb-6 border-b border-gray-800 pb-2">
                <section.icon className="w-6 h-6 text-secondary" />
                <h2 className="text-2xl font-bold text-white">{section.category}</h2>
              </div>

              <div className="space-y-1">
                {section.items.map((item, itemIdx) => {
                  const uniqueId = `${sectionIdx}-${itemIdx}`;
                  return (
                    <FAQItem
                      key={uniqueId}
                      question={item.question}
                      answer={item.answer}
                      isOpen={openId === uniqueId}
                      onClick={() => toggleItem(uniqueId)}
                    />
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        {/* CTA FINAL (Contacto) */}
        <div className="mt-16 bg-gray-800 border border-gray-700 rounded-2xl p-8 text-center">
          <h3 className="text-xl font-bold text-white mb-2">¿Aún tienes dudas?</h3>
          <p className="text-gray-400 mb-6">Estamos aquí para ayudarte. Escríbenos y te responderemos lo antes posible.</p>
          <div className="flex justify-center gap-4">
            <Link 
              to="/contacto" 
              className="flex items-center gap-2 bg-secondary text-primary font-bold py-3 px-6 rounded-lg hover:bg-lime-400 transition-colors shadow-[0_0_15px_rgba(137,254,0,0.3)]"
            >
              <Mail className="w-5 h-5" />
              Contáctanos
            </Link>
          </div>
        </div>

      </div>
    </div>
  );
}