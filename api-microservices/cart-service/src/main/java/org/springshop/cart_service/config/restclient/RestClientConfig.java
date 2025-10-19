package org.springshop.cart_service.config.restclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Marks the class as a source of bean definitions
public class RestClientConfig {

    /**
     * Define RestTemplate as a Spring Bean so it can be injected 
     * into other components (like UserClient).
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
