-- ===================================================================
-- SCRIPT DE BASE DE DATOS: TIENDA EFE (E-COMMERCE)
-- Motor: MySQL 8.x / MariaDB
-- Módulos:
--   1. Seguridad, Personal y Accesos (Roles, Empleados, Usuarios)
--   2. Catálogo e Inventario (Categorías, Marcas, Productos, Kardex/Stock)
--   3. Clientes, Ventas y Facturación (Clientes, Pedidos, Detalle, Comprobantes)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS tienda_efe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tienda_efe;

-- -------------------------------------------------------------------
-- 1. MÓDULO DE SEGURIDAD, PERSONAL Y ACCESOS
-- -------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE, -- Correo o usuario de login
    password VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'ACTIVO', -- ACTIVO, INACTIVO, BLOQUEADO
    rol_id BIGINT NOT NULL,
    es_personal BOOLEAN DEFAULT FALSE,
    cargo VARCHAR(80),                  -- Cargo si es personal: Administrador, Vendedor, Almacenero, etc.
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles (id)
);

-- -------------------------------------------------------------------
-- 2. MÓDULO DE PRODUCTOS E INVENTARIO (STOCK)
-- -------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(10),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS marcas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    modelo VARCHAR(80),
    sku VARCHAR(50) NOT NULL UNIQUE,
    precio DECIMAL(10, 2) NOT NULL,
    oferta DECIMAL(10, 2) DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    descripcion TEXT,
    icon VARCHAR(10),
    imagen_url VARCHAR(255),
    categoria_id BIGINT NOT NULL,
    marca_id BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id),
    CONSTRAINT fk_producto_marca FOREIGN KEY (marca_id) REFERENCES marcas (id)
);

-- Kardex de movimientos de inventario (auditoría de stock)
CREATE TABLE IF NOT EXISTS movimientos_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- ENTRADA, SALIDA, AJUSTE
    cantidad INT NOT NULL,
    stock_anterior INT NOT NULL,
    stock_posterior INT NOT NULL,
    motivo VARCHAR(200) NOT NULL, -- "Venta pedido #123", "Ingreso mercadería proveedor", etc.
    usuario_responsable VARCHAR(100),
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos (id)
);

-- -------------------------------------------------------------------
-- 3. MÓDULO DE CLIENTES, VENTAS Y FACTURACIÓN
-- -------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(10) DEFAULT 'DNI', -- DNI, RUC, CE
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    usuario_id BIGINT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metodo_entrega VARCHAR(50) DEFAULT 'Delivery', -- Delivery, Recojo en tienda
    direccion_envio VARCHAR(255),
    costo_envio DECIMAL(10, 2) DEFAULT 0.00,
    subtotal DECIMAL(10, 2) NOT NULL,
    igv DECIMAL(10, 2) NOT NULL,                    -- 18% IGV (Perú)
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(30) DEFAULT 'PENDIENTE',         -- PENDIENTE, PAGADO, EN_CAMINO, ENTREGADO, CANCELADO
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE TABLE IF NOT EXISTS pedido_detalles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos (id)
);

-- Comprobantes de Pago Electrónicos (Facturación: Boletas y Facturas)
CREATE TABLE IF NOT EXISTS comprobantes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT UNIQUE NOT NULL,
    tipo_comprobante VARCHAR(20) NOT NULL, -- BOLETA, FACTURA
    serie VARCHAR(10) NOT NULL,             -- B001, F001
    numero VARCHAR(20) NOT NULL,            -- 00000001
    cliente_documento VARCHAR(20) NOT NULL,
    cliente_razon_social VARCHAR(150) NOT NULL,
    cliente_direccion VARCHAR(200),
    subtotal DECIMAL(10, 2) NOT NULL,
    igv DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_sunat VARCHAR(20) DEFAULT 'ACEPTADO', -- ACEPTADO, ANULADO
    CONSTRAINT fk_comprobante_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id)
);

-- -------------------------------------------------------------------
-- DATOS INICIALES SEMILLA (SEED DATA)
-- -------------------------------------------------------------------

INSERT IGNORE INTO roles (id, nombre, descripcion) VALUES
(1, 'ROLE_ADMIN', 'Administrador con acceso total al sistema'),
(2, 'ROLE_VENDEDOR', 'Personal de ventas y atención al cliente'),
(3, 'ROLE_ALMACENERO', 'Personal de control de stock y almacén'),
(4, 'ROLE_CLIENTE', 'Cliente registrado de la tienda web');

INSERT IGNORE INTO usuarios (id, username, password, nombres, apellidos, telefono, estado, rol_id, es_personal, cargo) VALUES
(1, 'admin@efe.com', 'admin123', 'Administrador', 'EFE', '999888777', 'ACTIVO', 1, TRUE, 'Gerente de Tienda'),
(2, 'vendedor@efe.com', 'vendedor123', 'Carlos', 'Gómez', '987654321', 'ACTIVO', 2, TRUE, 'Asesor Comercial'),
(3, 'almacen@efe.com', 'almacen123', 'Marcos', 'Flores', '912345678', 'ACTIVO', 3, TRUE, 'Jefe de Almacén');

INSERT IGNORE INTO categorias (id, nombre, icon) VALUES
(1, 'Celulares', '📱'),
(2, 'Laptops', '💻'),
(3, 'Televisores', '📺'),
(4, 'Electrohogar', '🧊'),
(5, 'Audio', '🎧');

INSERT IGNORE INTO marcas (id, nombre) VALUES
(1, 'Samsung'),
(2, 'LG'),
(3, 'Apple'),
(4, 'Lenovo'),
(5, 'Motorola'),
(6, 'Sony');

INSERT IGNORE INTO productos (id, nombre, categoria_id, marca_id, modelo, sku, precio, oferta, stock, icon, imagen_url, descripcion) VALUES
(1, 'Samsung Galaxy A55 5G', 1, 1, 'A55 5G', 'EFE-001', 1499.00, 1299.00, 15, '📱', 'imagenes/productos/a55-frontal-con-ficha.jpg', 'Smartphone Samsung con pantalla Super AMOLED y cámara con estabilización óptica.'),
(2, 'Laptop Lenovo IdeaPad', 2, 4, 'IdeaPad 3', 'EFE-002', 2299.00, 0.00, 8, '💻', 'imagenes/productos/laptop-lenovo.jpg', 'Laptop para productividad, estudio y trabajo con procesador veloz.'),
(3, 'Smart TV LG 55 pulgadas', 3, 2, 'UHD 55', 'EFE-003', 1899.00, 1749.00, 6, '📺', 'imagenes/productos/smart-tv-lg.jpg', 'Televisor Smart TV 4K con tecnología ThinQ AI y sonido envolvente.'),
(4, 'Refrigeradora Samsung', 4, 1, 'RT38', 'EFE-004', 2499.00, 0.00, 4, '🧊', 'imagenes/productos/refrigeradora-samsung.jpg', 'Refrigeradora No Frost con sistema Digital Inverter y ahorro de energía.'),
(5, 'Audífonos Sony', 5, 6, 'WH-CH520', 'EFE-005', 199.00, 169.00, 30, '🎧', 'imagenes/productos/audifonos-sony.jpg', 'Audífonos inalámbricos Bluetooth con hasta 50 horas de batería.'),
(6, 'iPhone 13', 1, 3, '13', 'EFE-006', 2599.00, 2399.00, 5, '📱', 'imagenes/productos/iphone-13.jpg', 'Smartphone Apple con chip A15 Bionic y doble cámara avanzada.');
