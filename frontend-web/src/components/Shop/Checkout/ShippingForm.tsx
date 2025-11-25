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
    
    // Estados de la UI
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(true); // Controla si los campos están activos
    const [error, setError] = useState<string | null>(null);

    // --- CARGA DE DATOS ---
    useEffect(() => {
        const fetchLatestAddress = async () => {
            if (!userId) {
                setIsLoading(false);
                return;
            }

            try {
                const response = await getLastAddressByUser(userId);
                
                // Normalización de datos (si viene en array o objeto)
                let dataToUse = response;
                if (Array.isArray(response)) {
                    if (response.length > 0) dataToUse = response[0];
                    else {
                        // Si es array vacío, permitimos crear nueva
                        setIsEditing(true);
                        setIsLoading(false);
                        return;
                    }
                }

                // Si hay datos válidos, rellenamos y BLOQUEAMOS el formulario
                if (dataToUse && dataToUse.id) {
                    setAddress({
                        street: dataToUse.street || '',
                        state: dataToUse.state || '',
                        // Corrección aquí: miramos ambas posibilidades de nombre
                        zipCode: dataToUse.zipCode || dataToUse.zipCode || '', 
                        country: dataToUse.country || '',
                        city: dataToUse.city || '',
                        phoneNumber: dataToUse.phoneNumber || '',
                        userId: dataToUse.userId || userId
                    });

                    setExistingAddressId(dataToUse.id);
                    
                    // IMPORTANTE: Esto pone el formulario en "Modo Lectura"
                    setIsEditing(false); 
                    
                    // IMPORTANTE: ELIMINAMOS onShippingSubmit(dataToUse) DE AQUÍ
                    // Si lo llamamos, el padre cree que ya terminamos y oculta el form.
                    // El padre debe recibir los datos solo cuando el usuario haga clic en Guardar/Confirmar.
                } else {
                    setIsEditing(true);
                }

            } catch (err) {
                console.error("Error cargando dirección:", err);
                setIsEditing(true); // En caso de error, dejamos editar manual
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

    // Activa el modo edición
    const handleEditClick = (e: React.MouseEvent) => {
        e.preventDefault();
        setIsEditing(true); 
    };

    // Cancela la edición y vuelve a mostrar los datos guardados (Opcional pero recomendado UX)
    const handleCancelEdit = (e: React.MouseEvent) => {
        e.preventDefault();
        setIsEditing(false);
        // Aquí podrías volver a resetear el form con los datos originales si quisieras
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
            
            // AHORA SÍ notificamos al padre, porque el usuario hizo clic explícitamente
            onShippingSubmit(savedAddress);
            setIsEditing(false); 

        } catch (err: any) {
            console.error("Error al guardar:", err);
            setError(err.message || "Error al guardar la dirección.");
            onShippingSubmit(null);
        } finally {
            setIsSubmitting(false);
        }
    };

    // Opcional: Un botón para confirmar la dirección existente si no se quiere editar
    const handleConfirmExisting = (e: React.MouseEvent) => {
        e.preventDefault();
        // Construimos el objeto con los datos actuales del estado
        const currentData = { ...address, id: existingAddressId } as ShippingAddressResponse;
        onShippingSubmit(currentData);
    }

    if (isLoading) {
        return <div className="text-white text-center p-4">Cargando información de envío...</div>;
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-4 max-w-md mx-auto bg-gray-600/20 p-6 rounded-lg mb-8 shadow-[0_0_15px_rgba(137,254,0,.3)]">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-xl font-semibold text-secondary">Dirección de Envío</h3>
                {!isEditing && (
                    <span className="text-xs bg-lime-500/20 text-lime-400 border border-lime-500/50 px-2 py-1 rounded">
                        Dirección Guardada con éxito
                    </span>
                )}
            </div>

            {error && <div className="bg-red-500/20 text-red-200 p-2 rounded text-sm mb-4">{error}</div>}

            {/* CAMPOS DEL FORMULARIO */}
            <div>
                <label htmlFor="street" className="block text-sm font-medium text-gray-300 mb-1">Calle principal</label>
                <input
                    type="text" id="street" name="street"
                    value={address.street} onChange={handleChange}
                    required disabled={!isEditing}
                    className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                />
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label htmlFor="country" className="block text-sm font-medium text-gray-300 mb-1">País</label>
                    <input
                        type="text" id="country" name="country"
                        value={address.country} onChange={handleChange}
                        required disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                </div>
                <div>
                    <label htmlFor="state" className="block text-sm font-medium text-gray-300 mb-1">Departamento</label>
                    <input
                        type="text" id="state" name="state"
                        value={address.state} onChange={handleChange}
                        required disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label htmlFor="city" className="block text-sm font-medium text-gray-300 mb-1">Ciudad</label>
                    <input
                        type="text" id="city" name="city"
                        value={address.city} onChange={handleChange}
                        required disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                </div>
                <div>
                    <label htmlFor="zipCode" className="block text-sm font-medium text-gray-300 mb-1">Código postal</label>
                    <input
                        type="text" id="zipCode" name="zipCode"
                        value={address.zipCode} onChange={handleChange}
                        required disabled={!isEditing}
                        className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                </div>
            </div>

            <div>
                <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-300 mb-1">Teléfono</label>
                <input
                    type="tel" id="phoneNumber" name="phoneNumber"
                    value={address.phoneNumber || ''} onChange={handleChange}
                    disabled={!isEditing} placeholder='(Opcional)'
                    className="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white focus:ring-secondary focus:border-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                />
            </div>

            {/* BOTONES DE ACCIÓN */}
            <div className="flex gap-3 mt-6">
                {!isEditing ? (
                    <>
                        {/* Botón para editar la dirección existente */}
                        <button
                            onClick={handleEditClick}
                            className="flex-1 bg-gray-600 text-white font-bold py-2 rounded-full hover:bg-gray-500 transition-all border border-gray-500"
                        >
                            Editar
                        </button>
                        
                        {/* Botón para confirmar y usar esta dirección (envía al padre) */}
                        <button
                            onClick={handleConfirmExisting}
                            className="flex-1 bg-secondary text-primary font-bold py-2 rounded-full hover:bg-lime-400 transition-all shadow-[0_0_15px_rgba(137,254,0,.7)]"
                        >
                            Usar esta dirección
                        </button>
                    </>
                ) : (
                    <>
                        {/* Si estamos editando una existente, mostramos cancelar */}
                        {existingAddressId && (
                            <button
                                onClick={handleCancelEdit}
                                type="button"
                                className="px-4 bg-red-500/20 text-red-300 font-bold py-2 rounded-full hover:bg-red-500/40 transition-all border border-red-500/50"
                            >
                                Cancelar
                            </button>
                        )}
                        
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 bg-blue-600 text-white font-bold py-2 rounded-full hover:bg-blue-500 transition-all shadow-[0_0_15px_rgba(59,130,246,.5)]"
                        >
                            {isSubmitting ? "Guardando..." : (existingAddressId ? "Guardar Cambios" : "Crear Dirección")}
                        </button>
                    </>
                )}
            </div>
        </form>
    );
};

export default ShippingForm;