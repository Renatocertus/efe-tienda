/* Administración - Panel MySQL en Tiempo Real (Tiendas EFE) */

// Módulos destacados conectados con MySQL en tiempo real
const adminSections = [
  ['resumen', '📊 Resumen General'],
  ['productos', '📦 1. Productos (Stock/Precios)'],
  ['pedidos', '🛒 2. Pedidos y Ventas'],
  ['clientes', '👥 3. Clientes'],
  ['inventario', '📋 4. Movimientos Stock (Kardex)'],
  ['comprobantes', '🧾 5. Comprobantes SUNAT'],
  ['usuarios', '👤 Usuarios / Personal']
];

// Estado de sincronización en tiempo real
let liveSyncTimer = null;
let liveSyncActivo = true;
let ultimaSincronizacion = 'Nunca';
let cargandoSync = false;
let modoBDActual = 'MYSQL'; // MYSQL o H2_MEMORIA

// Datos cargados desde MySQL en memoria
let datosMySQL = {
  productos: [],
  pedidos: [],
  clientes: [],
  inventario: [],
  comprobantes: [],
  resumen: null
};

function renderAdmin() {
  if (!esAdmin()) {
    return `
      <div class="panel text-center py-5">
        <h2>🔒 Panel de Control MySQL</h2>
        <p class="muted">Para ver y modificar las 5 tablas en tiempo real necesitas permisos de Administrador.</p>
        <div class="mt-4">
          <button class="btn btn-primary px-4 py-2" onclick="loginAdminDemo()">⚡ Ingresar como Administrador Demo (1 Clic)</button>
        </div>
        <p class="muted mt-3" style="font-size:12px;">Credenciales: admin@efe.com / admin123</p>
      </div>
    `;
  }

  // Iniciar temporizador de tiempo real si no está corriendo
  iniciarPollingEnVivo();

  let menu = lista('tpl-admin-menu', adminSections, function (s) {
    return { clave: s[0], texto: s[1], clase: adminTab === s[0] ? 'active' : '' };
  });

  let badgeTexto = liveSyncActivo
    ? (modoBDActual === 'H2_MEMORIA' ? '🟣 EN VIVO (H2 Memoria)' : '🟢 EN VIVO (MySQL 8.0)')
    : '⏸️ PAUSADO';

  return plantilla('tpl-admin', {
    menu: menu,
    syncEstado: badgeTexto,
    syncClase: liveSyncActivo ? (modoBDActual === 'H2_MEMORIA' ? 'live-badge memory' : 'live-badge online') : 'live-badge offline',
    ultimaSync: ultimaSincronizacion,
    contenido: adminContent()
  });
}

function loginAdminDemo() {
  const usuarios = get('usuarios') || [];
  const admin = usuarios.find(u => u.rol === 2) || { id: 2, nombres: 'Administrador Demo', rol: 2 };
  set('sesion', admin.id);
  toast('¡Acceso concedido como Administrador! ✅');
  render();
}

function cambiarTab(nombre) {
  adminTab = nombre;
  render();
  sincronizarModuloActual(true);
}

function toggleLiveSync() {
  liveSyncActivo = !liveSyncActivo;
  if (liveSyncActivo) {
    iniciarPollingEnVivo();
    toast('Sincronización en tiempo real: ACTIVADA 🟢');
    sincronizarModuloActual(true);
  } else {
    detenerPollingEnVivo();
    toast('Sincronización en tiempo real: PAUSADA ⏸️');
    render();
  }
}

function iniciarPollingEnVivo() {
  if (liveSyncTimer) return;
  // Carga inmediata
  sincronizarModuloActual(false);
  // Polling cada 3 segundos
  liveSyncTimer = setInterval(function () {
    if (currentPage === 'admin' && liveSyncActivo && !cargandoSync) {
      sincronizarModuloActual(false);
    }
  }, 3000);
}

function detenerPollingEnVivo() {
  if (liveSyncTimer) {
    clearInterval(liveSyncTimer);
    liveSyncTimer = null;
  }
}

