package com.Eduaventuras.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para documentar la API
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eduAventurasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EduAventuras API")
                        .description("API REST para plataforma educativa gratuita")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EduAventuras Team")
                                .email("info@eduaventuras.com")));
    }
}