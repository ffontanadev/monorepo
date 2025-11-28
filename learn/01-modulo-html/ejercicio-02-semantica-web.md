# Ejercicio 02 - Semántica Web

## 🎯 Objetivo
Aprender a estructurar páginas web usando etiquetas semánticas de HTML5 para mejorar la accesibilidad y el SEO.

## 📚 Conceptos Clave
- Etiquetas semánticas vs etiquetas genéricas (`<div>`)
- Estructura de página: `<header>`, `<nav>`, `<main>`, `<section>`, `<article>`, `<footer>`
- Importancia de la semántica para accesibilidad y SEO
- Jerarquía y anidamiento correcto

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Elementos semánticos](https://developer.mozilla.org/es/docs/Glossary/Semantics#sem%C3%A1ntica_en_html)
- [HTML5 Doctor - Elementos semánticos](http://html5doctor.com/lets-talk-about-semantics/)
- [W3Schools - HTML Semantic Elements](https://www.w3schools.com/html/html5_semantic_elements.asp)

### Preguntas para investigar:
1. ¿Qué es la semántica en HTML?
2. ¿Por qué usar `<header>` en lugar de `<div class="header">`?
3. ¿Cuál es la diferencia entre `<section>` y `<article>`?
4. ¿Qué va dentro de `<nav>`?
5. ¿Cuándo usar `<main>` y cuántas veces puede aparecer?

## 💻 Tarea

Crea un archivo llamado `pagina-semantica.html` que contenga una página web estructurada con etiquetas semánticas.

### La página debe tener:

1. **Header (Encabezado)**:
   - Título del sitio con `<h1>`
   - Subtítulo o eslogan con `<p>`

2. **Navigation (Navegación)**:
   - Menú con 4 enlaces (Inicio, Sobre mí, Proyectos, Contacto)
   - Los enlaces pueden ser `href="#"` por ahora

3. **Main (Contenido Principal)**:
   - **Section 1**: "Sobre mí"
     - Encabezado `<h2>`
     - 2 párrafos de texto
   - **Section 2**: "Mis intereses"
     - Encabezado `<h2>`
     - 2 párrafos de texto

4. **Footer (Pie de página)**:
   - Texto de copyright
   - Año actual

### Requisitos Específicos:
- Usa `<header>`, `<nav>`, `<main>`, `<section>`, `<footer>` apropiadamente
- Cada sección debe tener su propio encabezado (`<h2>`)
- El `<h1>` debe ser único en la página
- Mantén la estructura correctamente indentada

## ✅ Checklist de Autoevaluación

### Estructura Semántica:
- [ ] La página tiene un `<header>` como primer elemento dentro de `<body>`
- [ ] Existe un elemento `<nav>` con enlaces de navegación
- [ ] Hay un elemento `<main>` que contiene el contenido principal
- [ ] Dentro de `<main>` hay al menos 2 elementos `<section>`
- [ ] La página termina con un `<footer>`
- [ ] No se usaron `<div>` genéricos donde podrían ir etiquetas semánticas

### Jerarquía de Encabezados:
- [ ] Hay un único `<h1>` en toda la página
- [ ] Cada `<section>` tiene su `<h2>`
- [ ] La jerarquía es lógica (h1 → h2, no h1 → h3)

### Navegación:
- [ ] El `<nav>` contiene una lista (`<ul>` o enlaces directos)
- [ ] Hay al menos 4 enlaces de navegación
- [ ] Los enlaces tienen texto descriptivo

### Contenido:
- [ ] El header tiene título y subtítulo
- [ ] Cada section tiene contenido relevante (no "Lorem ipsum")
- [ ] El footer tiene información de copyright

### Código Limpio:
- [ ] El código está correctamente indentado por niveles
- [ ] Todas las etiquetas están cerradas
- [ ] Los nombres de etiquetas están en minúsculas
- [ ] El archivo incluye la estructura básica HTML5 completa

### Validación:
- [ ] El archivo se visualiza correctamente en el navegador
- [ ] La estructura es clara y organizada visualmente
- [ ] No hay errores en la consola del navegador

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Agrega un elemento `<aside>` con información secundaria
- Usa `<article>` dentro de una de las secciones
- Agrega más niveles de jerarquía (`<h3>`, `<h4>`)
- Incluye un elemento `<time>` para el año del copyright

## 🐛 Errores Comunes

### Error 1: Múltiples `<main>`
```html
<!-- ❌ INCORRECTO -->
<body>
    <main>
        <section>Sección 1</section>
    </main>
    <main>
        <section>Sección 2</section>
    </main>
</body>
```

**Solución**: Solo debe haber **un** elemento `<main>` por página.

### Error 2: `<nav>` sin estructura
```html
<!-- ❌ INCORRECTO -->
<nav>
    <a href="#">Inicio</a>
    <a href="#">Sobre mí</a>
</nav>
```

**Mejor práctica** (Opcional pero recomendado):
```html
<!-- ✅ MEJOR -->
<nav>
    <ul>
        <li><a href="#">Inicio</a></li>
        <li><a href="#">Sobre mí</a></li>
    </ul>
</nav>
```

### Error 3: Contenido fuera de `<main>`
```html
<!-- ❌ INCORRECTO -->
<body>
    <header>...</header>
    <nav>...</nav>
    <section>Este contenido debería estar en main</section>
    <footer>...</footer>
</body>
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<body>
    <header>...</header>
    <nav>...</nav>
    <main>
        <section>Ahora está en el lugar correcto</section>
    </main>
    <footer>...</footer>
</body>
```

### Error 4: Múltiples `<h1>`
```html
<!-- ❌ INCORRECTO -->
<header>
    <h1>Mi Sitio</h1>
</header>
<main>
    <section>
        <h1>Sobre mí</h1>  <!-- ¡Segundo h1! -->
    </section>
</main>
```

**Solución**: Usa `<h2>` para los encabezados de secciones.

## 🔗 Recursos Adicionales

- [HTML5 Outliner](https://gsnedders.html5.org/outliner/) - Verifica tu estructura
- [Guía de semántica HTML5](https://www.semrush.com/blog/semantic-html5-guide/)
- [Accessibility Tree](https://developer.chrome.com/docs/devtools/accessibility/reference/#pane) - DevTools

## 📸 Estructura Visual Esperada

```
┌─────────────────────────────────────┐
│ HEADER                              │
│ - Título (h1)                       │
│ - Subtítulo                         │
├─────────────────────────────────────┤
│ NAV                                 │
│ - Inicio | Sobre mí | Proyectos...  │
├─────────────────────────────────────┤
│ MAIN                                │
│ ┌─────────────────────────────────┐ │
│ │ SECTION - Sobre mí              │ │
│ │ - Encabezado (h2)               │ │
│ │ - Párrafos                      │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ SECTION - Mis intereses         │ │
│ │ - Encabezado (h2)               │ │
│ │ - Párrafos                      │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ FOOTER                              │
│ - Copyright © 2025                  │
└─────────────────────────────────────┘
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 03 - Textos y Formato](./ejercicio-03-textos-y-formato.md)
