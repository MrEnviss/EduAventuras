package com.eduaventuras.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

/**
 * Configuración para servir archivos estáticos (fotos, documentos, etc.)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos desde la carpeta uploads
        String uploadsPath = Paths.get("src/main/resources/static/uploads/").toAbsolutePath().toUri().toString();

        System.out.println("📁 Configurando servicio de archivos estáticos...");
        System.out.println("📍 Ruta de uploads: " + uploadsPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsPath, "classpath:/static/uploads/")
                .setCachePeriod(3600); // Cache de 1 hora

        System.out.println("✅ Configuración de archivos estáticos completada");
    }
}