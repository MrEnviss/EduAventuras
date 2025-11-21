package com.Eduaventuras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "descargas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Descarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_descarga", nullable = false)
    private LocalDateTime fechaDescarga;

    @PrePersist
    protected void onCreate() {
        fechaDescarga = LocalDateTime.now();
    }
}