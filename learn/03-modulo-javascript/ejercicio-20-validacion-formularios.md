# Ejercicio 20 - Validación de Formularios

## 🎯 Objetivo
Crear un formulario completamente funcional con validación personalizada en JavaScript.

## 📚 Conceptos Clave
- Validación HTML5 vs JavaScript personalizada
- Validar campos vacíos, emails, longitud
- Mostrar mensajes de error dinámicamente
- Prevenir envío si hay errores
- Mejorar UX con feedback visual

## 💻 Proyecto Final del Módulo

Crea un formulario de registro con:

### HTML requerido:
```html
<form id="registro">
    <input type="text" id="nombre" placeholder="Nombre completo">
    <span class="error" id="error-nombre"></span>
    
    <input type="email" id="email" placeholder="Email">
    <span class="error" id="error-email"></span>
    
    <input type="password" id="password" placeholder="Contraseña">
    <span class="error" id="error-password"></span>
    
    <input type="password" id="confirmar" placeholder="Confirmar contraseña">
    <span class="error" id="error-confirmar"></span>
    
    <button type="submit">Registrarse</button>
</form>
```

### Validaciones JavaScript:
```javascript
const form = document.getElementById('registro');

form.addEventListener('submit', (e) => {
    e.preventDefault();
    
    // Limpiar errores previos
    limpiarErrores();
    
    // Validar cada campo
    let valido = true;
    
    // Nombre (no vacío, mínimo 3 caracteres)
    const nombre = document.getElementById('nombre').value.trim();
    if (nombre === '') {
        mostrarError('error-nombre', 'El nombre es obligatorio');
        valido = false;
    } else if (nombre.length < 3) {
        mostrarError('error-nombre', 'Mínimo 3 caracteres');
        valido = false;
    }
    
    // Email (formato válido)
    const email = document.getElementById('email').value.trim();
    if (!validarEmail(email)) {
        mostrarError('error-email', 'Email inválido');
        valido = false;
    }
    
    // Password (mínimo 6 caracteres)
    const password = document.getElementById('password').value;
    if (password.length < 6) {
        mostrarError('error-password', 'Mínimo 6 caracteres');
        valido = false;
    }
    
    // Confirmar password (coincide)
    const confirmar = document.getElementById('confirmar').value;
    if (password !== confirmar) {
        mostrarError('error-confirmar', 'Las contraseñas no coinciden');
        valido = false;
    }
    
    // Si todo válido, enviar
    if (valido) {
        console.log('Formulario válido! Enviando...');
        alert('Registro exitoso!');
        form.reset();
    }
});

function mostrarError(id, mensaje) {
    const errorSpan = document.getElementById(id);
    errorSpan.textContent = mensaje;
    errorSpan.style.color = 'red';
}

function limpiarErrores() {
    const errores = document.querySelectorAll('.error');
    errores.forEach(error => error.textContent = '');
}

function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}
```

## ✅ Checklist Final
- [ ] El formulario previene submit si hay errores
- [ ] Validas todos los campos requeridos
- [ ] Muestras mensajes de error específicos
- [ ] Limpias errores antes de re-validar
- [ ] Validas formato de email con regex
- [ ] Comparas que las contraseñas coincidan
- [ ] La UX es clara (feedback visual)
- [ ] Reseteas el form después de envío exitoso

---

### 🎯 ¡Felicidades! Completaste el Módulo 3 de JavaScript
### 👉 Continúa con: [Módulo 4: Herramientas y Mejores Prácticas](../04-modulo-herramientas/README.md)
