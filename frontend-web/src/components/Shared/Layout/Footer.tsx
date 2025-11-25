import React from 'react';
import { Facebook, Instagram, Youtube } from 'lucide-react';

// --- Íconos personalizados SVG (Para X y WhatsApp) ---

const XIcon = ({ className }: { className?: string }) => (
  <svg viewBox="0 0 24 24" fill="currentColor" className={className}>
    <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" />
  </svg>
);

const WhatsAppIcon = ({ className }: { className?: string }) => (
  <svg viewBox="0 0 24 24" fill="currentColor" className={className}>
    <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.008-.57-.008-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
  </svg>
);

export default function Footer() {
  
  // Configuración de redes sociales
  const socialLinks = [
    { 
      name: 'Facebook', 
      icon: Facebook, 
      url: '#', 
      colorClass: 'hover:text-[#1877F2] hover:border-[#1877F2] hover:bg-[#1877F2]/10' 
    },
    { 
      name: 'Instagram', 
      icon: Instagram, 
      url: '#', 
      colorClass: 'hover:text-[#E4405F] hover:border-[#E4405F] hover:bg-[#E4405F]/10' 
    },
    { 
      name: 'X', 
      icon: XIcon, 
      url: '#', 
      colorClass: 'hover:text-white hover:border-white hover:bg-white/10' 
    },
    { 
      name: 'Youtube', 
      icon: Youtube, 
      url: '#', 
      colorClass: 'hover:text-[#FF0000] hover:border-[#FF0000] hover:bg-[#FF0000]/10' 
    },
    { 
      name: 'Whatsapp', 
      icon: WhatsAppIcon, 
      url: 'https://wa.me/XXXXXXXX', // Tu número aquí
      colorClass: 'hover:text-[#25D366] hover:border-[#25D366] hover:bg-[#25D366]/10' 
    },
  ];

  return (
    <footer className="bg-primary text-white pt-12 pb-8 sm:px-12 lg:px-20 border-t border-gray-800">
      <div className="container mx-auto">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
          
          {/* Columna Izquierda - Links */}
          <div className="space-y-4 text-center md:text-left">
            <h3 className="text-lg font-bold text-secondary">Soporte</h3>
            <ul className="space-y-3">
              <li>
                <a href="/contacto" className="text-gray-400 hover:text-secondary transition-colors duration-300">
                  Contáctanos
                </a>
              </li>
              <li>
                <a href="/" className="text-gray-400 hover:text-secondary transition-colors duration-300">
                  Tienda
                </a>
              </li>
              <li>
                <a href="/faq" className="text-gray-400 hover:text-secondary transition-colors duration-300">
                  Preguntas Frecuentes
                </a>
              </li>
            </ul>
          </div>

          {/* Espaciador Central (opcional, o podrías poner el logo aquí) */}
          <div className="hidden md:flex justify-center items-center">
            {/* Si tienes un logo SVG pequeño iría genial aquí */}
            <span className="text-gray-700 text-6xl font-black opacity-20 select-none">SHOP</span>
          </div>

          {/* Columna Derecha - Redes Sociales */}
          <div className="space-y-4 text-center md:text-right">
            <h3 className="text-lg font-bold text-secondary">Síguenos</h3>
            <div className="flex justify-center md:justify-end gap-4">
              {socialLinks.map((social) => (
                <a
                  key={social.name}
                  href={social.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className={`
                    w-10 h-10 
                    flex items-center justify-center 
                    rounded-full 
                    bg-gray-800 
                    border border-gray-600 text-gray-400
                    transition-all duration-300 ease-in-out
                    hover:scale-110 hover:shadow-lg
                    ${social.colorClass}
                  `}
                  aria-label={social.name}
                >
                  <social.icon className="w-5 h-5" />
                </a>
              ))}
            </div>
          </div>
        </div>

        {/* Barra inferior */}
        <div className="border-t border-gray-800 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center text-gray-500 text-sm gap-4">
            <p>&copy; {new Date().getFullYear()} SpringShop. Todos los derechos reservados.</p>
            <div className="flex gap-6">
              <a href="#" className="hover:text-secondary transition-colors">Términos de servicio</a>
              <a href="#" className="hover:text-secondary transition-colors">Política de Privacidad</a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}