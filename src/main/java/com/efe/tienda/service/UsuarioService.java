package com.efe.tienda.service;

import com.efe.tienda.model.Rol;
import com.efe.tienda.model.Usuario;
import com.efe.tienda.repository.RolRepository;
import com.efe.tienda.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public Optional<Usuario> login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password) && "ACTIVO".equalsIgnoreCase(u.getEstado()));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPersonal() {
        return usuarioRepository.findByEsPersonalTrue();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El usuario o correo ya está registrado: " + usuario.getUsername());
        }

        Rol rol = rolRepository.findByNombre(nombreRol != null ? nombreRol : "ROLE_CLIENTE")
                .orElseGet(() -> rolRepository.save(new Rol(null, nombreRol, "Rol creado automáticamente")));

        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarEstado(Long id, String nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        usuario.setEstado(nuevoEstado);
        return usuarioRepository.save(usuario);
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }
}
