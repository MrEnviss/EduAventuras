
package com.eduaventuras.model;

import com.eduaventuras.model.Materia;
import com.eduaventuras.model.Rol;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;

    // ===== CAMPOS NUEVOS PARA PERFIL =====

    @Column(name = "foto", length = 500)
    private String foto;  // Ruta relativa: /uploads/nombre_archivo.jpg

    @Column(name = "biografia", length = 500)
    private String biografia;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_favorita_id")
    private Materia materiaFavorita;

    // ===== CONSTRUCTORES =====

    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    // ===== GETTERS Y SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }

    public Materia getMateriaFavorita() { return materiaFavorita; }
    public void setMateriaFavorita(Materia materiaFavorita) { this.materiaFavorita = materiaFavorita; }

    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }
}