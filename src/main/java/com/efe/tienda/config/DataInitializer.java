package com.efe.tienda.config;

import com.efe.tienda.model.*;
import com.efe.tienda.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           CategoriaRepository categoriaRepository,
                           MarcaRepository marcaRepository,
                           ProductoRepository productoRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        try {
            // 1. Roles iniciales
            if (rolRepository.count() == 0) {
                rolRepository.saveAll(List.of(
                        new Rol(null, "ROLE_ADMIN", "Administrador total del sistema"),
                        new Rol(null, "ROLE_VENDEDOR", "Personal de ventas"),
                        new Rol(null, "ROLE_ALMACENERO", "Personal de almacén y stock"),
                        new Rol(null, "ROLE_CLIENTE", "Cliente de la tienda")
                ));
            }

            // 2. Personal y Administrador inicial
            if (usuarioRepository.count() == 0) {
                Rol adminRol = rolRepository.findByNombre("ROLE_ADMIN").orElseGet(() -> rolRepository.save(new Rol(null, "ROLE_ADMIN", "Administrador")));
                Rol vendedorRol = rolRepository.findByNombre("ROLE_VENDEDOR").orElseGet(() -> rolRepository.save(new Rol(null, "ROLE_VENDEDOR", "Vendedor")));

                Usuario admin = new Usuario();
                admin.setUsername("admin@efe.com");
                admin.setPassword("admin123");
                admin.setNombres("Administrador");
                admin.setApellidos("EFE");
                admin.setTelefono("999888777");
                admin.setEstado("ACTIVO");
                admin.setRol(adminRol);
                admin.setEsPersonal(true);
                admin.setCargo("Gerente de Tienda");
                usuarioRepository.save(admin);

                Usuario vendedor = new Usuario();
                vendedor.setUsername("vendedor@efe.com");
                vendedor.setPassword("vendedor123");
                vendedor.setNombres("Carlos");
                vendedor.setApellidos("Gómez");
                vendedor.setTelefono("987654321");
                vendedor.setEstado("ACTIVO");
                vendedor.setRol(vendedorRol);
                vendedor.setEsPersonal(true);
                vendedor.setCargo("Asesor Comercial");
                usuarioRepository.save(vendedor);
            }

            // 3. Categorías iniciales
            if (categoriaRepository.count() == 0) {
                categoriaRepository.saveAll(List.of(
                        new Categoria(null, "Celulares", "📱"),
                        new Categoria(null, "Laptops", "💻"),
                        new Categoria(null, "Televisores", "📺"),
                        new Categoria(null, "Electrohogar", "🧊"),
                        new Categoria(null, "Audio", "🎧")
                ));
            }

            // 4. Marcas iniciales
            if (marcaRepository.count() == 0) {
                marcaRepository.saveAll(List.of(
                        new Marca(null, "Samsung"),
                        new Marca(null, "LG"),
                        new Marca(null, "Apple"),
                        new Marca(null, "Lenovo"),
                        new Marca(null, "Motorola"),
                        new Marca(null, "Sony")
                ));
            }

            // 5. Productos iniciales
            if (productoRepository.count() == 0) {
                Categoria catCelulares = categoriaRepository.findByNombre("Celulares").orElse(null);
                Categoria catLaptops = categoriaRepository.findByNombre("Laptops").orElse(null);
                Categoria catTVs = categoriaRepository.findByNombre("Televisores").orElse(null);
                Categoria catElectro = categoriaRepository.findByNombre("Electrohogar").orElse(null);
                Categoria catAudio = categoriaRepository.findByNombre("Audio").orElse(null);

                Marca marcaSamsung = marcaRepository.findByNombre("Samsung").orElse(null);
                Marca marcaLenovo = marcaRepository.findByNombre("Lenovo").orElse(null);
                Marca marcaLG = marcaRepository.findByNombre("LG").orElse(null);
                Marca marcaSony = marcaRepository.findByNombre("Sony").orElse(null);
                Marca marcaApple = marcaRepository.findByNombre("Apple").orElse(null);

                Producto p1 = new Producto();
                p1.setNombre("Samsung Galaxy A55 5G");
                p1.setModelo("A55 5G");
                p1.setSku("EFE-001");
                p1.setPrecio(new BigDecimal("1499.00"));
                p1.setOferta(new BigDecimal("1299.00"));
                p1.setStock(15);
                p1.setStockMinimo(5);
                p1.setCategoria(catCelulares);
                p1.setMarca(marcaSamsung);
                p1.setIcon("📱");
                p1.setImagenUrl("imagenes/productos/a55-frontal-con-ficha.jpg");
                p1.setDescripcion("Smartphone Samsung con pantalla Super AMOLED y cámara con estabilización óptica.");

                Producto p2 = new Producto();
                p2.setNombre("Laptop Lenovo IdeaPad");
                p2.setModelo("IdeaPad 3");
                p2.setSku("EFE-002");
                p2.setPrecio(new BigDecimal("2299.00"));
                p2.setOferta(BigDecimal.ZERO);
                p2.setStock(8);
                p2.setStockMinimo(3);
                p2.setCategoria(catLaptops);
                p2.setMarca(marcaLenovo);
                p2.setIcon("💻");
                p2.setImagenUrl("imagenes/productos/laptop-lenovo.jpg");
                p2.setDescripcion("Laptop para productividad, estudio y trabajo con procesador veloz.");

                Producto p3 = new Producto();
                p3.setNombre("Smart TV LG 55 pulgadas");
                p3.setModelo("UHD 55");
                p3.setSku("EFE-003");
                p3.setPrecio(new BigDecimal("1899.00"));
                p3.setOferta(new BigDecimal("1749.00"));
                p3.setStock(6);
                p3.setStockMinimo(2);
                p3.setCategoria(catTVs);
                p3.setMarca(marcaLG);
                p3.setIcon("📺");
                p3.setImagenUrl("imagenes/productos/smart-tv-lg.jpg");
                p3.setDescripcion("Televisor Smart TV 4K con tecnología ThinQ AI y sonido envolvente.");

                Producto p4 = new Producto();
                p4.setNombre("Refrigeradora Samsung");
                p4.setModelo("RT38");
                p4.setSku("EFE-004");
                p4.setPrecio(new BigDecimal("2499.00"));
                p4.setOferta(BigDecimal.ZERO);
                p4.setStock(4);
                p4.setStockMinimo(2);
                p4.setCategoria(catElectro);
                p4.setMarca(marcaSamsung);
                p4.setIcon("🧊");
                p4.setImagenUrl("imagenes/productos/refrigeradora-samsung.jpg");
                p4.setDescripcion("Refrigeradora No Frost con sistema Digital Inverter y ahorro de energía.");

                Producto p5 = new Producto();
                p5.setNombre("Audífonos Sony");
                p5.setModelo("WH-CH520");
                p5.setSku("EFE-005");
                p5.setPrecio(new BigDecimal("199.00"));
                p5.setOferta(new BigDecimal("169.00"));
                p5.setStock(30);
                p5.setStockMinimo(10);
                p5.setCategoria(catAudio);
                p5.setMarca(marcaSony);
                p5.setIcon("🎧");
                p5.setImagenUrl("imagenes/productos/audifonos-sony.jpg");
                p5.setDescripcion("Audífonos inalámbricos Bluetooth con hasta 50 horas de batería.");

                Producto p6 = new Producto();
                p6.setNombre("iPhone 13");
                p6.setModelo("13");
                p6.setSku("EFE-006");
                p6.setPrecio(new BigDecimal("2599.00"));
                p6.setOferta(new BigDecimal("2399.00"));
                p6.setStock(5);
                p6.setStockMinimo(2);
                p6.setCategoria(catCelulares);
                p6.setMarca(marcaApple);
                p6.setIcon("📱");
                p6.setImagenUrl("imagenes/productos/iphone-13.jpg");
                p6.setDescripcion("Smartphone Apple con chip A15 Bionic y doble cámara avanzada.");

                productoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));
            }
            System.out.println("✅ Base de datos conectada y verificada exitosamente.");
        } catch (Exception e) {
            System.err.println("Aviso en inicialización de datos: " + e.getMessage());
        }
    }
}
