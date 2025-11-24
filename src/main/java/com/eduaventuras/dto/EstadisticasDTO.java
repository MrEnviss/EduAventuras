package com.eduaventuras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * DTO para reportes y estadísticas del sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {

    // Estadísticas de usuarios
    private Long totalUsuarios;
    private Long totalEstudiantes;
    private Long totalDocentes;
    private Long totalAdmins;

    // Estadísticas de recursos
    private Long totalRecursos;
    private Map<String, Long> recursosPorMateria; // {"Matemáticas": 15, "Español": 10}

    // Estadísticas de descargas
    private Long totalDescargas;
    private Map<String, Long> descargasPorMateria;

    // Top recursos más descargados
    private Map<String, Long> topRecursosMasDescargados; // {"titulo": cantidadDescargas}
}