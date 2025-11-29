package com.eduaventuras.service;

import com.eduaventuras.dto.ActualizarPerfilDTO;
import com.eduaventuras.dto.LoginDTO;
import com.eduaventuras.dto.RegistroDTO;
import com.eduaventuras.dto.UsuarioDTO;
import com.eduaventuras.model.Materia;
import com.eduaventuras.model.Rol;
import com.eduaventuras.model.Usuario;
import com.eduaventuras.repository.MateriaRepository;
import com.eduaventuras.repository.UsuarioRepository;
import com.eduaventuras.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    // Almacenamiento temporal de tokens de recuperación (en producción usar Redis o BD)
    private Map<String, TokenRecuperacion> tokensRecuperacion = new HashMap<>();

    /**
     * Buscar usuario por email
     */
    public UsuarioDTO buscarPorEmail(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el email: " + email);
        }

        Usuario usuario = usuarioOpt.get();
        return convertirADTO(usuario);
    }

    /**
     * Actualizar perfil (nombre y apellido)
     */
    public UsuarioDTO actualizarPerfil(String email, ActualizarPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos básicos
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());

        // Actualizar biografía
        usuario.setBiografia(dto.getBiografia());

        // Actualizar materia favorita
        if (dto.getMateriaFavoritaId() != null) {
            Materia materiaFavorita = materiaRepository.findById(dto.getMateriaFavoritaId())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
            usuario.setMateriaFavorita(materiaFavorita);
        } else {
            usuario.setMateriaFavorita(null);
        }

        usuario.setUltimaActualizacion(LocalDateTime.now());
        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirADTO(actualizado);
    }

    /**
     * Actualizar la foto de perfil del usuario
     */
    public void actualizarFotoPerfil(String email, String rutaFoto) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.setFoto(rutaFoto);
        usuarioRepository.save(usuario);

        System.out.println("✅ Foto actualizada para: " + email + " -> " + rutaFoto);
    }

    /**
     * Obtener la ruta de la foto del usuario por ID
     */
    public String obtenerRutaFotoPerfil(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElse(null);

            if (usuario == null) {
                System.out.println("⚠️ Usuario no encontrado: " + usuarioId);
                return null;
            }

            System.out.println("📸 Foto del usuario " + usuarioId + ": " + usuario.getFoto());
            return usuario.getFoto();

        } catch (Exception e) {
            System.err.println("❌ Error al obtener ruta de foto: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtener la ruta de la foto del usuario por email
     */
    public String obtenerRutaFotoDelEmail(String email) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElse(null);

            if (usuario == null) {
                System.out.println("⚠️ Usuario no encontrado: " + email);
                return null;
            }

            return usuario.getFoto();

        } catch (Exception e) {
            System.err.println("❌ Error al obtener ruta de foto: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtener foto de perfil (como byte[]) - Mantener para compatibilidad
     */
    public byte[] obtenerFotoPerfil(Long usuarioId) throws IOException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el ID: " + usuarioId);
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getFoto() == null || usuario.getFoto().isEmpty()) {
            return new byte[0];
        }

        // Construir ruta del archivo
        String nombreArchivo = usuario.getFoto().replace("/uploads/", "").replace("uploads/", "");
        Path filePath = Paths.get(uploadDir).resolve(nombreArchivo);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("No se encontró la foto de perfil");
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Eliminar foto de perfil
     */
    public void eliminarFotoPerfil(String email) throws IOException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getFoto() != null && !usuario.getFoto().isEmpty()) {
            // Eliminar archivo físico
            String nombreArchivo = usuario.getFoto().replace("/uploads/", "").replace("uploads/", "");
            Path filePath = Paths.get(uploadDir).resolve(nombreArchivo);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("✅ Archivo eliminado: " + filePath.toAbsolutePath());
            }

            // Actualizar usuario
            usuario.setFoto(null);
            usuarioRepository.save(usuario);
        }

        System.out.println("✅ Foto de perfil eliminada para: " + email);
    }

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
     * Cambiar contraseña (usuario logueado)
     */
    public void cambiarPassword(String email, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la contraseña actual sea correcta
        if (!PasswordUtil.verificar(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(PasswordUtil.encriptar(passwordNueva));
        usuarioRepository.save(usuario);
    }

    /**
     * Verificar si un email existe (para recuperación de contraseña)
     */
    public void verificarEmailExiste(String email) {
        usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));
    }

    /**
     * Generar token de recuperación de contraseña
     */
    public String generarTokenRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));

        // Generar token único
        String token = UUID.randomUUID().toString();

        // Guardar token con fecha de expiración (1 hora)
        TokenRecuperacion tokenRecuperacion = new TokenRecuperacion();
        tokenRecuperacion.setEmail(email);
        tokenRecuperacion.setToken(token);
        tokenRecuperacion.setFechaExpiracion(LocalDateTime.now().plusHours(1));

        tokensRecuperacion.put(token, tokenRecuperacion);

        return token;
    }

    /**
     * Restablecer contraseña con token de recuperación
     */
    public void restablecerPasswordConToken(String token, String nuevaPassword) {
        TokenRecuperacion tokenRecuperacion = tokensRecuperacion.get(token);

        if (tokenRecuperacion == null) {
            throw new RuntimeException("Token inválido o expirado");
        }

        // Verificar que el token no haya expirado
        if (LocalDateTime.now().isAfter(tokenRecuperacion.getFechaExpiracion())) {
            tokensRecuperacion.remove(token);
            throw new RuntimeException("Token expirado");
        }

        // Buscar usuario y actualizar contraseña
        Usuario usuario = usuarioRepository.findByEmail(tokenRecuperacion.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(PasswordUtil.encriptar(nuevaPassword));
        usuarioRepository.save(usuario);

        // Eliminar token usado
        tokensRecuperacion.remove(token);
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
     * Eliminar usuario permanentemente
     */
    public void eliminarPermanente(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar físicamente de la base de datos
        usuarioRepository.delete(usuario);
    }

    /**
     * Cambiar estado (activo/inactivo) de usuario
     */
    public UsuarioDTO cambiarEstado(Long id, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(activo);
        Usuario actualizado = usuarioRepository.save(usuario);

        return convertirADTO(actualizado);
    }

    /**
     * Cambiar rol de usuario
     */
    public UsuarioDTO cambiarRol(Long id, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setRol(nuevoRol);
        Usuario actualizado = usuarioRepository.save(usuario);

        return convertirADTO(actualizado);
    }

    /**
     * Desactivar usuario
     */
    public void desactivar(Long id) {
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
     * Contar todos los usuarios (para dashboard)
     */
    public long contarTodosLosUsuarios() {
        return usuarioRepository.count();
    }

    /**
     * Obtener usuarios recientes (para dashboard)
     */
    public List<UsuarioDTO> obtenerUsuariosRecientes(int limite) {
        return usuarioRepository.findAll().stream()
                .sorted((u1, u2) -> u2.getFechaRegistro().compareTo(u1.getFechaRegistro()))
                .limit(limite)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    public boolean validarTokenRecuperacion(String token) {
        TokenRecuperacion tokenRecuperacion = tokensRecuperacion.get(token);

        if (tokenRecuperacion == null) {
            return false;
        }

        // Verificar que no haya expirado
        if (LocalDateTime.now().isAfter(tokenRecuperacion.getFechaExpiracion())) {
            tokensRecuperacion.remove(token);
            return false;
        }

        return true;
    }

    /**
     * Obtener email asociado a un token
     */
    public String obtenerEmailPorToken(String token) {
        TokenRecuperacion tokenRecuperacion = tokensRecuperacion.get(token);
        return tokenRecuperacion != null ? tokenRecuperacion.getEmail() : null;
    }
    /**
     * Convertir entidad Usuario a DTO
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

        // ✅ NUEVOS CAMPOS - IMPORTANTE!
        dto.setFoto(usuario.getFoto());  // ← ESTO ES LO QUE FALTABA
        dto.setBiografia(usuario.getBiografia());
        dto.setUltimaActualizacion(usuario.getUltimaActualizacion());

        // Materia favorita
        if (usuario.getMateriaFavorita() != null) {
            dto.setMateriaFavoritaId(usuario.getMateriaFavorita().getId());
            dto.setMateriaFavoritaNombre(usuario.getMateriaFavorita().getNombre());
        }

        System.out.println("✅ DTO creado - Foto: " + dto.getFoto());

        return dto;
    }

    /**
     * Clase interna para tokens de recuperación
     */
    private static class TokenRecuperacion {
        private String email;
        private String token;
        private LocalDateTime fechaExpiracion;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
        public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
            this.fechaExpiracion = fechaExpiracion;
        }
    }
}