package com.Eduaventuras.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * Configuración para almacenamiento de archivos
 */
@Configuration
public class FileStorageConfig {

    @Value("${app.upload.dir:uploads/recursos}")
    private String uploadDir;

    /**
     * Crear directorio de uploads si no existe
     */
    @PostConstruct
    public void init() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("✅ Directorio de uploads creado: " + uploadDir);
            } else {
                System.err.println("❌ No se pudo crear el directorio de uploads: " + uploadDir);
            }
        } else {
            System.out.println("✅ Directorio de uploads ya existe: " + uploadDir);
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}