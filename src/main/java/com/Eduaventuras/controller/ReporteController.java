package com.Eduaventuras.controller;

import com.Eduaventuras.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /**
     * GET /api/reportes/estadisticas/descargar
     * Descargar reporte completo de estadísticas en PDF (solo admin)
     */
    @GetMapping("/estadisticas/descargar")
    public ResponseEntity<?> descargarReporteEstadisticas() {
        try {
            byte[] pdfBytes = reporteService.generarReporteEstadisticas();

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "EduAventuras_Reporte_" + fecha + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", nombreArchivo);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar el reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/materia/{materiaId}/descargar
     * Descargar reporte de una materia específica en PDF
     */
    @GetMapping("/materia/{materiaId}/descargar")
    public ResponseEntity<?> descargarReporteMateria(@PathVariable Long materiaId) {
        try {
            byte[] pdfBytes = reporteService.generarReporteMateria(materiaId);

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "Reporte_Materia_" + materiaId + "_" + fecha + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", nombreArchivo);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar el reporte: " + e.getMessage()));
        }
    }
}