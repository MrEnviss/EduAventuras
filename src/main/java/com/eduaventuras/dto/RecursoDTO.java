package com.eduaventuras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String nombreArchivo;
    private Long materiaId;
    private String materiaNombre;
    private Long subidoPorId;
    private String subidoPorNombre;
    private LocalDateTime fechaSubida;
    private Long tamanioBytes;
    private Long cantidadDescargas;
}