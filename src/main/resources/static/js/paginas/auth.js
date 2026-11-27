/* Sesión (login y registro) */

// Muestra el formulario de login o de registro
function formPage(tipo) {
  return plantilla(tipo === 'registro' ? 'tpl-registro' : 'tpl-login');
}

function authSubmit(e, esRegistro) {
  e.preventDefault();
  let datos = Object.fromEntries(new FormData(e.target));
  let usuarios = get('usuarios');
  let correo = datos.correo.trim().toLowerCase();

  if (esRegistro) {
    // El correo no puede repetirse
    let existe = usuarios.some(function (u) { return u.correo.toLowerCase() === correo; });
    if (existe) {
      toast('Ese correo ya está registrado');
      return;
    }

    let nuevo = {
      id: uid(),
      rol: 1,
      nombres: datos.nombres.trim(),
      apellidos: datos.apellidos.trim(),
      correo: correo,
      password: datos.password,
      telefono: datos.telefono,
      estado: 'Activo'
    };
    usuarios.push(nuevo);
    set('usuarios', usuarios);

    let clientes = get('clientes');
    clientes.push({ id: uid(), usuario: nuevo.id, dni: datos.dni });
    set('clientes', clientes);

    set('sesion', nuevo.id);
    toast('Cuenta creada correctamente');
  } else {
    let encontrado = usuarios.find(function (u) {
      return u.correo.toLowerCase() === correo && u.password === datos.password && u.estado === 'Activo';
    });
    if (!encontrado) {
      toast('Correo o contraseña incorrectos');
      return;
    }
    set('sesion', encontrado.id);
    toast('Bienvenido');
  }

  go('inicio');
}

function logout() {
  set('sesion', null);
  toast('Sesión cerrada');
  go('inicio');
}
