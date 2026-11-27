/* Función render y arranque */

// Dibuja la página actual dentro de <main id="app">
function render() {
  updateHeader();
  let app = $('#app');

  if (currentPage === 'inicio') {
    app.innerHTML = renderHome();

  } else if (currentPage === 'catalogo') {
    app.innerHTML = renderCatalogPage();
    renderCatalog();

  } else if (currentPage === 'producto') {
    app.innerHTML = renderProductPage();

  } else if (currentPage === 'login' || currentPage === 'registro') {
    app.innerHTML = formPage(currentPage);
    $('#authForm').onsubmit = function (e) {
      authSubmit(e, currentPage === 'registro');
    };

  } else if (currentPage === 'carrito') {
    app.innerHTML = renderCart();

  } else if (currentPage === 'checkout') {
    app.innerHTML = renderCheckout();
    let form = $('#checkoutForm');
    if (form) form.onsubmit = checkoutSubmit;

  } else if (currentPage === 'pedidos') {
    app.innerHTML = renderOrders();

  } else if (currentPage === 'admin') {
    app.innerHTML = renderAdmin();
  }
}

// Sincronización automática con la base de datos MySQL (Single Source of Truth)
async function sincronizarConBackend(verbose) {
  if (typeof API !== 'undefined') {
    try {
      const productosBD = await API.listarProductos();
      if (productosBD && productosBD.length > 0) {
        const mapeados = productosBD.map(function (p) {
          return {
            id: p.id,
            nombre: p.nombre,
            categoria: p.categoriaId || (p.categoria ? p.categoria.id : 1),
            marca: p.marcaId || (p.marca ? p.marca.id : 1),
            modelo: p.modelo || '',
            sku: p.sku || '',
            precio: Number(p.precio),
            oferta: Number(p.oferta) || 0,
            stock: Number(p.stock),
            icon: p.icon || '📦',
            descripcion: p.descripcion || ''
          };
        });
        set('productos', mapeados);
        render();
        if (verbose) {
          console.log('✅ Tiendas EFE: Base de datos sincronizada (' + productosBD.length + ' productos en vivo)');
        }
      }
    } catch (e) {
      console.warn('Aviso: Conectando con modo local.');
    }
  }
}

// Arranque inicial limpio
cargarDatosIniciales();
render();
sincronizarConBackend(true);
