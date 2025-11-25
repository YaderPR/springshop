import React, { useState, useEffect } from 'react';
import { 
    createShippingAddress, 
    getLastAddressByUser, 
    updateShippingAddress 
} from '../../../services/shipment/ShippingService';
import type { ShippingAddressRequest, ShippingAddressResponse } from '../../../services/shipment/ShippingService';

interface ShippingAddressFormState {
    street: string;
    state: string;
    zipCode: string;
    country: string;
    city: string;
    phoneNumber?: string;
    userId: number;
}

interface ShippingFormProps {
    onShippingSubmit: (savedAddress: ShippingAddressResponse | null) => void;
}

const ShippingForm: React.FC<ShippingFormProps> = ({ onShippingSubmit }) => {
    // Movemos esto dentro para asegurar que lea el valor actual al montar
    const userId = parseInt(localStorage.getItem('app_user_id') || '0');

    const initialAddress: ShippingAddressFormState = {
        street: '',
        state: '',
        zipCode: '',
        country: '',
        city: '',
        phoneNumber: '',
        userId: userId || 0,
    };

    const [address, setAddress] = useState<ShippingAddressFormState>(initialAddress);
    const [existingAddressId, setExistingAddressId] = useState<number | null>(null);
    
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // --- AQUÍ ESTÁ LA CORRECCIÓN PRINCIPAL ---
   useEffect(() => {
        const fetchLatestAddress = async () => {
            // 1. Validar Usuario
            if (!userId) {
                console.warn("No hay userId, cancelando carga.");
                setIsLoading(false);
                return;
            }

            try {
                const response = await getLastAddressByUser(userId);
                console.log("1. Respuesta cruda API:", response);

                // 2. Normalización de datos (Manejo de Array vs Objeto)
                let dataToUse = response;

                // Si es un array (ej: devuelve una lista aunque sea 'latest')
                if (Array.isArray(response)) {
                    if (response.length > 0) {
                        dataToUse = response[0]; // Usamos el primero
                    } else {
                        console.log("2. Array vacío: No hay dirección previa.");
                        setIsEditing(true); // Habilitar formulario para crear
                        setIsLoading(false);
                        return; // Salimos
                    }
                }

                // 3. Verificación de integridad (¿Tiene ID?)
                // A veces Spring devuelve un objeto vacío {} o con campos nulos
                if (!dataToUse || !dataToUse.id) {
                    console.warn("3. Objeto recibido no tiene ID válido:", dataToUse);
                    setIsEditing(true);
                    setIsLoading(false);
                    return;
                }

                console.log("4. Cargando dirección en formulario:", dataToUse);

                // 4. Mapeo seguro (evita undefined)
                setAddress({
                    street: dataToUse.street || '',
                    state: dataToUse.state || '',
                    // Intenta leer zipCode (camelCase) o zip_code (snake_case)
                    zipCode: dataToUse.zipCode || dataToUse.zipCode || '', 
                    country: dataToUse.country || '',
                    city: dataToUse.city || '',
                    phoneNumber: dataToUse.phoneNumber || '',
                    userId: dataToUse.userId || userId
                });

                setExistingAddressId(dataToUse.id);
                
                // 5. ¡AQUÍ ESTÁ LA CLAVE! Forzamos el modo lectura
                setIsEditing(false); 
                
                // Notificar al padre
                onShippingSubmit(dataToUse);

            } catch (err) {
                console.error("Error crítico cargando dirección:", err);
                // Solo si hay error habilitamos edición para que el usuario no se bloquee
                setIsEditing(true); 
            } finally {
                setIsLoading(false);
            }
        };

        fetchLatestAddress();
    }, [userId]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setAddress(prev => ({ ...prev, [name]: value }));
    };

    const handleEditClick = (e: React.MouseEvent) => {
        e.preventDefault();
        setIsEditing(true); 
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        setError(null);

        const addressPayload: ShippingAddressRequest = {
            street: address.street,
            country: address.country,
            city: address.city,
            state: address.state,
            zipCode: address.zipCode,
            phoneNumber: address.phoneNumber || undefined,
            userId: address.userId
        };

        try {
            let savedAddress;
            if (existingAddressId) {
                savedAddress = await updateShippingAddress(existingAddressId, addressPayload);
            } else {
                savedAddress = await createShippingAddress(addressPayload);
                setExistingAddressId(savedAddress.id);
            }
            
            console.log("Dirección guardada/actualizada:", savedAddress);
            onShippingSubmit(savedAddress);
            setIsEditing(false); // Vuelve a bloquear tras guardar

        } catch (err: any) {
            console.error("Error al guardar:", err);
            setError(err.message || "Error al guardar la dirección.");
            onShippingSubmit(null);
        } finally {
            setIsSubmitting(false);
        }
    };

    if (isLoading) {
        return <div className="text-white text-center p-4">Cargando información de envío...</div>;
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-4 max-w-md mx-auto bg-gray-800 p-6 rounded-lg mb-8 shadow-[0_0_15px_rgba(137,254,0,.7)]">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-xl font-semibold text-secondary">Dirección de Envío</h3>
                {!isEditing && (
                    <span className="text-xs bg-lime-500/20 text-lime-400 border border-lime-500/50 px-2 py-1 rounded">Guardada</span>
                )}
            </div>

            {error && <div className="bg-red-500/20 text-red-200 p-2 rounded text-sm mb-4">{error}</div>}

            {/* Campos del formulario */}
            <div>
                <label htmlFor="street" className="block text-sm font-medium text-gray-300 mb-1">Calle principal</label>
                <input
                    type="text"
                    id="street"
                    name="street"
                    value={address.street}
                    onChange={handleChange}
                    required
                    disabled={!isEditing}
                    className={`w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-60 disabled:cursor-not-allowed`}
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
                        disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-60 disabled:cursor-not-allowed"
                    />
                </div>
                <div>
                    <label htmlFor="state" className="block text-sm font-medium text-gray-300 mb-1">Departamento</label>
                    <input
                        type="text"
                        id="state"
                        name="state"
                        value={address.state}
                        required
                        disabled={!isEditing}
                        onChange={handleChange}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-60 disabled:cursor-not-allowed"
                    />
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label htmlFor="city" className="block text-sm font-medium text-gray-300 mb-1">Ciudad</label>
                    <input
                        type="text"
                        id="city"
                        name="city"
                        value={address.city}
                        onChange={handleChange}
                        required
                        disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-60 disabled:cursor-not-allowed"
                    />
                </div>
                <div>
                    <label htmlFor="zipCode" className="block text-sm font-medium text-gray-300 mb-1">Código postal</label>
                    <input
                        type="text"
                        id="zipCode"
                        name="zipCode"
                        value={address.zipCode}
                        onChange={handleChange}
                        required
                        disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-60 disabled:cursor-not-allowed"
                    />
                </div>
            </div>

            <div>
                <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-300 mb-1">Teléfono</label>
                <input
                    type="tel"
                    id="phoneNumber"
                    name="phoneNumber"
                    value={address.phoneNumber || ''}
                    onChange={handleChange}
                    disabled={!isEditing}
                    placeholder='(Opcional)'
                    className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-60 disabled:cursor-not-allowed"
                />
            </div>

            {/* Botones: Editar o Guardar */}
            {!isEditing ? (
                 <button
                 onClick={handleEditClick}
                 className="w-full mt-4 bg-blue-600 text-white font-bold py-2 rounded-full hover:bg-blue-500 transition-all shadow-[0_0_15px_rgba(59,130,246,.5)]"
               >
                 Editar Dirección
               </button>
            ) : (
                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="w-full mt-4 bg-secondary text-primary font-bold py-2 rounded-full hover:bg-lime-400 transition-all disabled:opacity-50 shadow-[0_0_15px_rgba(137,254,0,.7)]"
                >
                    {isSubmitting ? "Guardando..." : (existingAddressId ? "Actualizar Dirección" : "Guardar Dirección")}
                </button>
            )}
        </form>
    );
};

export default ShippingForm;