package com.efe.tienda.controller;

import com.efe.tienda.dto.MovimientoStockDTO;
import com.efe.tienda.dto.ProductoDTO;
import com.efe.tienda.dto.StockAjusteDTO;
import com.efe.tienda.model.Categoria;
import com.efe.tienda.model.Marca;
import com.efe.tienda.model.MovimientoStock;
import com.efe.tienda.model.Producto;
import com.efe.tienda.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoService productoService;

    public ProductoRestController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(@RequestParam(required = false) Long categoria,
                                                    @RequestParam(required = false) String buscar) {
        try {
            List<Producto> productos;
            if (categoria != null) {
                productos = productoService.buscarPorCategoria(categoria);
            } else if (buscar != null && !buscar.isBlank()) {
                productos = productoService.buscarPorTexto(buscar);
            } else {
                productos = productoService.listarTodos();
                if (productos.isEmpty()) {
                    System.out.println("🌱 Base de datos vacía. Sembrando 6 productos iniciales automáticamente...");
                    productos = productoService.inicializarCatalogoInicial();
                }
            }

            List<ProductoDTO> dtos = productos.stream()
                    .map(ProductoDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("❌ Error en GET /api/productos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ProductoDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bajo-stock")
    public List<ProductoDTO> listarBajoStock() {
        return productoService.listarBajoStock().stream()
                .map(ProductoDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(@RequestBody Producto producto) {
        return ResponseEntity.ok(new ProductoDTO(productoService.guardarOActualizar(producto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return productoService.buscarPorId(id).map(p -> {
            if (body.containsKey("nombre")) p.setNombre(String.valueOf(body.get("nombre")));
            if (body.containsKey("precio")) p.setPrecio(new java.math.BigDecimal(String.valueOf(body.get("precio"))));
            if (body.containsKey("oferta")) p.setOferta(new java.math.BigDecimal(String.valueOf(body.get("oferta"))));
            if (body.containsKey("stock")) p.setStock(Integer.parseInt(String.valueOf(body.get("stock"))));
            if (body.containsKey("modelo")) p.setModelo(String.valueOf(body.get("modelo")));
            if (body.containsKey("sku")) p.setSku(String.valueOf(body.get("sku")));
            if (body.containsKey("descripcion")) p.setDescripcion(String.valueOf(body.get("descripcion")));
            return ResponseEntity.ok(new ProductoDTO(productoService.guardarOActualizar(p)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return productoService.buscarPorId(id).map(p -> {
            p.setActivo(false);
            productoService.guardarOActualizar(p);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/stock/ajuste")
    public ResponseEntity<MovimientoStockDTO> ajustarStock(@RequestBody StockAjusteDTO dto) {
        return ResponseEntity.ok(new MovimientoStockDTO(productoService.registrarAjusteStock(dto)));
    }

    @GetMapping("/stock/movimientos")
    public List<MovimientoStockDTO> listarMovimientos(@RequestParam(required = false) Long productoId) {
        return productoService.listarMovimientosStock(productoId).stream()
                .map(MovimientoStockDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/categorias")
    public List<Categoria> categorias() {
        return productoService.listarCategorias();
    }

    @GetMapping("/marcas")
    public List<Marca> marcas() {
        return productoService.listarMarcas();
    }
}
