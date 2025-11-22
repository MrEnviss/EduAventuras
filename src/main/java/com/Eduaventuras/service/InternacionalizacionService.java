package com.Eduaventuras.service;

import com.Eduaventuras.util.MessageLoader;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio para internacionalización (i18n)
 */
@Service
public class InternacionalizacionService {

    /**
     * Obtener todos los mensajes en un idioma específico
     */
    public Map<String, String> obtenerMensajes(String idioma) {
        return MessageLoader.cargarMensajes(idioma);
    }

    /**
     * Obtener un mensaje específico en un idioma
     */
    public String obtenerMensaje(String clave, String idioma) {
        Map<String, String> mensajes = MessageLoader.cargarMensajes(idioma);
        return mensajes.getOrDefault(clave, clave);
    }

    /**
     * Obtener mensajes con valores reemplazados
     * Ejemplo: obtenerMensaje("bienvenida", "es", Map.of("nombre", "Juan"))
     * Si messages_es.properties tiene: bienvenida=Hola {nombre}
     * Resultado: "Hola Juan"
     */
    public String obtenerMensajeConParametros(String clave, String idioma, Map<String, String> parametros) {
        String mensaje = obtenerMensaje(clave, idioma);

        for (Map.Entry<String, String> entry : parametros.entrySet()) {
            mensaje = mensaje.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return mensaje;
    }
}