package com.Eduaventuras.service;

import com.Eduaventuras.dto.LoginDTO;
import com.Eduaventuras.dto.RegistroDTO;
import com.Eduaventuras.dto.UsuarioDTO;
import com.Eduaventuras.model.Rol;
import com.Eduaventuras.model.Usuario;
import com.Eduaventuras.repository.UsuarioRepository;
import com.Eduaventuras.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Registrar un nuevo usuario
     */
    public UsuarioDTO registrar(RegistroDTO registroDTO) {
        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(PasswordUtil.encriptar(registroDTO.getPassword()));
        usuario.setRol(registroDTO.getRol());
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }

    /**
     * Login de usuario
     */
    public UsuarioDTO login(LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!PasswordUtil.verificar(loginDTO.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return convertirADTO(usuario);
    }

    /**
     * Listar todos los usuarios
     */
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar usuario por ID
     */
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(usuario);
    }

    /**
     * Listar usuarios por rol
     */
    public List<UsuarioDTO> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Eliminar usuario (solo admin)
     */
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Contar usuarios por rol
     */
    public long contarPorRol(Rol rol) {
        return usuarioRepository.countByRol(rol);
    }

    /**
     * Convertir entidad a DTO
     */
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setActivo(usuario.getActivo());
        return dto;
    }
}