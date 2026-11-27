# 🛒 Tiendas EFE - E-commerce Modular (Spring Boot 3 + MySQL + Thymeleaf)

Sistema integral de comercio electrónico modular desarrollado en **Spring Boot 3** con persistencia en **MySQL** (JPA / Hibernate) y frontend reactivo modular (Thymeleaf, CSS y JavaScript).

---

## 🏗️ Arquitectura Modular del Backend

```
src/main/java/com/efe/tienda/
├── TiendaApplication.java                   # Punto de entrada principal Spring Boot
│
├── config/                                  # Configuraciones globales
│   ├── CorsConfig.java                      # Habilita peticiones cruzadas (CORS)
│   └── DataInitializer.java                 # Poblado automático inicial de datos en MySQL
│
├── controller/                              # Controladores Web y APIs REST
│   ├── HomeController.java                  # Sirve la vista principal web (Thymeleaf)
│   ├── ProductoRestController.java          # API /api/productos (Catálogo, Stock y Kardex)
│   ├── VentaRestController.java             # API /api/ventas (Pedidos, Carrito y Facturación)
│   ├── UsuarioRestController.java           # API /api/usuarios (Login, Personal y Accesos)
│   └── DashboardRestController.java         # API /api/dashboard (Métricas y resumen admin)
│
├── dto/                                     # Objetos de Transferencia de Datos (DTOs)
│   ├── VentaRequestDTO.java                 # Payload para procesar ventas y facturación
│   ├── ItemVentaDTO.java                    # Detalle de items comprados
│   ├── StockAjusteDTO.java                  # Auditoría y ajustes manuales de stock
│   ├── LoginRequestDTO.java                 # Credenciales de acceso
│   └── DashboardResponseDTO.java            # Métricas en tiempo real
│
├── model/                                   # Entidades de Base de Datos (JPA / MySQL)
│   ├── Categoria.java                       # Categorías de productos
│   ├── Marca.java                           # Marcas comerciales
│   ├── Producto.java                        # Productos, precios, oferta y stock actual
│   ├── MovimientoStock.java                 # Kardex de auditoría (Entradas, Salidas, Ajustes)
│   ├── Cliente.java                         # Clientes (DNI/RUC, dirección, teléfono)
│   ├── Pedido.java                          # Cabecera de venta (totales, IGV, estado)
│   ├── PedidoDetalle.java                   # Líneas de venta con precios congelados
│   ├── Comprobante.java                     # Facturación electrónica (Boletas y Facturas)
│   ├── Rol.java                             # Roles del sistema (ADMIN, VENDEDOR, etc.)
│   └── Usuario.java                         # Personal y cuentas de acceso
│
├── repository/                              # Interfaces Spring Data JPA
│   ├── ProductoRepository.java              # Búsquedas, filtros y alertas de bajo stock
│   ├── MovimientoStockRepository.java       # Historial de inventario
│   ├── PedidoRepository.java                # Consultas de pedidos y sumatoria de ventas
│   ├── ComprobanteRepository.java           # Correlativos de boletas y facturas
│   ├── ClienteRepository.java               # Búsqueda por DNI o correo
│   ├── UsuarioRepository.java               # Autenticación y filtro de empleados
│   └── ...
│
└── service/                                 # Lógica de Negocio Transaccional
    ├── ProductoService.java                 # Control de stock, validaciones y catálogo
    ├── VentaService.java                    # Descuento de stock atómico + cálculo IGV + factura
    ├── UsuarioService.java                  # Control de accesos y seguridad
    └── DashboardService.java                # Consolidación de métricas
```

---

## 🗄️ Base de Datos MySQL

### Configuración en `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tienda_efe?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```
*(Cambia la contraseña `root` por la que tengas configurada en tu MySQL).*

### Script SQL Opcional:
En `src/main/resources/db/tienda_efe_schema.sql` tienes el script DDL y DML completo con claves foráneas, índices y datos iniciales para ejecutar en MySQL Workbench o phpMyAdmin.
*(Nota: Gracias a `DataInitializer.java`, Spring Boot también auto-crea y llena las tablas automáticamente si están vacías).*

---

## 🚀 Requisitos y Cómo Ejecutar en Visual Studio Code

### 1. Requisito Crítico: Java 17 o 21
Spring Boot 3 requiere obligatoriamente **Java 17 o superior** (Java 8 no es compatible).
Si necesitas instalar Java 17 en Windows, abre PowerShell y ejecuta:
```powershell
winget install Microsoft.OpenJDK.17
```

### 2. Extensiones recomendadas en VS Code
1. **Extension Pack for Java** (de Microsoft).
2. **Spring Boot Tools** (de VMware).

### 3. Ejecutar el proyecto:
- Abre la carpeta `efe-tienda` en VS Code.
- Abre el archivo `src/main/java/com/efe/tienda/TiendaApplication.java`.
- Haz clic en el botón superior **"Run"** o presiona `F5`.
- Abre tu navegador en: [http://localhost:8080](http://localhost:8080)

---

## 🔑 Credenciales por Defecto
- **Administrador:** `admin@efe.com` / `admin123` (Acceso completo al módulo `/admin`)
- **Vendedor:** `vendedor@efe.com` / `vendedor123`
- **Almacenero:** `almacen@efe.com` / `almacen123`
