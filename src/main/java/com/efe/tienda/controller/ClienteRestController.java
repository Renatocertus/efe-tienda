package com.efe.tienda.controller;

import com.efe.tienda.dto.ClienteDTO;
import com.efe.tienda.model.Cliente;
import com.efe.tienda.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteRepository clienteRepository;

    public ClienteRestController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        try {
            return clienteRepository.findAll().stream()
                    .map(ClienteDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Error en GET /api/clientes: " + e.getMessage());
            return List.of();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ClienteDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody Cliente cliente) {
        if (cliente.getTipoDocumento() == null) cliente.setTipoDocumento("DNI");
        Cliente guardado = clienteRepository.save(cliente);
        return ResponseEntity.ok(new ClienteDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return clienteRepository.findById(id).map(c -> {
            if (body.containsKey("nombres"))    c.setNombres(body.get("nombres"));
            if (body.containsKey("apellidos"))  c.setApellidos(body.get("apellidos"));
            if (body.containsKey("correo"))     c.setCorreo(body.get("correo"));
            if (body.containsKey("telefono"))   c.setTelefono(body.get("telefono"));
            if (body.containsKey("direccion"))  c.setDireccion(body.get("direccion"));
            return ResponseEntity.ok(new ClienteDTO(clienteRepository.save(c)));
        }).orElse(ResponseEntity.notFound().build());
    }
}
