/* Página: inicio */

function renderHome() {
  let categorias = lista('tpl-categoria-card', get('categorias'), function (c) {
    return { id: c.id, nombre: c.nombre, foto: productImages[c.id] || productImages[1] };
  });

  let destacados = get('productos').slice(0, 6).map(productCard).join('');

  return plantilla('tpl-inicio', {
    categorias: categorias,
    destacados: destacados,
    fotoCelular: productImages[1],
    fotoTv: productImages[3],
    fotoRefri: productImages[4]
  });
}

// Desde el inicio: abre el catálogo ya filtrado por categoría
function filterCategory(id) {
  go('catalogo');
  $('#categoryFilter').value = id;
  renderCatalog();
}
