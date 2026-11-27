/* Pedidos */

function renderCheckout() {
  let u = user();
  if (!u) return formPage('login');

  let cliente = get('clientes').find(function (c) { return c.usuario === u.id; });
  let direcciones = get('direcciones').filter(function (d) { return cliente && d.cliente === cliente.id; });

  let total = 0;
  lineasDelCarrito(u).forEach(function (l) {
    total += (l.producto.oferta || l.producto.precio) * l.cantidad;
  });

  return plantilla('tpl-checkout', {
    total: money(total),
    direcciones: lista('tpl-opcion', direcciones, function (d) {
      return { valor: d.id, texto: d.distrito + ' - ' + d.direccion };
    }),
    metodos: lista('tpl-opcion', get('metodos'), function (m) {
      return { valor: m.id, texto: m.nombre + ' - ' + money(m.costo) };
    })
  });
}

function checkoutSubmit(e) {
  e.preventDefault();
  let u = user();
  let datos = Object.fromEntries(new FormData(e.target));

  // 1. Buscar (o crear) el cliente
  let cliente = get('clientes').find(function (c) { return c.usuario === u.id; });
  if (!cliente) {
    cliente = { id: uid(), usuario: u.id, dni: '' };
    set('clientes', get('clientes').concat(cliente));
  }

  // 2. Validar carrito
  let lineas = lineasDelCarrito(u);
  if (!lineas.length) {
    toast('El carrito está vacío');
    return;
  }

  // 3. Dirección: la guardada o una nueva
  let idDireccion = Number(datos.direccion) || null;
  if (!idDireccion) {
    if (!datos.direccionNueva.trim() || !datos.distrito.trim()) {
      toast('Selecciona o escribe una dirección de entrega');
      return;
    }
    let nueva = {
      id: uid(),
      cliente: cliente.id,
      departamento: datos.departamento,
      provincia: datos.provincia,
      distrito: datos.distrito,
      direccion: datos.direccionNueva
    };
    set('direcciones', get('direcciones').concat(nueva));
    idDireccion = nueva.id;
  }

  // 4. Calcular totales
  let detalles = lineas.map(function (l) {
    return {
      id: uid(),
      producto: l.producto.id,
      nombre: l.producto.nombre,
      cantidad: l.cantidad,
      precio: l.producto.oferta || l.producto.precio
    };
  });

  let subtotal = 0;
  detalles.forEach(function (d) { subtotal += d.precio * d.cantidad; });

  let metodo = get('metodos').find(function (m) { return m.id === Number(datos.metodo); });
  let costoEnvio = metodo ? metodo.costo : 0;

  // 5. Guardar el pedido
  let pedido = {
    id: uid(),
    codigo: 'EFE-' + Date.now().toString().slice(-7),
    cliente: cliente.id,
    usuario: u.id,
    direccion: idDireccion,
    metodo: Number(datos.metodo),
    fecha: new Date().toLocaleString(),
    subtotal: subtotal,
    costo: costoEnvio,
    total: subtotal + costoEnvio,
    estado: 'PAGO APROBADO',
    observaciones: datos.obs
  };
  set('pedidos', [pedido].concat(get('pedidos')));

  let detallesPedido = detalles.map(function (d) {
    d.pedido = pedido.id;
    return d;
  });
  set('pedidoDetalle', get('pedidoDetalle').concat(detallesPedido));

  // 6. Cerrar el carrito y borrar sus productos
  let carrito = carritoActivo(u);
  set('carritos', get('carritos').map(function (c) {
    if (c.id === carrito.id) c.estado = 'Convertido';
    return c;
  }));
  set('carritoDetalle', get('carritoDetalle').filter(function (d) { return d.carrito !== carrito.id; }));

  // 7. Descontar stock
  let productos = get('productos');
  detalles.forEach(function (d) {
    let p = productos.find(function (x) { return x.id === d.producto; });
    if (p) p.stock = Math.max(0, Number(p.stock) - d.cantidad);
  });
  set('productos', productos);

  toast('¡Compra exitosa! Pago Aprobado ✅');

  // Sincronizar automáticamente con MySQL a través de Spring Boot
  if (typeof API !== 'undefined') {
    API.procesarVenta({
      tipoDocumento: 'DNI',
      numeroDocumento: cliente.dni || ('DNI' + Date.now().toString().slice(-6)),
      nombres: u.nombres || 'Cliente',
      apellidos: u.apellidos || 'EFE',
      correo: u.correo || 'cliente@tienda.com',
      telefono: u.telefono || '999888777',
      direccion: datos.direccionNueva || 'Lima, Perú',
      metodoEntrega: metodo ? metodo.nombre : 'Delivery',
      direccionEnvio: datos.direccionNueva || 'Lima, Perú',
      costoEnvio: costoEnvio,
      items: detalles.map(function (d) { return { productoId: Number(d.producto), cantidad: Number(d.cantidad) }; }),
      tipoComprobante: 'BOLETA'
    }).then(function (res) {
      console.log('✅ Venta registrada en MySQL con éxito:', res);
      if (typeof sincronizarConBackend === 'function') {
        sincronizarConBackend();
      }
    }).catch(function (err) {
      console.warn('Aviso: Servidor Spring Boot no respondió, pedido guardado solo local:', err);
    });
  }

  go('pedidos');
}

function renderOrders() {
  let u = user();
  if (!u) {
    return plantilla('tpl-aviso', { mensaje: 'Inicia sesión para consultar tus pedidos.' });
  }

  // El administrador ve todos los pedidos, el cliente solo los suyos
  let pedidos = get('pedidos').filter(function (p) {
    return esAdmin() || p.usuario === u.id;
  });

  let filas = lista('tpl-pedido-fila', pedidos, function (p) {
    let clase = (p.estado === 'PAGO APROBADO' || p.estado === 'Entregado') ? 'green' : '';
    return { id: p.id, codigo: p.codigo, fecha: p.fecha, total: money(p.total), estado: p.estado, claseEstado: clase };
  });

  if (!pedidos.length) {
    filas = plantilla('tpl-fila-vacia', { columnas: 5, mensaje: 'Aún no tienes pedidos.' });
  }

  return plantilla('tpl-pedidos', {
    titulo: esAdmin() ? 'Todos los pedidos' : 'Mis pedidos',
    filas: filas
  });
}

function showOrder(id) {
  let pedido = get('pedidos').find(function (p) { return p.id === id; });
  let detalles = get('pedidoDetalle').filter(function (d) { return d.pedido === id; });

  let texto = pedido.codigo + '\nEstado: ' + pedido.estado + '\n\n';
  detalles.forEach(function (d) {
    texto += d.nombre + ' x' + d.cantidad + ' - ' + money(d.precio * d.cantidad) + '\n';
  });
  texto += '\nEnvío: ' + money(pedido.costo);
  texto += '\nTotal: ' + money(pedido.total);

  alert(texto);
}
