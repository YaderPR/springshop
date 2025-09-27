package org.springshop.api.gateway;

import org.springshop.api.gateway.dto.PaymentChargeRequest;
import org.springshop.api.gateway.dto.PaymentGatewayResponse;

public interface PaymentGateway {

    /**
     * Procesa un cargo financiero a la pasarela de pago.
     * @param request Contiene el token de pago, monto, y moneda.
     * @return El resultado del intento de cargo.
     */
    PaymentGatewayResponse charge(PaymentChargeRequest request);
}
