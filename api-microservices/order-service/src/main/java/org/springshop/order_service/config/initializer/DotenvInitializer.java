package org.springshop.order_service.config.initializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{
    
    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        // Carga las variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}