package com.eduaventuras.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utilidad para cargar mensajes de internacionalización
 */
public class MessageLoader {

    /**
     * Cargar mensajes desde archivo.properties
     */
    public static Map<String, String> cargarMensajes(String idioma) {
        Map<String, String> mensajes = new HashMap<>();

        // Validar idioma (solo español e inglés soportados)
        if (!idioma.equals("es") && !idioma.equals("en") && !idioma.equals("fr")) {
            idioma = "es"; // Por defecto español
        }

        String nombreArchivo = "messages_" + idioma + ".properties";

        try (InputStream input = MessageLoader.class.getClassLoader()
                .getResourceAsStream(nombreArchivo)) {

            if (input == null) {
                System.err.println(" No se pudo encontrar el archivo: " + nombreArchivo);
                return mensajesDefault();
            }

            Properties prop = new Properties();
            prop.load(input);

            // Convertir Properties a Map
            for (String key : prop.stringPropertyNames()) {
                mensajes.put(key, prop.getProperty(key));
            }

            System.out.println("Mensajes cargados para idioma: " + idioma);

        } catch (IOException ex) {
            System.err.println("Error al cargar mensajes: " + ex.getMessage());
            return mensajesDefault();
        }

        return mensajes;
    }

    /**
     * Mensajes por defecto si no se pueden cargar los archivos
     */
    private static Map<String, String> mensajesDefault() {
        Map<String, String> mensajes = new HashMap<>();
        mensajes.put("app.nombre", "EduAventuras");
        mensajes.put("app.bienvenida", "Bienvenido a EduAventuras");
        mensajes.put("error.general", "Ha ocurrido un error");
        return mensajes;
    }
}