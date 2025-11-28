# Ejercicio 17 - DOM Selección

## 🎯 Objetivo
Aprender a seleccionar elementos HTML desde JavaScript usando diferentes métodos del DOM.

## 📚 Conceptos Clave
- `document.querySelector()` - Selecciona el primer elemento que coincida
- `document.querySelectorAll()` - Selecciona todos los elementos
- `document.getElementById()` - Por ID
- `document.getElementsByClassName()` - Por clase
- `document.getElementsByTagName()` - Por etiqueta

## 💻 Tarea
Crea una página HTML con:
- 1 título `<h1 id="titulo">`
- 3 párrafos con clase `<p class="texto">`
- 1 botón `<button id="boton">`
- 1 lista con 5 items `<ul><li>`

Desde JavaScript, selecciona cada elemento y guarda en variables:
```javascript
const titulo = document.querySelector('#titulo');
const primerTexto = document.querySelector('.texto');
const todosLosTextos = document.querySelectorAll('.texto');
const boton = document.getElementById('boton');
const items = document.querySelectorAll('li');

console.log(titulo);
console.log(todosLosTextos.length);
```

## ✅ Checklist
- [ ] Usas querySelector para selectores CSS
- [ ] Usas querySelectorAll para múltiples elementos
- [ ] Iteras sobre NodeList con forEach
- [ ] Verificas que los elementos existen antes de usarlos

---

### 🎯 Continúa con: [Ejercicio 18 - DOM Manipulación](./ejercicio-18-dom-manipulacion.md)
