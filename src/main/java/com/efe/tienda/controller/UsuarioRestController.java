package com.efe.tienda.controller;

import com.efe.tienda.dto.LoginRequestDTO;
import com.efe.tienda.model.Rol;
import com.efe.tienda.model.Usuario;
import com.efe.tienda.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {
        return usuarioService.login(req.getUsername(), req.getPassword())
                .map(u -> ResponseEntity.ok(Map.of(
                        "mensaje", "Bienvenido " + u.getNombres(),
                        "usuario", u
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales incorrectas o usuario inactivo")));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario,
                                       @RequestParam(defaultValue = "ROLE_CLIENTE") String rol) {
        try {
            Usuario nuevo = usuarioService.registrarUsuario(usuario, rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/personal")
    public List<Usuario> listarPersonal() {
        return usuarioService.listarPersonal();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Usuario> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        return ResponseEntity.ok(usuarioService.actualizarEstado(id, estado));
    }

    @GetMapping("/roles")
    public List<Rol> listarRoles() {
        return usuarioService.listarRoles();
    }
}
