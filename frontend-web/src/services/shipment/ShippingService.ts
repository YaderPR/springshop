export interface ShippingAddressRequest {
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
    phoneNumber?: string;
    userId: number;
}

export interface ShippingAddressResponse extends ShippingAddressRequest {
    id: number;
}

const API_SHIPPING_URL = "http://localhost:8080/api/v2/addresses";

export async function createShippingAddress (
    addressData: ShippingAddressRequest
): Promise<ShippingAddressResponse> {
    const payload = { ...addressData, userId: 1}

    const response = await fetch(API_SHIPPING_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        const errorBody = await response.json().catch(() => ({ message: 'Error desconocido al crear dirección' }));
        console.error("Error al crear dirección de envío:", errorBody);
        throw new Error(errorBody.message ||`Error al crear dirección: ${response.status}`);
    }
    return response.json();
}