/* Sesión, menú y buscador */

let currentPage = 'inicio';
let adminTab = 'resumen';

// En la sesión solo guardamos el id del usuario (no la contraseña)
function user() {
  let id = get('sesion', null);
  if (!id) return null;
  return get('usuarios').find(function (u) { return u.id === id; }) || null;
}

function esAdmin() {
  let u = user();
  return u !== null && u.rol === 2;
}

// Cambia de página
function go(pagina) {
  currentPage = pagina;
  render();
  window.scrollTo(0, 0);
}

// Cualquier elemento con data-page navega a esa página
document.addEventListener('click', function (e) {
  let enlace = e.target.closest('[data-page]');
  if (enlace) {
    e.preventDefault();
    $('#mainNav').classList.remove('open');
    go(enlace.dataset.page);
  }
});

// Menú hamburguesa (celular)
$('#menuBtn').onclick = function () {
  $('#mainNav').classList.toggle('open');
};

// Buscador
function doSearch() {
  if (currentPage === 'catalogo') {
    renderCatalog();
  } else {
    go('catalogo');
  }
}

$('#searchBtn').onclick = doSearch;

$('#searchInput').addEventListener('keydown', function (e) {
  if (e.key === 'Enter') doSearch();
});

$('#searchInput').addEventListener('input', function () {
  if (currentPage === 'catalogo') renderCatalog();
});

// Actualiza la cabecera (carrito, nombre de usuario, menú admin)
function updateHeader() {
  let u = user();
  let cantidad = 0;

  if (u) {
    let carrito = get('carritos').find(function (c) {
      return c.usuario === u.id && c.estado === 'Activo';
    });
    if (carrito) {
      get('carritoDetalle').forEach(function (d) {
        if (d.carrito === carrito.id) cantidad += d.cantidad;
      });
    }
  }
  $('#cartCount').textContent = cantidad;

  if (u) {
    $('#accountArea').innerHTML = plantilla('tpl-cuenta-sesion', { nombres: u.nombres });
  } else {
    $('#accountArea').innerHTML = plantilla('tpl-cuenta-visitante');
  }

  // El enlace de administración siempre visible para la demo del prototipo
  $('#adminNav').classList.remove('hidden');
}
