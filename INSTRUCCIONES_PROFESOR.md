# 📘 GUÍA DE EJECUCIÓN DEL PROYECTO - TIENDAS EFE
### Sistema de Comercio Electrónico y Gestión con Resiliencia de Datos (Spring Boot + MySQL / H2)

---

## 🌟 Base de Datos en la Nube (Clever Cloud) + Respaldo en Memoria
Este proyecto está configurado para conectarse a **MySQL 8.0 en la Nube (Clever Cloud)**:
- **Ejecución Inmediata:** Cualquier persona (el docente o un evaluador) puede presionar **Run / F5** en VS Code o IntelliJ y el sistema se conectará a la base de datos en internet automáticamente. **No requiere instalar ni encender MySQL local**.
- **Tolerancia a fallos:** Si la computadora no cuenta con conexión a internet, el sistema conmuta automáticamente a una base de datos relacional interna en memoria (H2) para que el sitio web siga operando sin cerrarse.

---

## 💻 1. Requisitos del Sistema
- **Java JDK 17 o superior** (compatible con JDK 17, 21 o 25).
- **IDE:** Visual Studio Code (con *Extension Pack for Java*), IntelliJ IDEA o Eclipse.
- *(Opcional)* **MySQL Server 8.0** en el puerto 3306.

---

## 🚀 2. ¿Cómo Ejecutar el Proyecto? (¡1 solo paso!)

### En Visual Studio Code:
1. Abrir la carpeta `efe-tienda` en VS Code.
2. Ir a: `src/main/java/com/efe/tienda/TiendaApplication.java`.
3. Hacer clic en **Run** (o presionar `F5`).
4. En la terminal verás uno de estos dos mensajes:
   - `🟢 [MODO ACTIVO: MYSQL 8.0]` si detectó tu MySQL.
   - `🚀 [MODO DE RESPALDO ACTIVO: H2 DATABASE EN MEMORIA]` si no tienes MySQL activo.
5. Abre en tu navegador:
   👉 **`http://localhost:8080`**

### En IntelliJ IDEA:
1. Abrir como proyecto Maven.
2. Ejecutar la clase `TiendaApplication`.

---

## ⚙️ 3. Configuración Opcional de Contraseña de MySQL
Si deseas usar tu propio servidor MySQL local, puedes ajustar tus credenciales en:
`src/main/resources/application.properties`
```properties
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```
*(Si dejas la contraseña tal cual y no coincide, la aplicación activará el modo de respaldo en memoria automáticamente sin cerrarse).*

---

## 📊 4. Funcionalidades para la Evaluación

1. **Catálogo y Compra:**
   - Navega a **Productos**, añade artículos al carrito y finaliza la compra.
   - Se procesará como `PAGO APROBADO`, descontando el stock y generando comprobante.
2. **Panel de Control (5 Tablas en Tiempo Real):**
   - Haz clic en **"Administración"** en la barra superior.
   - Verás el badge en vivo que te indica el modo de la base de datos.
   - Puedes editar precios, ajustar stock (+ / -), cambiar estados de pedidos y ver el kardex.
3. **Consola SQL Web (Solo en modo H2):**
   - Disponible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:tienda_efe`, Usuario: `sa`, Contraseña vacía).
