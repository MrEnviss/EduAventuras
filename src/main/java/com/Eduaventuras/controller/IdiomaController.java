package com.Eduaventuras.controller;

import com.Eduaventuras.service.InternacionalizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/idioma")
@CrossOrigin(origins = "*")
public class IdiomaController {

    @Autowired
    private InternacionalizacionService i18nService;

    /**
     * GET /api/idioma/mensajes?lang=es
     * Obtener todos los mensajes en un idioma específico
     */
    @GetMapping("/mensajes")
    public ResponseEntity<Map<String, String>> obtenerMensajes(
            @RequestParam(defaultValue = "es") String lang) {

        Map<String, String> mensajes = i18nService.obtenerMensajes(lang);
        return ResponseEntity.ok(mensajes);
    }

    /**
     * GET /api/idioma/idiomas-disponibles
     * Obtener lista de idiomas soportados
     */
    @GetMapping("/idiomas-disponibles")
    public ResponseEntity<Map<String, String>> obtenerIdiomasDisponibles() {
        Map<String, String> idiomas = Map.of(
                "es", "Español",
                "en", "English"
        );
        return ResponseEntity.ok(idiomas);
    }
}