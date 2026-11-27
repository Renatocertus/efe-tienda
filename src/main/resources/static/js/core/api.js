/* ===================================================================
   MÓDULO DE COMUNICACIÓN CON SPRING BOOT REST API
   =================================================================== */

const API = {
  baseUrl: '', // Vía relativa al servidor Spring Boot en localhost:8080

  async get(endpoint) {
    try {
      const res = await fetch(this.baseUrl + endpoint);
      if (!res.ok) {
        const errText = await res.text();
        console.error('❌ Error de servidor ' + res.status + ' en GET ' + endpoint + ':', errText);
        throw new Error('HTTP ' + res.status + ': ' + errText);
      }
      return await res.json();
    } catch (e) {
      console.warn('API GET falló en ' + endpoint + ', usando datos locales:', e);
      return null;
    }
  },

  async post(endpoint, data) {
    try {
      const res = await fetch(this.baseUrl + endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      if (!res.ok) {
        const errText = await res.text();
        console.error('❌ Error de servidor ' + res.status + ' en POST ' + endpoint + ':', errText);
        throw new Error('HTTP ' + res.status + ': ' + errText);
      }
      return await res.json();
    } catch (e) {
      console.error('API POST error en ' + endpoint + ':', e);
      throw e;
    }
  },

  async put(endpoint, data) {
    try {
      const res = await fetch(this.baseUrl + endpoint, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      if (!res.ok) throw new Error('HTTP ' + res.status);
      return await res.json();
    } catch (e) {
      console.error('API PUT error en ' + endpoint + ':', e);
      throw e;
    }
  },

  // 1. Catálogo y Productos
  async listarProductos(categoriaId, busqueda) {
    let url = '/api/productos';
    const params = [];
    if (categoriaId) params.push('categoria=' + categoriaId);
    if (busqueda) params.push('buscar=' + encodeURIComponent(busqueda));
    if (params.length) url += '?' + params.join('&');
    return await this.get(url);
  },

  async obtenerProducto(id) {
    return await this.get('/api/productos/' + id);
  },

  async guardarProducto(producto) {
    return await this.post('/api/productos', producto);
  },

  async actualizarProducto(id, datos) {
    return await this.put('/api/productos/' + id, datos);
  },

  async eliminarProducto(id) {
    const res = await fetch(this.baseUrl + '/api/productos/' + id, { method: 'DELETE' });
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return true;
  },

  async ajustarStock(productoId, tipo, cantidad, motivo) {
    return await this.post('/api/productos/stock/ajuste', {
      productoId: productoId,
      tipo: tipo, // ENTRADA, SALIDA, AJUSTE
      cantidad: cantidad,
      motivo: motivo,
      usuarioResponsable: (getUsuarioActual() || {}).nombres || 'Admin'
    });
  },

  async listarMovimientosStock(productoId) {
    let url = '/api/productos/stock/movimientos';
    if (productoId) url += '?productoId=' + productoId;
    return await this.get(url);
  },

  // 2. Ventas y Facturación
  async procesarVenta(ventaData) {
    return await this.post('/api/ventas/procesar', ventaData);
  },

  async listarPedidos(clienteId) {
    let url = '/api/ventas/pedidos';
    if (clienteId) url += '?clienteId=' + clienteId;
    return await this.get(url);
  },

  async cambiarEstadoPedido(pedidoId, nuevoEstado) {
    return await this.put('/api/ventas/pedidos/' + pedidoId + '/estado', { estado: nuevoEstado });
  },

  async listarComprobantes() {
    return await this.get('/api/ventas/comprobantes');
  },

  // 3. Clientes (MySQL)
  async listarClientes() {
    return await this.get('/api/clientes');
  },

  async actualizarCliente(id, datos) {
    return await this.put('/api/clientes/' + id, datos);
  },

  async crearCliente(clienteData) {
    return await this.post('/api/clientes', clienteData);
  },

  // 4. Usuarios, Seguridad y Personal
  async login(username, password) {
    return await this.post('/api/usuarios/login', { username, password });
  },

  async registrar(usuarioData) {
    return await this.post('/api/usuarios/registro', usuarioData);
  },

  async listarPersonal() {
    return await this.get('/api/usuarios/personal');
  },

  // 4. Dashboard y Estado
  async obtenerDashboard() {
    return await this.get('/api/dashboard/resumen');
  },

  async obtenerEstadoConexion() {
    return await this.get('/api/dashboard/estado-conexion');
  }
};
