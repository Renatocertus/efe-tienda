/* Página: catálogo de productos */

function renderCatalogPage() {
  let categorias = lista('tpl-opcion', get('categorias'), function (c) {
    return { valor: c.id, texto: c.nombre };
  });
  return plantilla('tpl-catalogo', { categorias: categorias });
}

// Muestra los productos según el buscador y la categoría elegida
function renderCatalog() {
  let texto = $('#searchInput').value.toLowerCase();
  let categoria = $('#categoryFilter') ? $('#categoryFilter').value : '';

  let productos = get('productos').filter(function (p) {
    let coincideTexto = !texto ||
      p.nombre.toLowerCase().includes(texto) ||
      (p.sku || '').toLowerCase().includes(texto);
    let coincideCategoria = !categoria || p.categoria == categoria;
    return coincideTexto && coincideCategoria;
  });

  $('#catalogResults').innerHTML = productos.length
    ? productos.map(productCard).join('')
    : plantilla('tpl-vacio', { mensaje: 'No se encontraron productos.' });
}
