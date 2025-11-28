# Guía 02 - Chrome DevTools

## 🎯 Objetivo
Dominar Chrome DevTools para debugging, inspección y optimización de sitios web.

## 🔧 Abrir DevTools
- **Windows/Linux**: F12 o Ctrl+Shift+I
- **Mac**: Cmd+Option+I
- **Click derecho** → Inspeccionar

## 💻 Herramientas Principales

### 1. Elements (Inspector)
**Para qué**: Ver y modificar HTML/CSS en tiempo real

**Tareas**:
- Inspecciona cualquier elemento de una página web
- Modifica el HTML (doble-click)
- Cambia estilos CSS (panel derecho)
- Ve el Box Model visualmente
- Activa/desactiva propiedades CSS (checkbox)
- Agrega nuevas propiedades

**Práctica**: 
- Ve a youtube.com
- Inspecciona el logo
- Cambia su color
- Modifica el texto de un título

### 2. Console (Consola de JavaScript)
**Para qué**: Ejecutar JavaScript, ver logs y errores

**Tareas**:
- Ve errores JavaScript (en rojo)
- Ejecuta código JavaScript directamente
- Usa `console.log()` para debugging

**Práctica**:
```javascript
// Escribe en la consola:
console.log("Hola DevTools");
document.querySelector('h1').style.color = 'red';
```

### 3. Network (Red)
**Para qué**: Ver solicitudes HTTP, tiempos de carga

**Tareas**:
- Recarga la página con Network abierto
- Ve todas las solicitudes (HTML, CSS, JS, imágenes)
- Verifica tiempos de carga
- Identifica archivos pesados

### 4. Sources (Fuentes)
**Para qué**: Ver archivos del sitio, debugging avanzado

**Tareas**:
- Ve todos los archivos cargados
- Agrega breakpoints en JavaScript
- Debug paso a paso

### 5. Application (Aplicación)
**Para qué**: Ver LocalStorage, Cookies, Cache

## ✅ Checklist de Habilidades
- [ ] Abres DevTools con atajos de teclado
- [ ] Inspeccionas elementos y modificas CSS
- [ ] Usas la consola para ejecutar JavaScript
- [ ] Ves y entiendes errores en Console
- [ ] Verificas tiempos de carga en Network
- [ ] Usas el selector de elementos (icono de flecha)

## 💡 Tips Pro
- **Device Mode**: Ctrl+Shift+M para simular móviles
- **Screenshots**: Cmd/Ctrl+Shift+P → "Screenshot"
- **Copy CSS**: Click derecho en un elemento → Copy → Copy styles
- **Edición en vivo**: Cambios en DevTools NO se guardan automáticamente

---

### 👉 Continúa con: [Guía 03 - Git Básico](./guia-03-git-basico.md)
