package com.eduaventuras.dto;

import com.eduaventuras.model.Rol;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private LocalDateTime fechaRegistro;
    private Boolean activo;

    // ===== NUEVOS CAMPOS =====
    private String foto;
    private String biografia;
    private LocalDateTime ultimaActualizacion;
    private Long materiaFavoritaId;
    private String materiaFavoritaNombre;

    // ===== CONSTRUCTORES =====

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombre, String apellido, String email, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    // ===== GETTERS Y SETTERS =====

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                ", foto='" + foto + '\'' +
                ", biografia='" + biografia + '\'' +
                ", materiaFavoritaNombre='" + materiaFavoritaNombre + '\'' +
                '}';
    }
}