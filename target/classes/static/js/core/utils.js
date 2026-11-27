/* Funciones de ayuda */

// Atajo para buscar un elemento
function $(selector) {
  return document.querySelector(selector);
}

// Da formato de soles: 12.5 -> "S/ 12.50"
function money(n) {
  return 'S/ ' + Number(n || 0).toFixed(2);
}

// Lee una lista guardada en localStorage
function get(nombre, porDefecto = []) {
  try {
    let dato = JSON.parse(localStorage.getItem('efe_' + nombre));
    return dato === null ? porDefecto : dato;
  } catch (error) {
    return porDefecto;
  }
}

// Guarda un dato en localStorage
function set(nombre, valor) {
  localStorage.setItem('efe_' + nombre, JSON.stringify(valor));
}

// Genera un id único
function uid() {
  return Date.now() + Math.floor(Math.random() * 1000);
}

// Evita que el texto escrito por el usuario rompa el HTML
function esc(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}

// Mensaje flotante que desaparece solo
function toast(texto) {
  let el = $('#toast');
  el.textContent = texto;
  el.classList.add('show');
  setTimeout(function () {
    el.classList.remove('show');
  }, 2400);
}


// Dibuja una plantilla HTML (las plantillas están en templates/).
// {{campo}} se reemplaza con el valor escapado y {{{campo}}} con HTML ya armado.
// Dentro de una tabla se escribe <!--{{{campo}}}--> para que el navegador no lo mueva.
function plantilla(id, datos = {}) {
  let molde = document.getElementById(id);
  if (!molde) {
    console.error('No existe la plantilla ' + id);
    return '';
  }
  let patron = /<!--\{\{\{(\w+)\}\}\}-->|\{\{\{(\w+)\}\}\}|\{\{(\w+)\}\}/g;
  return molde.innerHTML.replace(patron, function (todo, enTabla, crudo, normal) {
    if (enTabla) return datos[enTabla] ?? '';
    if (crudo) return datos[crudo] ?? '';
    return esc(datos[normal]);
  });
}

// Repite una plantilla por cada elemento de una lista
function lista(id, items, convertir) {
  return items.map(function (item) {
    return plantilla(id, convertir ? convertir(item) : item);
  }).join('');
}

// Si una foto .jpg no existe, muestra el .svg del mismo nombre
function imgFallback(img) {
  img.onerror = null;
  img.src = img.src.replace(/\.jpg$/, '.svg');
}
