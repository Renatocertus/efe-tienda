/* Datos iniciales */

// Foto de cada producto (si falta el .jpg se usa el .svg del mismo nombre)
const productImages = {
  1: 'imagenes/productos/a55-frontal-con-ficha.jpg',
  2: 'imagenes/productos/laptop-lenovo.jpg',
  3: 'imagenes/productos/smart-tv-lg.jpg',
  4: 'imagenes/productos/refrigeradora-samsung.jpg',
  5: 'imagenes/productos/audifonos-sony.jpg',
  6: 'imagenes/productos/iphone-13.jpg'
};

// Fotos extra del Galaxy A55 (galería del detalle)
const a55Photos = [
  'imagenes/productos/a55-frontal-con-ficha.jpg',
  'imagenes/productos/a55-trasera.jpg',
  'imagenes/productos/a55-frontal-diagonal.jpg',
  'imagenes/productos/a55-trasera-diagonal.jpg',
  'imagenes/productos/a55-vista-lateral.jpg',
  'imagenes/productos/a55-detalle-camaras.jpg'
];

// Fotos de un producto: el A55 tiene 6, los demás solo una
function fotosDe(p) {
  if (p.id === 1) return a55Photos;
  return [productImages[p.id] || 'imagenes/productos/laptop-lenovo.jpg'];
}

// Crea los datos de ejemplo solo la primera vez
function cargarDatosIniciales() {
  if (!get('roles').length) {
    set('roles', [
      { id: 1, nombre: 'Cliente' },
      { id: 2, nombre: 'Administrador' }
    ]);
  }

  if (!get('categorias').length) {
    set('categorias', [
      { id: 1, nombre: 'Celulares', icon: '📱' },
      { id: 2, nombre: 'Laptops', icon: '💻' },
      { id: 3, nombre: 'Televisores', icon: '📺' },
      { id: 4, nombre: 'Electrohogar', icon: '🧊' },
      { id: 5, nombre: 'Audio', icon: '🎧' }
    ]);
  }

  if (!get('marcas').length) {
    let nombres = ['Samsung', 'LG', 'Apple', 'Motorola', 'Xiaomi', 'Sony'];
    set('marcas', nombres.map(function (nombre, i) {
      return { id: i + 1, nombre: nombre };
    }));
  }

  if (!get('metodos').length) {
    set('metodos', [
      { id: 1, nombre: 'Delivery', costo: 15 },
      { id: 2, nombre: 'Recojo en tienda', costo: 0 }
    ]);
  }

  if (!get('garantias').length) {
    set('garantias', [
      { id: 1, nombre: 'Garantía extendida 1 año', meses: 12, precio: 49.9 },
      { id: 2, nombre: 'Garantía extendida 2 años', meses: 24, precio: 89.9 }
    ]);
  }

  if (!get('servicios').length) {
    set('servicios', [
      { id: 1, nombre: 'Instalación', precio: 39.9 },
      { id: 2, nombre: 'Configuración inicial', precio: 29.9 }
    ]);
  }

  if (!get('productos').length) {
    set('productos', [
      { id: 1, nombre: 'Samsung Galaxy A55 5G', categoria: 1, marca: 1, modelo: 'A55 5G', sku: 'EFE-001', precio: 1499, oferta: 1299, stock: 15, icon: '📱', descripcion: 'Smartphone Samsung con gran pantalla y cámara.' },
      { id: 2, nombre: 'Laptop Lenovo IdeaPad', categoria: 2, marca: 4, modelo: 'IdeaPad 3', sku: 'EFE-002', precio: 2299, oferta: 0, stock: 8, icon: '💻', descripcion: 'Laptop para estudio y trabajo.' },
      { id: 3, nombre: 'Smart TV LG 55 pulgadas', categoria: 3, marca: 2, modelo: 'UHD 55', sku: 'EFE-003', precio: 1899, oferta: 1749, stock: 6, icon: '📺', descripcion: 'Televisor Smart TV 4K.' },
      { id: 4, nombre: 'Refrigeradora Samsung', categoria: 4, marca: 1, modelo: 'RT38', sku: 'EFE-004', precio: 2499, oferta: 0, stock: 4, icon: '🧊', descripcion: 'Refrigeradora No Frost.' },
      { id: 5, nombre: 'Audífonos Sony', categoria: 5, marca: 6, modelo: 'WH-CH520', sku: 'EFE-005', precio: 199, oferta: 169, stock: 30, icon: '🎧', descripcion: 'Audífonos inalámbricos.' },
      { id: 6, nombre: 'iPhone 13', categoria: 1, marca: 3, modelo: '13', sku: 'EFE-006', precio: 2599, oferta: 2399, stock: 5, icon: '📱', descripcion: 'Teléfono Apple.' }
    ]);
  }

  if (!get('usuarios').length) {
    set('usuarios', [
      { id: 1, rol: 2, nombres: 'Administrador', apellidos: 'EFE', correo: 'admin@efe.com', password: 'admin123', telefono: '', estado: 'Activo' }
    ]);
  }

  // Tablas que empiezan vacías
  let vacias = ['clientes', 'direcciones', 'carritos', 'carritoDetalle', 'pedidos', 'pedidoDetalle', 'adicionales', 'inventario'];
  vacias.forEach(function (nombre) {
    if (localStorage.getItem('efe_' + nombre) === null) {
      set(nombre, []);
    }
  });
}
