/* Carrito */

// Busca el carrito activo del usuario (o null)
function carritoActivo(u) {
  return get('carritos').find(function (c) {
    return c.usuario === u.id && c.estado === 'Activo';
  }) || null;
}

function addCart(id, cantidad = 1) {
  let u = user();
  if (!u) {
    toast('Inicia sesión para agregar productos');
    go('login');
    return;
  }

  let producto = get('productos').find(function (p) { return p.id === id; });
  if (!producto) return;

  // Si no tiene carrito activo, se crea uno
  let carrito = carritoActivo(u);
  if (!carrito) {
    carrito = { id: uid(), usuario: u.id, estado: 'Activo' };
    set('carritos', get('carritos').concat(carrito));
  }

  let detalle = get('carritoDetalle');
  let linea = detalle.find(function (d) { return d.carrito === carrito.id && d.producto === id; });
  let actual = linea ? linea.cantidad : 0;

  // No se puede pedir más de lo que hay en stock
  if (actual + cantidad > Number(producto.stock)) {
    toast('Stock insuficiente: quedan ' + producto.stock + ' unidades');
    return;
  }

  if (linea) {
    linea.cantidad += cantidad;
  } else {
    detalle.push({ id: uid(), carrito: carrito.id, producto: id, cantidad: cantidad });
  }
  set('carritoDetalle', detalle);

  updateHeader();
  toast('Producto agregado al carrito');
}

// Devuelve las líneas del carrito con los datos del producto
function lineasDelCarrito(u) {
  let carrito = carritoActivo(u);
  if (!carrito) return [];

  let lineas = [];
  get('carritoDetalle').forEach(function (d) {
    if (d.carrito !== carrito.id) return;
    let producto = get('productos').find(function (p) { return p.id === d.producto; });
    if (producto) lineas.push({ id: d.id, cantidad: d.cantidad, producto: producto });
  });
  return lineas;
}

function renderCart() {
  let u = user();
  if (!u) {
    return plantilla('tpl-aviso', { mensaje: 'Inicia sesión para ver tu carrito.' });
  }

  let lineas = lineasDelCarrito(u);
  let total = 0;

  let filasHtml = lineas.map(function (l) {
    let precio = l.producto.oferta || l.producto.precio;
    total += precio * l.cantidad;
    return plantilla('tpl-carrito-linea', {
      id: l.id,
      icono: l.producto.icon || '',
      nombre: l.producto.nombre,
      precio: money(precio),
      cantidad: l.cantidad,
      subtotal: money(precio * l.cantidad)
    });
  }).join('');

  if (!lineas.length) {
    filasHtml = plantilla('tpl-vacio', { mensaje: 'Tu carrito está vacío.' });
  }

  return plantilla('tpl-carrito', {
    lineas: filasHtml,
    total: money(total),
    deshabilitado: lineas.length ? '' : 'disabled'
  });
}

function changeQty(idLinea, cambio) {
  let detalle = get('carritoDetalle');
  let linea = detalle.find(function (d) { return d.id === idLinea; });
  if (!linea) return;

  let producto = get('productos').find(function (p) { return p.id === linea.producto; });
  let maximo = producto ? Number(producto.stock) : 99;

  if (cambio > 0 && linea.cantidad >= maximo) {
    toast('Stock máximo alcanzado');
  } else {
    linea.cantidad = Math.max(1, linea.cantidad + cambio);
  }

  set('carritoDetalle', detalle);
  render();
}

function removeLine(idLinea) {
  set('carritoDetalle', get('carritoDetalle').filter(function (d) { return d.id !== idLinea; }));
  render();
}
