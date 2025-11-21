package com.Eduaventuras.repository;


import com.Eduaventuras.model.Rol;
import com.Eduaventuras.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByActivo(Boolean activo);
    long countByRol(Rol rol);
}