/* Página: detalle de producto */

let fotosActuales = [];   // fotos del producto que se está viendo

function openProduct(id) {
  set('productoVisto', id);
  go('producto');
}

function renderProductPage() {
  let id = get('productoVisto');
  let productos = get('productos');
  let p = productos.find(function (x) { return x.id === id; }) || productos[0];

  if (!p) {
    return plantilla('tpl-vacio', { mensaje: 'Producto no encontrado.' });
  }

  let marca = get('marcas').find(function (m) { return m.id === p.marca; });
  let precio = p.oferta || p.precio;
  fotosActuales = fotosDe(p);

  let miniaturas = fotosActuales.map(function (foto, i) {
    return plantilla('tpl-miniatura', {
      foto: foto,
      indice: i,
      numero: i + 1,
      activa: i === 0 ? 'active' : ''
    });
  }).join('');

  let garantias = lista('tpl-opcion-extra', get('garantias'), function (g) {
    return { grupo: 'garantia', id: g.id, nombre: g.nombre, precio: money(g.precio) };
  });

  let servicios = lista('tpl-opcion-extra', get('servicios'), function (s) {
    return { grupo: 'servicio', id: s.id, nombre: s.nombre, precio: money(s.precio) };
  });

  return plantilla('tpl-producto-detalle', {
    id: p.id,
    nombre: p.nombre,
    marca: marca ? marca.nombre : 'Marca',
    sku: p.sku || 'EFE-' + p.id,
    descripcion: p.descripcion || 'Conoce este producto y disfruta de la calidad que Tiendas EFE tiene para ti.',
    precio: money(precio),
    precioViejo: money(p.precio),
    claseViejo: p.oferta ? '' : 'hidden',
    textoDescuento: p.oferta ? '-' + Math.round((1 - p.oferta / p.precio) * 100) + '%' : '',
    claseDescuento: p.oferta ? '' : 'hidden',
    cuota: money(precio / 9.6),
    corazon: esFavorito(p.id) ? '♥' : '♡',
    foto: fotosActuales[0],
    miniaturas: miniaturas,
    garantias: garantias,
    servicios: servicios
  });
}

// Cambia la foto grande al hacer clic en una miniatura
function changeProductPhoto(indice) {
  let foto = $('#mainProductPhoto');
  if (!foto || !fotosActuales[indice]) return;

  foto.src = fotosActuales[indice];
  document.querySelectorAll('.thumbs .thumb').forEach(function (boton, i) {
    boton.classList.toggle('active', i === indice);
  });
}

// Botones + y - de cantidad (entre 1 y 20)
function detailQty(cambio) {
  let el = $('#detailQty');
  if (!el) return;
  let nueva = Number(el.textContent) + cambio;
  el.textContent = Math.max(1, Math.min(20, nueva));
}

function addDetailCart(id) {
  let cantidad = Number($('#detailQty').textContent) || 1;
  addCart(id, cantidad);
}

function esFavorito(id) {
  return get('favoritos').includes(id);
}

function toggleFavorite(id, boton) {
  let favoritos = get('favoritos');
  if (favoritos.includes(id)) {
    favoritos = favoritos.filter(function (x) { return x !== id; });
    boton.textContent = '♡';
  } else {
    favoritos.push(id);
    boton.textContent = '♥';
  }
  set('favoritos', favoritos);
  toast('Favoritos actualizados');
}
