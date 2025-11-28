package com.eduaventuras.controller;

import com.eduaventuras.dto.LoginDTO;
import com.eduaventuras.dto.RegistroDTO;
import com.eduaventuras.dto.UsuarioDTO;
import com.eduaventuras.model.Rol;
import com.eduaventuras.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private com.eduaventuras.security.JwtUtil jwtUtil;

    /**
     * POST /api/usuarios/registro
     * Registrar un nuevo usuario
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            UsuarioDTO usuario = usuarioService.registrar(registroDTO);

            // Generar token JWT automáticamente después del registro
            String token= jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario registrado exitosamente");
            response.put("usuario", usuario);
            response.put("token", token);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/usuarios/login
     * Iniciar sesión - Devuelve token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            UsuarioDTO usuario = usuarioService.login(loginDTO);

            // COMENTAR JWT TEMPORALMENTE
              String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Login exitoso");
            response.put("usuario", usuario);
             response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }



    /**
     * GET /api/usuarios
     * Listar todos los usuarios (solo admin)
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/{id}
     * Buscar usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/usuarios/rol/{rol}
     * Listar usuarios por rol
     */
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(@PathVariable Rol rol) {
        List<UsuarioDTO> usuarios = usuarioService.listarPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * DELETE /api/usuarios/{id}
     * Eliminar usuario (solo admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/usuarios/estadisticas
     * Obtener estadísticas de usuarios
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalEstudiantes", usuarioService.contarPorRol(Rol.ESTUDIANTE));
        stats.put("totalDocentes", usuarioService.contarPorRol(Rol.DOCENTE));
        stats.put("totalAdmins", usuarioService.contarPorRol(Rol.ADMIN));
        return ResponseEntity.ok(stats);
    }
}