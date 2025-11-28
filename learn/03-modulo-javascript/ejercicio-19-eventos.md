# Ejercicio 19 - Eventos

## 🎯 Objetivo
Responder a interacciones del usuario con event listeners.

## 📚 Conceptos Clave
- `addEventListener()` - Escuchar eventos
- Tipos de eventos: click, submit, input, keydown, mouseover
- El objeto `event` - Información del evento
- `event.preventDefault()` - Prevenir comportamiento por defecto
- `event.target` - Elemento que disparó el evento

## 💻 Tarea

### 1. Click en Botón
```javascript
const boton = document.querySelector('#mi-boton');

boton.addEventListener('click', function() {
    console.log('Botón clickeado!');
});

// Arrow function
boton.addEventListener('click', () => {
    alert('Hola!');
});
```

### 2. Input (escribir en input)
```javascript
const input = document.querySelector('#nombre');

input.addEventListener('input', (event) => {
    console.log('Escribiendo:', event.target.value);
});
```

### 3. Submit de Formulario
```javascript
const form = document.querySelector('form');

form.addEventListener('submit', (event) => {
    event.preventDefault();  // Evita recargar la página
    
    const nombre = document.querySelector('#nombre').value;
    console.log('Formulario enviado:', nombre);
});
```

### 4. Eventos de Mouse
```javascript
const caja = document.querySelector('.caja');

caja.addEventListener('mouseover', () => {
    caja.style.backgroundColor = 'red';
});

caja.addEventListener('mouseout', () => {
    caja.style.backgroundColor = 'blue';
});
```

## Proyectos Prácticos
1. **Contador**: Botón + y - que modifica un número en pantalla
2. **Calculadora simple**: 2 inputs + botones de operaciones
3. **Cambio de tema**: Botón que cambia dark/light mode

## ✅ Checklist
- [ ] Usas addEventListener correctamente
- [ ] Manejas eventos de click, input, submit
- [ ] Usas event.preventDefault() cuando es necesario
- [ ] Accedes a event.target.value
- [ ] Respondes a eventos de mouse

---

### 🎯 Continúa con: [Ejercicio 20 - Validación de Formularios](./ejercicio-20-validacion-formularios.md)
