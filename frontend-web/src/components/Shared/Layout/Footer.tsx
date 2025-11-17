import React from 'react';

export default function Footer() {
  return (
    <footer className="bg-primary text-white pt-8 pb-8  sm:px-12 lg:px-20">
      <div className="container mx-auto">
        <div className="grid grid-cols-2 md:grid-cols-3 gap-8 mb-8">
          
          

          <div className="space-y-4">
            <h3 className="text-lg font-bold text-secondary">Support</h3>
            <ul className="space-y-2">
              <li><a href="/contacto" className="text-gray-400 hover:text-secondary transition-colors">Contact us</a></li>
              <li><a href="/" className="text-gray-400 hover:text-secondary transition-colors">Shopping</a></li>
            </ul>
          </div>

          <div className="hidden md:block"></div>

          <div className="space-y-4 text-right">
            <h3 className="text-lg font-bold text-secondary">Social Media</h3>
            <div className="flex justify-end gap-3">
              <div className="w-8 h-8 bg-primary text-secondary ring-1 ring-secondary rounded-full">f</div>
              <div className="w-8 h-8 bg-primary text-secondary ring-1 ring-secondary rounded-full">X</div>
              <div className="w-8 h-8 bg-primary text-secondary ring-1 ring-secondary rounded-full">In</div>
              <div className="w-8 h-8 bg-primary text-secondary ring-1 ring-secondary rounded-full">Yt</div>
            </div>
          </div>
        </div>

        <div className="border-t border-gray-700 pt-6">
          <div className="flex flex-col md:flex-row justify-between items-center text-gray-500 text-sm">
            <p>&copy; {new Date().getFullYear()} SpringShop. Todos los derechos reservados.</p>
            <div className="flex gap-4 mt-4 md:mt-0">
              <a href="#" className="hover:text-secondary transition-colors">Terms of service</a>
              <a href="#" className="hover:text-secondary transition-colors">Privacy Policy</a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}