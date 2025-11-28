# Ejercicio 18 - DOM Manipulación

## 🎯 Objetivo
Modificar el contenido, estilos y estructura del DOM dinámicamente.

## 📚 Conceptos Clave
- `element.textContent` - Cambiar texto
- `element.innerHTML` - Cambiar HTML interno
- `element.style` - Modificar estilos CSS
- `element.classList` - Agregar/quitar clases
- `document.createElement()` - Crear elementos
- `element.append()` / `appendChild()` - Agregar al DOM

## 💻 Tarea

### 1. Modificar Contenido
```javascript
const titulo = document.querySelector('h1');
titulo.textContent = "Nuevo Título";  // Solo texto
titulo.innerHTML = "<strong>Título</strong> Modificado";  // Con HTML
```

### 2. Modificar Estilos
```javascript
const elemento = document.querySelector('.caja');
elemento.style.backgroundColor = 'blue';
elemento.style.padding = '20px';
elemento.style.borderRadius = '10px';
```

### 3. Trabajar con Clases
```javascript
const boton = document.querySelector('button');
boton.classList.add('active');      // Agregar clase
boton.classList.remove('disabled'); // Quitar clase
boton.classList.toggle('visible');  // Toggle (si existe quita, si no agrega)
```

### 4. Crear y Agregar Elementos
```javascript
const nuevoParrafo = document.createElement('p');
nuevoParrafo.textContent = "Este es un nuevo párrafo";
nuevoParrafo.classList.add('texto');

document.body.append(nuevoParrafo);
```

## Proyecto Práctico
Crea una TODO list simple:
- Input para agregar tareas
- Botón "Agregar"
- Al hacer click, crea un `<li>` con la tarea
- Agrégalo a un `<ul>`

## ✅ Checklist
- [ ] Modificas textContent e innerHTML
- [ ] Cambias estilos con .style
- [ ] Usas classList correctamente
- [ ] Creas elementos con createElement
- [ ] Agregas elementos al DOM

---

### 🎯 Continúa con: [Ejercicio 19 - Eventos](./ejercicio-19-eventos.md)
