package com.efe.tienda.service;

import com.efe.tienda.dto.StockAjusteDTO;
import com.efe.tienda.model.Categoria;
import com.efe.tienda.model.Marca;
import com.efe.tienda.model.MovimientoStock;
import com.efe.tienda.model.Producto;
import com.efe.tienda.repository.CategoriaRepository;
import com.efe.tienda.repository.MarcaRepository;
import com.efe.tienda.repository.MovimientoStockRepository;
import com.efe.tienda.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final MovimientoStockRepository movimientoStockRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           MarcaRepository marcaRepository,
                           MovimientoStockRepository movimientoStockRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.movimientoStockRepository = movimientoStockRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    public List<Producto> buscarPorTexto(String query) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(query);
    }

    public List<Producto> listarBajoStock() {
        return productoRepository.findProductosConBajoStock();
    }

    @Transactional
    public Producto guardarOActualizar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public MovimientoStock registrarAjusteStock(StockAjusteDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + dto.getProductoId()));

        int stockActual = producto.getStock();
        int nuevoStock = stockActual;

        if ("ENTRADA".equalsIgnoreCase(dto.getTipo())) {
            nuevoStock = stockActual + dto.getCantidad();
        } else if ("SALIDA".equalsIgnoreCase(dto.getTipo())) {
            if (stockActual < dto.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente. Stock actual: " + stockActual + ", solicitado: " + dto.getCantidad());
            }
            nuevoStock = stockActual - dto.getCantidad();
        } else if ("AJUSTE".equalsIgnoreCase(dto.getTipo())) {
            nuevoStock = dto.getCantidad();
        }

        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProducto(producto);
        movimiento.setTipo(dto.getTipo().toUpperCase());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setStockAnterior(stockActual);
        movimiento.setStockPosterior(nuevoStock);
        movimiento.setMotivo(dto.getMotivo() != null ? dto.getMotivo() : "Ajuste manual de inventario");
        movimiento.setUsuarioResponsable(dto.getUsuarioResponsable() != null ? dto.getUsuarioResponsable() : "Admin");
        movimiento.setFechaMovimiento(LocalDateTime.now());

        return movimientoStockRepository.save(movimiento);
    }

    public List<MovimientoStock> listarMovimientosStock(Long productoId) {
        if (productoId != null) {
            return movimientoStockRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId);
        }
        return movimientoStockRepository.findTop50ByOrderByFechaMovimientoDesc();
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findByActivoTrue();
    }

    public List<Marca> listarMarcas() {
        return marcaRepository.findByActivoTrue();
    }

    @Transactional
    public synchronized List<Producto> inicializarCatalogoInicial() {
        if (productoRepository.count() > 0) {
            return productoRepository.findAll();
        }

        // 1. Categorías seguras
        Categoria catCelulares = categoriaRepository.findByNombre("Celulares")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Celulares", "📱")));
        Categoria catLaptops = categoriaRepository.findByNombre("Laptops")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Laptops", "💻")));
        Categoria catTVs = categoriaRepository.findByNombre("Televisores")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Televisores", "📺")));
        Categoria catElectro = categoriaRepository.findByNombre("Electrohogar")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Electrohogar", "🧊")));
        Categoria catAudio = categoriaRepository.findByNombre("Audio")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Audio", "🎧")));

        // 2. Marcas seguras
        Marca marcaSamsung = marcaRepository.findByNombre("Samsung")
                .orElseGet(() -> marcaRepository.save(new Marca(null, "Samsung")));
        Marca marcaLG = marcaRepository.findByNombre("LG")
                .orElseGet(() -> marcaRepository.save(new Marca(null, "LG")));
        Marca marcaLenovo = marcaRepository.findByNombre("Lenovo")
                .orElseGet(() -> marcaRepository.save(new Marca(null, "Lenovo")));
        Marca marcaApple = marcaRepository.findByNombre("Apple")
                .orElseGet(() -> marcaRepository.save(new Marca(null, "Apple")));
        Marca marcaSony = marcaRepository.findByNombre("Sony")
                .orElseGet(() -> marcaRepository.save(new Marca(null, "Sony")));

        // 3. Productos iniciales
        Producto p1 = new Producto();
        p1.setNombre("Samsung Galaxy A55 5G");
        p1.setModelo("A55 5G");
        p1.setSku("EFE-001");
        p1.setPrecio(new java.math.BigDecimal("1499.00"));
        p1.setOferta(new java.math.BigDecimal("1299.00"));
        p1.setStock(15);
        p1.setStockMinimo(5);
        p1.setCategoria(catCelulares);
        p1.setMarca(marcaSamsung);
        p1.setIcon("📱");
        p1.setDescripcion("Smartphone Samsung con pantalla Super AMOLED y cámara con estabilización óptica.");

        Producto p2 = new Producto();
        p2.setNombre("Laptop Lenovo IdeaPad");
        p2.setModelo("IdeaPad 3");
        p2.setSku("EFE-002");
        p2.setPrecio(new java.math.BigDecimal("2299.00"));
        p2.setOferta(java.math.BigDecimal.ZERO);
        p2.setStock(8);
        p2.setStockMinimo(3);
        p2.setCategoria(catLaptops);
        p2.setMarca(marcaLenovo);
        p2.setIcon("💻");
        p2.setDescripcion("Laptop para productividad, estudio y trabajo con procesador veloz.");

        Producto p3 = new Producto();
        p3.setNombre("Smart TV LG 55 pulgadas");
        p3.setModelo("UHD 55");
        p3.setSku("EFE-003");
        p3.setPrecio(new java.math.BigDecimal("1899.00"));
        p3.setOferta(new java.math.BigDecimal("1749.00"));
        p3.setStock(6);
        p3.setStockMinimo(2);
        p3.setCategoria(catTVs);
        p3.setMarca(marcaLG);
        p3.setIcon("📺");
        p3.setDescripcion("Televisor Smart TV 4K con tecnología ThinQ AI y sonido envolvente.");

        Producto p4 = new Producto();
        p4.setNombre("Refrigeradora Samsung");
        p4.setModelo("RT38");
        p4.setSku("EFE-004");
        p4.setPrecio(new java.math.BigDecimal("2499.00"));
        p4.setOferta(java.math.BigDecimal.ZERO);
        p4.setStock(4);
        p4.setStockMinimo(2);
        p4.setCategoria(catElectro);
        p4.setMarca(marcaSamsung);
        p4.setIcon("🧊");
        p4.setDescripcion("Refrigeradora No Frost con sistema Digital Inverter y ahorro de energía.");

        Producto p5 = new Producto();
        p5.setNombre("Audífonos Sony");
        p5.setModelo("WH-CH520");
        p5.setSku("EFE-005");
        p5.setPrecio(new java.math.BigDecimal("199.00"));
        p5.setOferta(new java.math.BigDecimal("169.00"));
        p5.setStock(30);
        p5.setStockMinimo(10);
        p5.setCategoria(catAudio);
        p5.setMarca(marcaSony);
        p5.setIcon("🎧");
        p5.setDescripcion("Audífonos inalámbricos Bluetooth con hasta 50 horas de batería.");

        Producto p6 = new Producto();
        p6.setNombre("iPhone 13");
        p6.setModelo("13");
        p6.setSku("EFE-006");
        p6.setPrecio(new java.math.BigDecimal("2599.00"));
        p6.setOferta(new java.math.BigDecimal("2399.00"));
        p6.setStock(5);
        p6.setStockMinimo(2);
        p6.setCategoria(catCelulares);
        p6.setMarca(marcaApple);
        p6.setIcon("📱");
        p6.setDescripcion("Smartphone Apple con chip A15 Bionic y doble cámara avanzada.");

        productoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));
        return productoRepository.findAll();
    }
}
