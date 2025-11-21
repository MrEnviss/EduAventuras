package com.Eduaventuras.dto;

import com.Eduaventuras.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}