// Sincroniza el módulo activo directamente con MySQL
async function sincronizarModuloActual(forzarRender) {
  if (typeof API === 'undefined') return;
  cargandoSync = true;

  try {
    const ahora = new Date().toLocaleTimeString();

    // Consultar modo de base de datos (MySQL vs H2 en memoria)
    try {
      const estado = await API.obtenerEstadoConexion();
      if (estado && estado.modo) modoBDActual = estado.modo;
    } catch (e) {}

    if (adminTab === 'resumen') {
      const res = await API.obtenerDashboard();
      if (res) {
        datosMySQL.resumen = res;
        ultimaSincronizacion = ahora;
        if (forzarRender || currentPage === 'admin') actualizarDOMResumen(res);
      }
    } else if (adminTab === 'productos') {
      const prods = await API.listarProductos();
      if (prods) {
        datosMySQL.productos = prods;
        ultimaSincronizacion = ahora;
        // Mantener localStorage actualizado
        set('productos', prods.map(p => ({
          id: p.id,
          nombre: p.nombre,
          precio: Number(p.precio),
          oferta: Number(p.oferta) || 0,
          stock: Number(p.stock),
          sku: p.sku || '',
          modelo: p.modelo || '',
          icon: p.icon || '📦',
          descripcion: p.descripcion || ''
        })));
        if (forzarRender || currentPage === 'admin') actualizarDOMTabla('productos');
      }
    } else if (adminTab === 'pedidos') {
      const peds = await API.listarPedidos();
      if (peds) {
        datosMySQL.pedidos = peds;
        ultimaSincronizacion = ahora;
        if (forzarRender || currentPage === 'admin') actualizarDOMTabla('pedidos');
      }
    } else if (adminTab === 'clientes') {
      const cls = await API.listarClientes();
      if (cls) {
        datosMySQL.clientes = cls;
        ultimaSincronizacion = ahora;
        if (forzarRender || currentPage === 'admin') actualizarDOMTabla('clientes');
      }
    } else if (adminTab === 'inventario') {
      const movs = await API.listarMovimientosStock();
      if (movs) {
        datosMySQL.inventario = movs;
        ultimaSincronizacion = ahora;
        if (forzarRender || currentPage === 'admin') actualizarDOMTabla('inventario');
      }
    } else if (adminTab === 'comprobantes') {
      const comps = await API.listarComprobantes();
      if (comps) {
        datosMySQL.comprobantes = comps;
        ultimaSincronizacion = ahora;
        if (forzarRender || currentPage === 'admin') actualizarDOMTabla('comprobantes');
      }
    }
    actualizarIndicadorSync();
  } catch (err) {
    console.warn('Sync en vivo MySQL:', err);
  } finally {
    cargandoSync = false;
  }
}

function actualizarIndicadorSync() {
  const elem = document.getElementById('syncHora');
  if (elem) elem.textContent = ultimaSincronizacion;
}

