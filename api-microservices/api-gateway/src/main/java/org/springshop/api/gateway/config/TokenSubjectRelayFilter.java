package org.springshop.api.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class TokenSubjectRelayFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
            exchange.getPrincipal()
                .flatMap(principal -> {
                    if (principal instanceof JwtAuthenticationToken jwtAuth) {
                        String subject = jwtAuth.getToken().getSubject();
                        // Clonar request con nuevo header
                        ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r.headers(h -> h.set("X-Auth-Subject", subject)))
                            .build();
                        return chain.filter(mutated);
                    }
                    return chain.filter(exchange);
                });
    }
}
