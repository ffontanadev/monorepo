# Ejercicio 13 - SVG Básico

## 🎯 Objetivo
Aprender a trabajar con gráficos vectoriales SVG, insertarlos en HTML y estilizarlos con CSS para crear íconos y gráficos escalables.

## 📚 Conceptos Clave
- ¿Qué es SVG? (Scalable Vector Graphics)
- SVG inline vs SVG como imagen
- Elementos básicos: `<svg>`, `<circle>`, `<rect>`, `<path>`, `<polygon>`
- Atributos: `viewBox`, `width`, `height`, `fill`, `stroke`
- Estilizar SVG con CSS
- Ventajas de SVG sobre imágenes raster

## 🔍 Investigación Previa (30 min)

Antes de empezar, investiga en estos recursos:
- [MDN - SVG Tutorial](https://developer.mozilla.org/es/docs/Web/SVG/Tutorial)
- [SVG on the Web](https://svgontheweb.com/)
- [CSS Tricks - Using SVG](https://css-tricks.com/using-svg/)

### Preguntas para investigar:
1. ¿Cuál es la diferencia entre SVG y PNG/JPG?
2. ¿Qué hace el atributo `viewBox`?
3. ¿Cuándo usar SVG inline vs `<img src="file.svg">`?
4. ¿Qué es `fill` y `stroke`?
5. ¿Por qué SVG es perfecto para íconos?

## 💻 Tarea

Crea dos archivos:
1. **`svg-demo.html`**
2. **`css/svg-styles.css`**

### La página debe contener:

#### Parte 1: SVG Básicos Inline

**1. Círculo Simple**:
```html
<svg width="100" height="100">
    <circle cx="50" cy="50" r="40" fill="blue" />
</svg>
```

**2. Rectángulo**:
```html
<svg width="200" height="100">
    <rect x="10" y="10" width="180" height="80" fill="green" />
</svg>
```

**3. Polígono (Triángulo)**:
```html
<svg width="100" height="100">
    <polygon points="50,10 90,90 10,90" fill="red" />
</svg>
```

#### Parte 2: Íconos SVG Estilizados con CSS

Crea 4 íconos diferentes usando SVG:

**Ícono 1: Corazón**
- SVG inline con un path
- Estiliza con CSS (fill, stroke)
- Animación al hover (scale o color)

**Ícono 2: Estrella**
- Usa `<polygon>` para crear una estrella de 5 puntas
- Color que cambia al hover

**Ícono 3: Check (✓)**
- Usa `<path>` o `<polyline>`
- Animación stroke-dasharray al hover

**Ícono 4: Hamburguesa (☰) para menú**
- Tres líneas usando `<line>` o `<rect>`
- Transición al hover

#### Parte 3: SVG Responsive

**Logo Simple**:
- Crea un logo usando múltiples formas SVG
- Usa `viewBox` para hacerlo escalable
- Debe verse bien en cualquier tamaño
- Estiliza con CSS

#### Parte 4: Comparación SVG vs PNG

- Muestra el mismo ícono como SVG y como PNG
- Agranda ambos para demostrar escalabilidad
- Documenta las diferencias

### Requisitos Específicos:
- Al menos 6 elementos SVG inline en el HTML
- Usa `<circle>`, `<rect>`, `<polygon>`, y `<path>`
- Todos los SVG deben tener `viewBox` definido
- Estiliza al menos 4 SVG con CSS (no atributos inline)
- Al menos 2 SVG deben tener transiciones/animaciones
- Usa clases CSS para estilizar, no solo atributos fill/stroke

## ✅ Checklist de Autoevaluación

### Conceptos Básicos:
- [ ] Entiendes que SVG es un formato vectorial (escalable sin pérdida)
- [ ] Sabes la diferencia entre raster (JPG/PNG) y vectorial (SVG)
- [ ] Entiendes que SVG inline permite estilizar con CSS

### Elementos SVG:
- [ ] Usas `<svg>` como contenedor
- [ ] Creas un `<circle>` con cx, cy, r
- [ ] Creas un `<rect>` con x, y, width, height
- [ ] Creas un `<polygon>` con points
- [ ] Usas al menos un `<path>` (aunque sea copiado)

### Atributos Básicos:
- [ ] Todos los `<svg>` tienen `viewBox`
- [ ] Entiendes qué hace viewBox (ej: `viewBox="0 0 100 100"`)
- [ ] Usas `width` y `height` o CSS para dimensiones
- [ ] Usas `fill` para color de relleno
- [ ] Usas `stroke` para color de borde

### ViewBox y Escalabilidad:
- [ ] Entiendes que viewBox define el "sistema de coordenadas"
- [ ] Los SVG escalan sin pixelarse
- [ ] Puedes cambiar el tamaño sin perder calidad

### Estilizado con CSS:
- [ ] Asignas clases a elementos SVG: `<circle class="mi-circulo" />`
- [ ] Estilizas con CSS: `.mi-circulo { fill: blue; }`
- [ ] Usas `:hover` para cambios interactivos
- [ ] Aplicas transitions a propiedades como fill, transform

### Íconos Completados:
- [ ] Ícono 1: Corazón funciona y tiene hover
- [ ] Ícono 2: Estrella funciona y tiene hover
- [ ] Ícono 3: Check está creado
- [ ] Ícono 4: Menú hamburguesa está creado
- [ ] Logo responsive se ve bien en todos los tamaños

### Responsive:
- [ ] Los SVG se adaptan al contenedor
- [ ] Usas viewBox correctamente para escalabilidad
- [ ] Puedes agregar `width: 100%` y el SVG se adapta

### Comparación SVG vs PNG:
- [ ] Muestras el mismo ícono en ambos formatos
- [ ] Al agrandar, se ve la diferencia de calidad
- [ ] Documentas qué formato usar cuándo

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa gradientes con `<linearGradient>` o `<radialGradient>`
- Anima SVG con `@keyframes` CSS
- Crea un path complejo usando herramientas (Figma, Illustrator)
- Usa `<symbol>` y `<use>` para reutilizar formas
- Explora `stroke-dasharray` para animaciones de dibujo

## 🐛 Errores Comunes

### Error 1: SVG sin viewBox
```html
<!-- ❌ Difícil de escalar -->
<svg width="100" height="100">
    <circle cx="50" cy="50" r="40" />
</svg>
```

**Mejor práctica**:
```html
<!-- ✅ Escalable y flexible -->
<svg viewBox="0 0 100 100" width="100" height="100">
    <circle cx="50" cy="50" r="40" />
</svg>
```

### Error 2: Intentar estilizar SVG externo con CSS
```html
<!-- ❌ No puedes cambiar fill con CSS -->
<img src="icono.svg" class="icono" />
```

```css
/* Esto NO funciona en <img> */
.icono {
    fill: red; /* No tiene efecto */
}
```

**Solución**: Usa SVG inline para estilizar con CSS:
```html
<!-- ✅ Ahora sí puedes estilizar -->
<svg class="icono" viewBox="0 0 24 24">
    <path d="..." />
</svg>
```

```css
.icono {
    fill: red; /* ¡Funciona! */
}
```

### Error 3: Coordenadas fuera del viewBox
```html
<!-- ❌ El círculo está fuera del viewBox y no se ve -->
<svg viewBox="0 0 100 100">
    <circle cx="200" cy="200" r="40" />
    <!-- cx/cy están fuera de 0-100 -->
</svg>
```

**Solución**: Asegura que las coordenadas estén dentro del viewBox.

### Error 4: Olvidar cerrar etiquetas SVG
```html
<!-- ❌ INCORRECTO -->
<svg>
    <circle cx="50" cy="50" r="40">
</svg>
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<svg>
    <circle cx="50" cy="50" r="40" />  <!-- Auto-cierre -->
</svg>
```

## 🔗 Recursos Adicionales

### Íconos SVG Gratuitos:
- [Heroicons](https://heroicons.com/) - Íconos SVG copiables
- [Feather Icons](https://feathericons.com/) - Íconos minimalistas
- [Bootstrap Icons](https://icons.getbootstrap.com/) - Gran colección
- [Material Icons](https://fonts.google.com/icons) - Google Icons

### Herramientas:
- [SVGOMG](https://jakearchibald.github.io/svgomg/) - Optimizador de SVG
- [SVG Path Visualizer](https://svg-path-visualizer.netlify.app/) - Entiende paths
- [Boxy SVG](https://boxy-svg.com/) - Editor de SVG online

### Tutoriales:
- [SVG Tutorial - MDN](https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial)
- [Pocket Guide to Writing SVG](https://svgpocketguide.com/)

## 📊 Guía de Elementos SVG Básicos

| Elemento | Uso | Atributos Clave |
|----------|-----|-----------------|
| `<circle>` | Círculos | `cx cy r` |
| `<rect>` | Rectángulos | `x y width height` |
| `<line>` | Líneas | `x1 y1 x2 y2` |
| `<polygon>` | Polígonos | `points` |
| `<path>` | Formas complejas | `d` (data) |
| `<text>` | Texto | `x y` |

## 📸 Ejemplo Visual: ViewBox Explicado

```
viewBox="0 0 100 100"
         │ │  │   │
         │ │  │   └─ Altura (100 unidades)
         │ │  └───── Ancho (100 unidades)
         │ └──────── Y mínima (esquina superior)
         └────────── X mínima (esquina izquierda)

┌─────────────────┐
│ (0,0)           │ ← Origen
│                 │
│     (50,50)     │ ← Centro
│        •        │
│                 │
│      (100,100)  │ ← Esquina inferior derecha
└─────────────────┘
```

## 💡 Plantilla de Inicio

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVG Demo</title>
    <link rel="stylesheet" href="css/svg-styles.css">
</head>
<body>
    <h1>SVG Básicos</h1>

    <!-- Círculo -->
    <svg viewBox="0 0 100 100" width="100" height="100">
        <circle class="circulo-azul" cx="50" cy="50" r="40" />
    </svg>

    <!-- Rectángulo -->
    <svg viewBox="0 0 200 100" width="200" height="100">
        <rect class="rectangulo-verde" x="10" y="10" width="180" height="80" />
    </svg>

    <!-- Estrella -->
    <svg viewBox="0 0 100 100" width="100" height="100" class="icono-estrella">
        <polygon points="50,10 61,35 85,35 66,50 73,75 50,60 27,75 34,50 15,35 39,35" />
    </svg>

    <!-- Corazón (path copiado de Heroicons o similar) -->
    <svg viewBox="0 0 24 24" width="48" height="48" class="icono-corazon">
        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
    </svg>
</body>
</html>
```

```css
/* svg-styles.css */

/* Círculo */
.circulo-azul {
    fill: #3498db;
    transition: fill 0.3s ease;
}

.circulo-azul:hover {
    fill: #2980b9;
}

/* Rectángulo */
.rectangulo-verde {
    fill: #2ecc71;
    stroke: #27ae60;
    stroke-width: 3;
}

/* Estrella */
.icono-estrella polygon {
    fill: gold;
    transition: transform 0.3s ease;
}

.icono-estrella:hover polygon {
    transform: scale(1.2);
    transform-origin: center;
}

/* Corazón */
.icono-corazon {
    cursor: pointer;
}

.icono-corazon path {
    fill: #e74c3c;
    transition: fill 0.3s ease, transform 0.3s ease;
}

.icono-corazon:hover path {
    fill: #c0392b;
    transform: scale(1.1);
}
```

## 🎨 Propiedades CSS para SVG

| Propiedad | Uso | Ejemplo |
|-----------|-----|---------|
| `fill` | Color de relleno | `fill: blue;` |
| `stroke` | Color del borde | `stroke: red;` |
| `stroke-width` | Grosor del borde | `stroke-width: 2;` |
| `opacity` | Transparencia | `opacity: 0.5;` |
| `transform` | Transformaciones | `transform: rotate(45deg);` |

## 🧪 Ejercicio Práctico

Intenta crear estos íconos desde cero:

1. **Cuadrado con esquinas redondeadas**: Usa `<rect>` con `rx` y `ry`
2. **Flecha**: Usa `<polygon>` o `<path>`
3. **Plus (+)**: Dos `<rect>` rotados o un `<path>`
4. **X**: Dos `<line>` cruzadas

---

### 🎯 ¡Felicidades! Has completado el Módulo 2 de CSS

### 👉 Continúa con: [Módulo 3: JavaScript - Interactividad](../03-modulo-javascript/README.md)
