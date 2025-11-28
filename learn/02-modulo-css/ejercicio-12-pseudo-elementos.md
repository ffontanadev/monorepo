# Ejercicio 12 - Pseudo-elementos

## 🎯 Objetivo
Aprender a usar pseudo-elementos `::before` y `::after` para agregar contenido decorativo y efectos visuales sin modificar el HTML.

## 📚 Conceptos Clave
- Pseudo-elementos: `::before` y `::after`
- Propiedad `content` (obligatoria)
- Posicionamiento con `position: absolute`
- Diferencia entre pseudo-clases (`:hover`) y pseudo-elementos (`::before`)
- Casos de uso: iconos, decoraciones, overlays

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Pseudo-elementos](https://developer.mozilla.org/es/docs/Web/CSS/Pseudo-elements)
- [MDN - ::before](https://developer.mozilla.org/es/docs/Web/CSS/::before)
- [MDN - ::after](https://developer.mozilla.org/es/docs/Web/CSS/::after)
- [CSS Tricks - Pseudo Elements](https://css-tricks.com/pseudo-element-roundup/)

### Preguntas para investigar:
1. ¿Cuál es la diferencia entre `:` y `::`?
2. ¿Por qué `content` es obligatorio en ::before y ::after?
3. ¿Cuántos pseudo-elementos puede tener un elemento?
4. ¿Se pueden animar los pseudo-elementos?
5. ¿Para qué sirve `content: ""`?

## 💻 Tarea

Crea dos archivos:
1. **`pseudo-elementos-demo.html`**
2. **`css/pseudo-elementos.css`**

### La página debe contener 6 ejemplos usando pseudo-elementos:

#### Ejemplo 1: Quote con Comillas Decorativas
- Bloque de cita (blockquote)
- `::before` agrega comilla de apertura (")
- `::after` agrega comilla de cierre (")
- Las comillas son grandes y estilizadas

#### Ejemplo 2: Botón con Ícono
- Botón con texto
- `::before` agrega un ícono usando Unicode (ej: ► ▶ ✓ ✗ ★)
- El ícono está posicionado a la izquierda del texto
- Transición al hacer hover

#### Ejemplo 3: Link con Underline Animado
- Enlace de texto
- `::after` crea una línea debajo
- Al hacer hover, la línea crece de 0% a 100% width
- Usa `transform: scaleX()`

#### Ejemplo 4: Card con Ribbon (Cinta)
- Tarjeta con información
- `::before` o `::after` crea una cinta tipo "Nuevo" o "Oferta"
- Posicionada en una esquina
- Fondo de color y texto rotado (opcional)

#### Ejemplo 5: Tooltip
- Elemento con texto
- Al hacer hover, aparece un tooltip
- `::after` es el contenido del tooltip
- `::before` es el triángulo/flecha (usa borders)

#### Ejemplo 6: Overlay en Imagen
- Contenedor con imagen
- `::after` crea un overlay oscuro
- Al hacer hover, el overlay aparece o cambia de opacidad
- Texto sobre el overlay

### Requisitos Específicos:
- Todos los ejemplos usan ::before y/o ::after
- Cada pseudo-elemento tiene la propiedad `content`
- Al menos 3 ejemplos usan `position: absolute`
- Al menos 2 ejemplos incluyen transiciones
- Usa Unicode characters o emoji en `content`
- Al menos un ejemplo usa pseudo-elemento para forma geométrica

## ✅ Checklist de Autoevaluación

### Conceptos Básicos:
- [ ] Entiendes que ::before inserta contenido ANTES del contenido del elemento
- [ ] Entiendes que ::after inserta contenido DESPUÉS del contenido del elemento
- [ ] Sabes que el elemento padre debe tener `position: relative` para posicionamiento absoluto

### Sintaxis:
- [ ] Usas doble dos puntos `::before` (no solo `:before`)
- [ ] Todos los pseudo-elementos tienen `content: "algo"` o `content: ""`
- [ ] Los selectores son correctos (ej: `.clase::before`)

### Propiedad Content:
- [ ] Usas `content` en todos los pseudo-elementos
- [ ] Pruebas con texto: `content: "Nuevo"`
- [ ] Pruebas con Unicode: `content: "\2713"` (✓)
- [ ] Usas `content: ""` cuando solo quieres la forma/decoración

### Posicionamiento:
- [ ] El elemento padre tiene `position: relative`
- [ ] El pseudo-elemento tiene `position: absolute`
- [ ] Usas `top`, `right`, `bottom`, `left` para posicionar
- [ ] Los elementos están donde esperas

### Estilos:
- [ ] Los pseudo-elementos tienen estilos aplicados (color, size, etc.)
- [ ] Usas `display: block` o `inline-block` cuando es necesario
- [ ] Los tamaños están definidos (width, height) cuando corresponde

### Ejemplos Completados:
- [ ] Ejemplo 1: Quote con comillas funciona
- [ ] Ejemplo 2: Botón con ícono se ve bien
- [ ] Ejemplo 3: Underline animado funciona en hover
- [ ] Ejemplo 4: Card con ribbon está posicionada correctamente
- [ ] Ejemplo 5: Tooltip aparece en hover
- [ ] Ejemplo 6: Overlay en imagen funciona

### Transiciones:
- [ ] Al menos 2 ejemplos tienen transitions
- [ ] Las animaciones son suaves
- [ ] Los efectos mejoran la UX

### Casos de Uso:
- [ ] Usas ::before/::after para decoración, no contenido importante
- [ ] El contenido principal está en el HTML, no en pseudo-elementos
- [ ] Los pseudo-elementos son prescindibles (accesibilidad)

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Crea un triángulo usando solo borders en ::before
- Usa `attr()` para mostrar el contenido de un atributo
- Crea un contador con `counter-increment`
- Usa múltiples pseudo-elementos en un mismo diseño complejo
- Combina ::before y ::after en el mismo elemento

## 🐛 Errores Comunes

### Error 1: Olvidar la propiedad content
```css
/* ❌ NO APARECERÁ */
.elemento::before {
    background: red;
    width: 20px;
    height: 20px;
    /* Falta content! */
}
```

**Solución**:
```css
/* ✅ CORRECTO */
.elemento::before {
    content: ""; /* Vacío pero obligatorio */
    display: block;
    background: red;
    width: 20px;
    height: 20px;
}
```

### Error 2: Posicionamiento sin position relative en el padre
```css
/* El elemento padre */
.contenedor {
    /* Falta position: relative */
}

/* El pseudo-elemento */
.contenedor::after {
    content: "";
    position: absolute;
    top: 0;
    right: 0;
    /* Se posicionará relativamente al body, no al contenedor */
}
```

**Solución**:
```css
/* ✅ CORRECTO */
.contenedor {
    position: relative; /* Ahora ::after se posiciona aquí */
}

.contenedor::after {
    content: "";
    position: absolute;
    top: 0;
    right: 0;
}
```

### Error 3: Usar un solo dos puntos (sintaxis vieja)
```css
/* ⚠️ FUNCIONA PERO ES VIEJO */
.elemento:before {
    content: "Texto";
}
```

**Mejor práctica**:
```css
/* ✅ SINTAXIS MODERNA */
.elemento::before {
    content: "Texto";
}
```

### Error 4: Intentar usar pseudo-elementos en elementos vacíos
```html
<!-- ❌ NO FUNCIONA EN ESTOS -->
<img src="foto.jpg" />
<input type="text" />
<br />
```

**Nota**: `<img>`, `<input>`, `<br>` no pueden tener ::before/::after porque no tienen contenido.

## 🔗 Recursos Adicionales

### Unicode Characters:
- [Unicode Table](https://unicode-table.com/) - Todos los caracteres Unicode
- [HTML Symbols](https://www.toptal.com/designers/htmlarrows/) - Símbolos útiles
- [Emojis](https://emojipedia.org/) - Para usar en content

### Herramientas:
- [CSS Arrow Please](http://www.cssarrowplease.com/) - Genera triángulos
- [Pseudo-elements Generator](https://webcode.tools/generators/css/pseudo-element)

### Inspiración:
- [Creative Pseudo-elements](https://codepen.io/search/pens?q=pseudo%20elements)
- [30 Seconds of CSS](https://www.30secondsofcode.org/css/p/1/) - Snippets

## 💡 Casos de Uso Comunes

### 1. Comillas en Quotes:
```css
blockquote::before {
    content: """;
    font-size: 3em;
    color: #ccc;
}
```

### 2. Ícono antes de texto:
```css
.icono::before {
    content: "★ ";
    color: gold;
}
```

### 3. Línea decorativa:
```css
.titulo::after {
    content: "";
    display: block;
    width: 50px;
    height: 3px;
    background: blue;
    margin-top: 10px;
}
```

### 4. Triángulo (tooltip arrow):
```css
.tooltip::before {
    content: "";
    position: absolute;
    top: -10px;
    left: 50%;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-bottom: 10px solid black;
}
```

## 📸 Visualización de Pseudo-elementos

```
┌─────────────────────────┐
│ ::before (ANTES)        │  ← Pseudo-elemento ::before
├─────────────────────────┤
│                         │
│   Contenido Real        │  ← Contenido del HTML
│   del Elemento          │
│                         │
├─────────────────────────┤
│ ::after (DESPUÉS)       │  ← Pseudo-elemento ::after
└─────────────────────────┘
```

## 📋 Plantilla de Inicio

```css
/* Ejemplo 1: Quote con Comillas */
.quote {
    position: relative;
    padding: 2rem;
    font-style: italic;
}

.quote::before {
    content: """;
    font-size: 4rem;
    position: absolute;
    top: 0;
    left: 0;
    color: #ddd;
    line-height: 1;
}

/* Ejemplo 2: Botón con Ícono */
.boton-icono::before {
    content: "✓ ";
    font-weight: bold;
    color: green;
}

/* Ejemplo 3: Underline Animado */
.link-animado {
    position: relative;
    text-decoration: none;
}

.link-animado::after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background: blue;
    transform: scaleX(0);
    transform-origin: left;
    transition: transform 0.3s ease;
}

.link-animado:hover::after {
    transform: scaleX(1);
}

/* Ejemplo 4: Ribbon */
.card {
    position: relative;
    padding: 2rem;
    background: white;
}

.card::before {
    content: "NUEVO";
    position: absolute;
    top: 10px;
    right: -5px;
    background: red;
    color: white;
    padding: 5px 10px;
    font-size: 0.8rem;
    font-weight: bold;
}

/* Agrega más ejemplos... */
```

## 🎨 Unicode Characters Útiles

```css
/* Flechas */
content: "→";  /* → */
content: "←";  /* ← */
content: "↓";  /* ↓ */
content: "↑";  /* ↑ */

/* Símbolos */
content: "★";  /* ★ Estrella */
content: "♥";  /* ♥ Corazón */
content: "✓";  /* ✓ Check */
content: "✗";  /* ✗ X */

/* Comillas */
content: """;  /* " Comilla izquierda */
content: """;  /* " Comilla derecha */

/* Formas */
content: "■";  /* ■ Cuadrado */
content: "●";  /* ● Círculo */
content: "▶";  /* ▶ Play */
```

## 🧪 Debugging Tips

1. **DevTools**: Los pseudo-elementos aparecen en el inspector
2. **Background temporal**: Agrega `background: red` para ver dónde está el pseudo-elemento
3. **Verifica content**: Si no aparece, probablemente falta `content: ""`
4. **Position**: Asegura que el padre tenga `position: relative`

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 13 - SVG Básico](./ejercicio-13-svg-basico.md)
