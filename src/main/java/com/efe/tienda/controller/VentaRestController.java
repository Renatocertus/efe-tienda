package com.efe.tienda.controller;

import com.efe.tienda.dto.ComprobanteDTO;
import com.efe.tienda.dto.PedidoDTO;
import com.efe.tienda.dto.VentaRequestDTO;
import com.efe.tienda.model.Comprobante;
import com.efe.tienda.model.Pedido;
import com.efe.tienda.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaService ventaService;

    public VentaRestController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarVenta(@RequestBody VentaRequestDTO dto) {
        try {
            Pedido pedido = ventaService.procesarVenta(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", pedido.getId(),
                    "codigo", pedido.getCodigo(),
                    "estado", pedido.getEstado(),
                    "total", pedido.getTotal(),
                    "subtotal", pedido.getSubtotal(),
                    "igv", pedido.getIgv(),
                    "mensaje", "Venta procesada exitosamente en MySQL"
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("❌ Error de validación en venta: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ ERROR INTERNO AL PROCESAR VENTA: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar la venta: " + e.getMessage()));
        }
    }

    @GetMapping("/pedidos")
    public List<PedidoDTO> listarPedidos(@RequestParam(required = false) Long clienteId) {
        List<Pedido> pedidos;
        if (clienteId != null) {
            pedidos = ventaService.listarPorCliente(clienteId);
        } else {
            pedidos = ventaService.listarTodos();
        }
        return pedidos.stream().map(PedidoDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<PedidoDTO> buscarPedidoPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(PedidoDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/pedidos/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new PedidoDTO(ventaService.cambiarEstado(id, nuevoEstado)));
    }

    @GetMapping("/comprobantes")
    public List<ComprobanteDTO> listarComprobantes() {
        return ventaService.listarComprobantes().stream()
                .map(ComprobanteDTO::new)
                .collect(Collectors.toList());
    }
}