function adminContent() {
  if (adminTab === 'resumen') {
    const res = datosMySQL.resumen || {
      totalProductos: get('productos').length,
      totalUsuarios: get('usuarios').length,
      totalPedidos: get('pedidos').length,
      pedidosPendientes: 0,
      totalVentas: 0,
      productosBajoStock: 0
    };

    return `
      <div class="live-panel-header">
        <div>
          <h2>📊 Panel de Control MySQL</h2>
          <p class="muted">Datos sincronizados en tiempo real con la base de datos <code>tienda_efe</code> en MySQL 8.0</p>
        </div>
        <div class="live-controls d-flex align-items-center gap-2">
          <span class="${liveSyncActivo ? 'live-badge online' : 'live-badge offline'}">${liveSyncActivo ? '🟢 EN VIVO (MySQL)' : '⏸️ PAUSADO'}</span>
          <button class="btn btn-primary btn-sm" onclick="sincronizarModuloActual(true)">🔄 Refrescar ahora</button>
          <button class="btn btn-outline-secondary btn-sm" onclick="toggleLiveSync()">${liveSyncActivo ? 'Pausar' : 'Activar'}</button>
        </div>
      </div>

      <div class="stats" id="resumenStats">
        <div class="stat"><b>${res.totalProductos || 0}</b><span>Productos en MySQL</span></div>
        <div class="stat"><b>${res.totalPedidos || 0}</b><span>Pedidos Totales</span></div>
        <div class="stat"><b>S/ ${Number(res.totalVentas || 0).toFixed(2)}</b><span>Ventas Totales</span></div>
        <div class="stat"><b>${res.productosBajoStock || 0}</b><span>Alerta Stock Crítico</span></div>
      </div>

      <div class="panel">
        <h3>⚡ 5 Tablas Modificables en Tiempo Real</h3>
        <p class="muted">Selecciona cualquiera en el menú lateral para ver cómo cambian en MySQL inmediatamente:</p>
        <div class="realtime-cards">
          <div class="rt-card" onclick="cambiarTab('productos')">
            <b>1. productos</b>
            <p>Modifica stock, precios, agrega o elimina artículos al instante.</p>
            <span class="btn-link">Gestionar Productos &rarr;</span>
          </div>
          <div class="rt-card" onclick="cambiarTab('pedidos')">
            <b>2. pedidos</b>
            <p>Aprueba o cambia estados de pedidos ("PAGO APROBADO", "ENTREGADO").</p>
            <span class="btn-link">Gestionar Pedidos &rarr;</span>
          </div>
          <div class="rt-card" onclick="cambiarTab('clientes')">
            <b>3. clientes</b>
            <p>Edita datos de clientes (documentos, nombres, correo, teléfono).</p>
            <span class="btn-link">Gestionar Clientes &rarr;</span>
          </div>
          <div class="rt-card" onclick="cambiarTab('inventario')">
            <b>4. movimientos_stock</b>
            <p>Kardex automático. Registra ingresos o salidas de mercadería.</p>
            <span class="btn-link">Ver Kardex en Vivo &rarr;</span>
          </div>
          <div class="rt-card" onclick="cambiarTab('comprobantes')">
            <b>5. comprobantes</b>
            <p>Facturación electrónica SUNAT (Boletas y Facturas emitidas).</p>
            <span class="btn-link">Ver Facturación &rarr;</span>
          </div>
        </div>
      </div>
    `;
  }

  // TABLA 1: PRODUCTOS
  if (adminTab === 'productos') {
    const prods = datosMySQL.productos.length ? datosMySQL.productos : get('productos');
    return generarVistaTabla({
      titulo: '📦 Tabla: productos (MySQL)',
      subtitulo: 'Catálogo de productos, stock disponible y precios en tiempo real.',
      botonAgregar: '<button class="btn btn-primary btn-sm" onclick="modalNuevoProducto()">+ Nuevo Producto</button> <button class="btn btn-outline-secondary btn-sm" onclick="modalAjusteStock()">⚡ Ajustar Stock</button>',
      columnas: ['ID', 'Nombre', 'SKU', 'Precio', 'Oferta', 'Stock', 'Acciones'],
      filas: prods.map(p => `
        <tr>
          <td><b>#${p.id}</b></td>
          <td>${p.icon || '📦'} ${p.nombre}</td>
          <td><code>${p.sku || '-'}</code></td>
          <td>S/ ${Number(p.precio).toFixed(2)}</td>
          <td>${p.oferta && Number(p.oferta) > 0 ? 'S/ ' + Number(p.oferta).toFixed(2) : '-'}</td>
          <td><span class="badge ${p.stock <= 5 ? 'text-bg-danger' : 'text-bg-success'}">${p.stock} un.</span></td>
          <td>
            <div class="actions d-flex gap-1">
              <button class="btn btn-warning btn-sm" onclick="editarProductoMySQL(${p.id})">✏️ Editar</button>
              <button class="btn btn-info btn-sm text-white" onclick="ajustarStockRapido(${p.id}, '${p.nombre.replace(/'/g, "\\'")}', ${p.stock})">⚡ Stock</button>
              <button class="btn btn-danger btn-sm" onclick="eliminarProductoMySQL(${p.id})">🗑️</button>
            </div>
          </td>
        </tr>
      `).join('')
    });
  }

  // TABLA 2: PEDIDOS
  if (adminTab === 'pedidos') {
    const peds = datosMySQL.pedidos;
    return generarVistaTabla({
      titulo: '🛒 Tabla: pedidos (MySQL)',
      subtitulo: 'Pedidos registrados y actualización de estados en tiempo real.',
      botonAgregar: '<button class="btn btn-primary btn-sm" onclick="sincronizarModuloActual(true)">🔄 Refrescar</button>',
      columnas: ['ID', 'Código', 'Cliente', 'Fecha', 'Total', 'Estado Actual', 'Cambiar Estado'],
      filas: peds.length ? peds.map(p => `
        <tr>
          <td><b>#${p.id}</b></td>
          <td><code>${p.codigo}</code></td>
          <td>${p.clienteNombre || 'Cliente #' + (p.clienteId || '-')}</td>
          <td>${p.fechaEmision || '-'}</td>
          <td><b>S/ ${Number(p.total).toFixed(2)}</b></td>
          <td><span class="badge ${p.estado === 'PAGO APROBADO' ? 'text-bg-success' : (p.estado === 'ENTREGADO' ? 'text-bg-info' : 'text-bg-warning')}">${p.estado}</span></td>
          <td>
            <select class="form-select form-select-sm select-estado" onchange="cambiarEstadoPedidoMySQL(${p.id}, this.value)">
              <option value="">Cambiar a...</option>
              <option value="PAGO APROBADO">PAGO APROBADO</option>
              <option value="EN CAMINO">EN CAMINO</option>
              <option value="ENTREGADO">ENTREGADO</option>
              <option value="CANCELADO">CANCELADO</option>
            </select>
          </td>
        </tr>
      `).join('') : '<tr><td colspan="7" class="text-center py-4 text-muted">No hay pedidos registrados todavía en MySQL. Haz una compra en la tienda para verla aquí en vivo.</td></tr>'
    });
  }

  // TABLA 3: CLIENTES
  if (adminTab === 'clientes') {
    const cls = datosMySQL.clientes;
    return generarVistaTabla({
      titulo: '👥 Tabla: clientes (MySQL)',
      subtitulo: 'Directorio de clientes y edición de información de contacto en tiempo real.',
      botonAgregar: '<button class="btn btn-primary btn-sm" onclick="modalNuevoCliente()">+ Nuevo Cliente</button>',
      columnas: ['ID', 'Doc.', 'Nombres y Apellidos', 'Correo', 'Teléfono', 'Dirección', 'Acciones'],
      filas: cls.length ? cls.map(c => `
        <tr>
          <td><b>#${c.id}</b></td>
          <td><code>${c.tipoDocumento || 'DNI'}: ${c.numeroDocumento}</code></td>
          <td><b>${c.nombres} ${c.apellidos}</b></td>
          <td>${c.correo || '-'}</td>
          <td>${c.telefono || '-'}</td>
          <td>${c.direccion || '-'}</td>
          <td>
            <button class="btn btn-warning btn-sm" onclick="editarClienteMySQL(${c.id})">✏️ Editar</button>
          </td>
        </tr>
      `).join('') : '<tr><td colspan="7" class="text-center py-4 text-muted">No hay clientes registrados en MySQL.</td></tr>'
    });
  }

  // TABLA 4: INVENTARIO / KARDEX
  if (adminTab === 'inventario') {
    const movs = datosMySQL.inventario;
    return generarVistaTabla({
      titulo: '📋 Tabla: movimientos_stock (Kardex MySQL)',
      subtitulo: 'Auditoría en tiempo real de entradas, salidas y ventas de stock.',
      botonAgregar: '<button class="btn btn-primary btn-sm" onclick="modalAjusteStock()">+ Registrar Movimiento</button>',
      columnas: ['ID', 'Fecha', 'Producto', 'Tipo', 'Cantidad', 'Stock Antes ➡️ Después', 'Motivo', 'Responsable'],
      filas: movs.length ? movs.map(m => `
        <tr>
          <td>#${m.id}</td>
          <td><small>${m.fechaMovimiento || '-'}</small></td>
          <td><b>${m.productoNombre || 'Producto #' + m.productoId}</b></td>
          <td><span class="badge ${m.tipo === 'ENTRADA' ? 'text-bg-success' : (m.tipo === 'SALIDA' ? 'text-bg-danger' : 'text-bg-warning')}">${m.tipo}</span></td>
          <td><b>${m.tipo === 'SALIDA' ? '-' : '+'}${m.cantidad}</b></td>
          <td><code>${m.stockAnterior} ➡️ ${m.stockPosterior}</code></td>
          <td>${m.motivo || '-'}</td>
          <td><small>${m.usuarioResponsable || 'Sistema'}</small></td>
        </tr>
      `).join('') : '<tr><td colspan="8" class="text-center py-4 text-muted">No hay movimientos de stock registrados en MySQL todavía.</td></tr>'
    });
  }

  // TABLA 5: COMPROBANTES SUNAT
  if (adminTab === 'comprobantes') {
    const comps = datosMySQL.comprobantes;
    return generarVistaTabla({
      titulo: '🧾 Tabla: comprobantes (Facturación Electrónica MySQL)',
      subtitulo: 'Boletas y facturas emitidas automáticamente tras cada compra aprobada.',
      botonAgregar: '<button class="btn btn-primary btn-sm" onclick="sincronizarModuloActual(true)">🔄 Refrescar</button>',
      columnas: ['ID', 'Tipo', 'Serie - Número', 'Cliente / Razón Social', 'Subtotal', 'IGV (18%)', 'Total', 'SUNAT'],
      filas: comps.length ? comps.map(c => `
        <tr>
          <td>#${c.id}</td>
          <td><b>${c.tipoComprobante}</b></td>
          <td><code>${c.serie}-${c.numero}</code></td>
          <td>${c.clienteRazonSocial || c.clienteDocumento}</td>
          <td>S/ ${Number(c.subtotal).toFixed(2)}</td>
          <td>S/ ${Number(c.igv).toFixed(2)}</td>
          <td><b>S/ ${Number(c.total).toFixed(2)}</b></td>
          <td><span class="badge text-bg-success">✓ ${c.estadoSunat || 'ACEPTADO'}</span></td>
        </tr>
      `).join('') : '<tr><td colspan="8" class="text-center py-4 text-muted">No hay comprobantes electrónicos emitidos todavía. Haz una compra para generar uno.</td></tr>'
    });
  }

  // Módulos secundarios estándar
  let datos = get(adminTab) || [];
  return `
    <div class="panel">
      <h2>${adminTab}</h2>
      <p class="muted">${datos.length} registros</p>
      <div class="table-wrap table-responsive">
        <table class="table table-striped table-hover align-middle">
          <thead class="table-dark"><tr><th>ID</th><th>Datos</th></tr></thead>
          <tbody>
            ${datos.map(d => `<tr><td>#${d.id}</td><td><pre class="mb-0">${JSON.stringify(d)}</pre></td></tr>`).join('')}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

function generarVistaTabla(config) {
  return `
    <div class="panel">
      <div class="toolbar">
        <div>
          <h2>${config.titulo}</h2>
          <p class="muted">${config.subtitulo}</p>
        </div>
        <div class="actions d-flex gap-2">
          ${config.botonAgregar}
        </div>
      </div>
      <div class="table-wrap table-responsive">
        <table class="realtime-table table table-striped table-hover align-middle">
          <thead class="table-dark">
            <tr>${config.columnas.map(c => `<th>${c}</th>`).join('')}</tr>
          </thead>
          <tbody id="adminTableBody">
            ${config.filas}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

function actualizarDOMTabla(modulo) {
  if (adminTab !== modulo) return;
  const container = document.getElementById('adminContent');
  if (container) {
    container.innerHTML = adminContent();
  }
}

function actualizarDOMResumen(res) {
  const statsElem = document.getElementById('resumenStats');
  if (statsElem && res) {
    statsElem.innerHTML = `
      <div class="stat"><b>${res.totalProductos || 0}</b><span>Productos en MySQL</span></div>
      <div class="stat"><b>${res.totalPedidos || 0}</b><span>Pedidos Totales</span></div>
      <div class="stat"><b>S/ ${Number(res.totalVentas || 0).toFixed(2)}</b><span>Ventas Totales</span></div>
      <div class="stat"><b>${res.productosBajoStock || 0}</b><span>Alerta Stock Crítico</span></div>
    `;
  }
}

// ===================================================================
// ACCIONES EN TIEMPO REAL DIRECTO A MYSQL
// ===================================================================

// 1. PRODUCTOS: Editar
async function editarProductoMySQL(id) {
  const p = datosMySQL.productos.find(x => x.id === id) || (get('productos').find(x => x.id === id));
  if (!p) return;

  const nuevoNombre = prompt('Nombre del producto:', p.nombre);
  if (nuevoNombre === null) return;

  const nuevoPrecio = prompt('Precio de venta (S/):', p.precio);
  if (nuevoPrecio === null || isNaN(Number(nuevoPrecio))) return toast('Precio inválido');

  const nuevoStock = prompt('Stock disponible en almacén:', p.stock);
  if (nuevoStock === null || isNaN(Number(nuevoStock))) return toast('Stock inválido');

  try {
    toast('Guardando en MySQL...');
    await API.actualizarProducto(id, {
      nombre: nuevoNombre,
      precio: Number(nuevoPrecio),
      stock: Number(nuevoStock)
    });
    toast('✅ Producto actualizado en MySQL en tiempo real');
    await sincronizarModuloActual(true);
    if (typeof sincronizarConBackend === 'function') sincronizarConBackend();
  } catch (err) {
    alert('Error al actualizar en MySQL: ' + err.message);
  }
}

// 1. PRODUCTOS: Ajuste rápido de stock (+ / -)
async function ajustarStockRapido(id, nombre, stockActual) {
  const cantidadStr = prompt(`⚡ Ajuste de stock para:\n"${nombre}"\n\nStock actual en MySQL: ${stockActual}\nIngresa la cantidad a SUMAR (ej: 5) o RESTAR (ej: -2):`);
  if (cantidadStr === null) return;

  const cant = Number(cantidadStr);
  if (isNaN(cant) || cant === 0) return toast('Cantidad inválida');

  const tipo = cant > 0 ? 'ENTRADA' : 'SALIDA';
  const cantAbs = Math.abs(cant);
  const motivo = prompt('Motivo del movimiento en Kardex:', cant > 0 ? 'Ingreso de mercadería' : 'Merma / Ajuste físico');
  if (motivo === null) return;

  try {
    toast('Registrando movimiento en MySQL...');
    await API.ajustarStock(id, tipo, cantAbs, motivo);
    toast(`✅ Stock actualizado en MySQL: ${tipo === 'ENTRADA' ? '+' : '-'}${cantAbs}`);
    await sincronizarModuloActual(true);
    if (typeof sincronizarConBackend === 'function') sincronizarConBackend();
  } catch (err) {
    alert('Error al ajustar stock en MySQL: ' + err.message);
  }
}

// 1. PRODUCTOS: Eliminar/Desactivar
async function eliminarProductoMySQL(id) {
  if (!confirm(`¿Estás seguro de desactivar el producto #${id} de MySQL?`)) return;
  try {
    await API.eliminarProducto(id);
    toast('✅ Producto desactivado en MySQL');
    await sincronizarModuloActual(true);
    if (typeof sincronizarConBackend === 'function') sincronizarConBackend();
  } catch (err) {
    alert('Error al eliminar en MySQL: ' + err.message);
  }
}

// 1. PRODUCTOS: Modal nuevo producto
async function modalNuevoProducto() {
  const nombre = prompt('Nombre del producto:');
  if (!nombre) return;
  const precio = prompt('Precio normal (S/):', '999.00');
  if (!precio || isNaN(Number(precio))) return;
  const oferta = prompt('Precio de oferta (0 si no tiene):', '0');
  const stock = prompt('Stock inicial:', '10');
  const icon = prompt('Emoji / Ícono:', '📦');

  try {
    toast('Guardando nuevo producto en MySQL...');
    await API.guardarProducto({
      nombre: nombre,
      sku: 'EFE-' + Date.now().toString().slice(-4),
      modelo: 'Modelo 2026',
      precio: Number(precio),
      oferta: Number(oferta) || 0,
      stock: Number(stock) || 0,
      stockMinimo: 3,
      icon: icon || '📦',
      descripcion: 'Producto agregado desde el panel de control en tiempo real.',
      categoria: { id: 1 },
      marca: { id: 1 }
    });
    toast('✅ ¡Nuevo producto guardado en MySQL!');
    await sincronizarModuloActual(true);
    if (typeof sincronizarConBackend === 'function') sincronizarConBackend();
  } catch (err) {
    alert('Error al guardar en MySQL: ' + err.message);
  }
}

// 2. PEDIDOS: Cambiar Estado
async function cambiarEstadoPedidoMySQL(id, nuevoEstado) {
  if (!nuevoEstado) return;
  try {
    toast(`Cambiando pedido #${id} a "${nuevoEstado}" en MySQL...`);
    await API.cambiarEstadoPedido(id, nuevoEstado);
    toast(`✅ Pedido #${id} actualizado a "${nuevoEstado}" en MySQL`);
    await sincronizarModuloActual(true);
  } catch (err) {
    alert('Error al cambiar estado en MySQL: ' + err.message);
  }
}

// 3. CLIENTES: Editar
async function editarClienteMySQL(id) {
  const c = datosMySQL.clientes.find(x => x.id === id);
  if (!c) return;

  const nombres = prompt('Nombres:', c.nombres);
  if (nombres === null) return;
  const apellidos = prompt('Apellidos:', c.apellidos);
  if (apellidos === null) return;
  const telefono = prompt('Teléfono:', c.telefono || '');
  if (telefono === null) return;
  const correo = prompt('Correo:', c.correo || '');
  if (correo === null) return;
  const direccion = prompt('Dirección:', c.direccion || '');
  if (direccion === null) return;

  try {
    toast('Actualizando cliente en MySQL...');
    await API.actualizarCliente(id, {
      nombres, apellidos, telefono, correo, direccion
    });
    toast('✅ Cliente actualizado en MySQL en tiempo real');
    await sincronizarModuloActual(true);
  } catch (err) {
    alert('Error al actualizar cliente en MySQL: ' + err.message);
  }
}

// 3. CLIENTES: Nuevo Cliente
async function modalNuevoCliente() {
  const dni = prompt('Número de DNI / RUC:');
  if (!dni) return;
  const nombres = prompt('Nombres:');
  if (!nombres) return;
  const apellidos = prompt('Apellidos:');
  if (!apellidos) return;
  const telefono = prompt('Teléfono:', '999888777');
  const correo = prompt('Correo:', 'cliente@email.com');
  const direccion = prompt('Dirección:', 'Lima, Perú');

  try {
    toast('Registrando cliente en MySQL...');
    await API.crearCliente({
      tipoDocumento: dni.length === 11 ? 'RUC' : 'DNI',
      numeroDocumento: dni,
      nombres: nombres,
      apellidos: apellidos,
      telefono: telefono,
      correo: correo,
      direccion: direccion
    });
    toast('✅ Cliente registrado en MySQL');
    await sincronizarModuloActual(true);
  } catch (err) {
    alert('Error al crear cliente en MySQL: ' + err.message);
  }
}

// 4. KARDEX: Modal ajuste de stock
async function modalAjusteStock() {
  const prods = datosMySQL.productos.length ? datosMySQL.productos : get('productos');
  const listaTexto = prods.map(p => `#${p.id}: ${p.nombre} (Stock: ${p.stock})`).join('\n');
  const idStr = prompt(`Selecciona el ID del producto:\n\n${listaTexto}`);
  if (!idStr) return;

  const prodId = Number(idStr);
  const prod = prods.find(p => p.id === prodId);
  if (!prod) return toast('Producto no encontrado');

  const tipo = prompt('Tipo de movimiento (ENTRADA, SALIDA o AJUSTE):', 'ENTRADA');
  if (!tipo) return;

  const cantStr = prompt('Cantidad:', '5');
  if (!cantStr || isNaN(Number(cantStr))) return;

  const motivo = prompt('Motivo del movimiento:', 'Ajuste manual de inventario');

  try {
    toast('Guardando en Kardex de MySQL...');
    await API.ajustarStock(prodId, tipo.toUpperCase(), Number(cantStr), motivo);
    toast('✅ Movimiento registrado en MySQL y stock actualizado');
    await sincronizarModuloActual(true);
    if (typeof sincronizarConBackend === 'function') sincronizarConBackend();
  } catch (err) {
    alert('Error al registrar movimiento en MySQL: ' + err.message);
  }
}