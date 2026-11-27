/* Componente: tarjeta de producto (se usa en inicio y catálogo) */

// Prepara los datos de un producto para la plantilla tpl-producto-card
function datosTarjeta(p) {
  let categoria = get('categorias').find(function (c) { return c.id === p.categoria; });
  let marca = get('marcas').find(function (m) { return m.id === p.marca; });

  return {
    id: p.id,
    nombre: p.nombre,
    foto: fotosDe(p)[0],
    categoria: categoria ? categoria.nombre : 'Producto',
    marca: marca ? marca.nombre : '',
    precio: money(p.oferta || p.precio),
    precioViejo: money(p.precio),
    claseViejo: p.oferta ? '' : 'hidden',
    stock: p.stock
  };
}

function productCard(p) {
  return plantilla('tpl-producto-card', datosTarjeta(p));
}
