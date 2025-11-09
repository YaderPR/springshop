import React, { useState } from 'react';
import { createShippingAddress } from '../../../services/shipment/ShippingService';
import type { ShippingAddressResponse } from '../../../services/shipment/ShippingService';

interface ShippingAddressFormState {
    
    street: string;
    country: string;
    state: string;
    zipCode: string;
    country: string;
    phoneNumber?: string;
    userId: number;
}

interface ShippingFormProps {
  onShippingSubmit: (savedAddress: ShippingAddressResponse | null) => void; 
}

const initialAddress: ShippingAddress = {
    
    street: '',
    state: '',
    zipCode: '',
    country: '',
    phoneNumber: '',
};

const ShippingForm: React.FC<ShippingFormProps> = ({ onShippingSubmit }) => {
  const [address, setAddress] = useState<ShippingAddressFormState>(initialAddress);
  const [isSubmitting, setIsSubmitting] = useState(false); 
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setAddress(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    const addressPayload: Omit<ShippingAddressRequest, 'userId'> = {
        street: address.street,
        country: address.country,
        city: address.city,
        state: address.state,
        zipCode: address.zipCode,
        phoneNumber: address.phoneNumber || undefined,
    };

    try {
        const savedAddress = await createShippingAddress(addressPayload as ShippingAddressRequest);
        console.log("Dirección de envío guardada:", savedAddress);
        onShippingSubmit(savedAddress); 
        // setIsSubmitting(false);

    } catch (err: any) {
        console.error("Error al guardar la dirección de envío:", err);
        setError(err.message || "Ocurrió un error al guardar la dirección.");
        onShippingSubmit(null);
    } finally {
        setIsSubmitting(false);
    }
  };

  //                                                                  shadow-[0_0_15px_rgba(137,254,0,.7)]
  return (

    <form onSubmit={handleSubmit} className="space-y-4 max-w-md mx-auto bg-gray-800 p-6 rounded-lg mb-8 ">
      <h3 className="text-xl font-semibold text-secondary mb-4">Dirección de Envío</h3>

      <div>
        <label htmlFor="street" className="block text-sm font-medium text-gray-300 mb-1">Calle principal</label>
        <input 
            type="text" 
            id="street" 
            name="street" 
            value={address.street} 
            onChange={handleChange} 
            required
            placeholder='calle principal, avenida...'
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary"
        />
      </div>
      
      <div className="grid grid-cols-2 gap-4">
        <div>
        <label htmlFor="country" className="block text-sm font-medium text-gray-300 mb-1">País</label>
        <input
          type="text"
          id="country"
          name="country"
          value={address.country}
          onChange={handleChange}
          required
          placeholder="Nicaragua"
          className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary"
        />
      </div>
      
      <div>
        <label htmlFor="state" className="block text-sm font-medium text-gray-300 mb-1">Departamento </label>
        <input
          type="text"
          id="state"
          name="state"
          value={address.state}
            required
            placeholder='León'
          onChange={handleChange}
          className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary"
        />
      </div>
      </div>
      

      {/* Ciudad y Estado/Provincia           grid grid-cols-2 gap-4 */}
      <div className="grid grid-cols-2 gap-4">
        <div className="block text-sm font-medium text-gray-300 mb-1">
          <label htmlFor="city" className="block text-sm font-medium text-gray-300 mb-1">Ciudad</label>
          <input
            type="text"
            id="city"
            name="city"
            value={address.city}
            onChange={handleChange}
            required
            placeholder='Managua, Chinandega...'
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary"
          />
        </div>

        <div className="block text-sm font-medium text-gray-300 mb-1">
          <label htmlFor="zipCode" className="block text-sm font-medium text-gray-300 mb-1">Código postal</label>
          <input
            type="text"
            id="zipCode"
            name="zipCode"
            value={address.zipCode}
            onChange={handleChange}
            required
            placeholder='00000'
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary"
          />
        </div>
      </div>
      
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-300 mb-1">Teléfono </label>
          <input
            type="tel"
            id="phoneNumber"
            name="phoneNumber"
            value={address.phoneNumber}
            onChange={handleChange}
            placeholder='(Opcional)'
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary"
          />
        </div>
      </div>

      {/* Botón de Envío */}
      <button 
        type="submit"
        disabled={isSubmitting}
        className="w-full mt-4 bg-secondary text-primary font-bold py-2 rounded-full hover:bg-lime-400 transition-all disabled:opacountry-50 shadow-[0_0_15px_rgba(137,254,0,.7)]"
      >
        {isSubmitting ? "Guardando..." : "Guardar Dirección"}
      </button>
    </form>
  );
};

export default ShippingForm;