package com.eduaventuras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Long cantidadRecursos;
}