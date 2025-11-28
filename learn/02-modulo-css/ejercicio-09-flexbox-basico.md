# Ejercicio 09 - Flexbox Básico

## 🎯 Objetivo
Dominar Flexbox para crear layouts flexibles y responsivos sin usar floats o posicionamiento complejo.

## 📚 Conceptos Clave
- `display: flex` en el contenedor padre
- Eje principal (main axis) y eje transversal (cross axis)
- `flex-direction`: row, column, row-reverse, column-reverse
- `justify-content`: alineación en el eje principal
- `align-items`: alineación en el eje transversal
- `flex-wrap`: permite que items se envuelvan
- `gap`: espaciado entre items

## 🔍 Investigación Previa (30 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Flexbox](https://developer.mozilla.org/es/docs/Learn/CSS/CSS_layout/Flexbox)
- [CSS Tricks - A Complete Guide to Flexbox](https://css-tricks.com/snippets/css/a-guide-to-flexbox/)
- [Flexbox Froggy](https://flexboxfroggy.com/) - ¡Juega para aprender!

### Preguntas para investigar:
1. ¿Qué es un flex container y qué es un flex item?
2. ¿Cuál es la diferencia entre `justify-content` y `align-items`?
3. ¿Qué hace `flex-direction: column`?
4. ¿Cuándo usar `flex-wrap: wrap`?
5. ¿Qué valores puede tener `justify-content`?

## 💻 Tarea

Crea dos archivos:
1. **`flexbox-layouts.html`**
2. **`css/flexbox.css`**

### La página debe contener 4 layouts diferentes usando Flexbox:

#### Layout 1: Navegación Horizontal
- Contenedor flex con dirección row
- 4-5 items de navegación (enlaces)
- Distribuidos con espacio uniforme
- Centrados verticalmente

#### Layout 2: Card Layout (3 Columnas)
- Contenedor flex con 3 tarjetas
- Cada tarjeta tiene: imagen, título, descripción, botón
- Las tarjetas tienen la misma altura
- Espacio entre tarjetas con `gap`

#### Layout 3: Barra de Herramientas
- Logo a la izquierda
- Menú centrado
- Botón de acción a la derecha
- Todo en una misma línea horizontal

#### Layout 4: Layout Vertical (Columna)
- Contenedor con `flex-direction: column`
- Header, contenido principal, footer
- Footer pegado al fondo (si hay espacio)

### Requisitos Específicos:
- Todos los layouts deben usar `display: flex`
- Usa al menos 3 valores diferentes de `justify-content`
- Usa al menos 2 valores diferentes de `align-items`
- Al menos un layout debe usar `flex-direction: column`
- Al menos un layout debe usar `gap` para espaciado
- Al menos un layout debe usar `flex-wrap: wrap`

## ✅ Checklist de Autoevaluación

### Conceptos Básicos:
- [ ] Entiendes qué es un flex container (elemento con `display: flex`)
- [ ] Entiendes qué son flex items (hijos directos del container)
- [ ] Sabes identificar el eje principal vs eje transversal

### Display Flex:
- [ ] Todos los layouts usan `display: flex` en el contenedor
- [ ] Los items se comportan como flex items
- [ ] El layout responde a las propiedades flex

### Flex Direction:
- [ ] Usas `flex-direction: row` (horizontal)
- [ ] Usas `flex-direction: column` (vertical)
- [ ] Entiendes cómo cambia el eje principal

### Justify Content (Eje Principal):
- [ ] Usas `justify-content: center` (centrar)
- [ ] Usas `justify-content: space-between` (espaciado entre items)
- [ ] Usas `justify-content: space-around` o `space-evenly`
- [ ] Entiendes la diferencia entre cada valor

### Align Items (Eje Transversal):
- [ ] Usas `align-items: center` (centrar)
- [ ] Usas `align-items: stretch` (estirar, default)
- [ ] Pruebas con otros valores (flex-start, flex-end)

### Gap:
- [ ] Usas `gap` para espaciado entre items
- [ ] El gap se aplica automáticamente sin margins manuales
- [ ] Entiendes que gap es más limpio que margins

### Flex Wrap:
- [ ] Al menos un layout usa `flex-wrap: wrap`
- [ ] Los items se envuelven a la siguiente línea cuando no caben
- [ ] Entiendes cuándo es necesario wrap

### Layouts Completados:
- [ ] Layout 1 (Navegación) funciona correctamente
- [ ] Layout 2 (Cards) muestra 3 tarjetas alineadas
- [ ] Layout 3 (Barra) distribuye elementos correctamente
- [ ] Layout 4 (Columna) apila elementos verticalmente

### Código Limpio:
- [ ] El CSS está organizado por secciones (un layout por sección)
- [ ] Hay comentarios explicando cada layout
- [ ] Los nombres de clases son descriptivos

### Responsive:
- [ ] Los layouts se adaptan al cambiar el ancho de la ventana
- [ ] Con wrap, los items se reorganizan automáticamente

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa `flex-grow`, `flex-shrink`, `flex-basis` en items individuales
- Usa `align-self` para alinear un item específico diferente
- Crea un layout tipo "Holy Grail" (header, sidebar, main, sidebar, footer)
- Usa `order` para cambiar el orden visual de items
- Combina flexbox con media queries para responsive

## 🐛 Errores Comunes

### Error 1: Aplicar flex a los items en lugar del contenedor
```css
/* ❌ INCORRECTO */
.item {
    display: flex; /* Esto hace al item un flex container */
}
```

**Solución**:
```css
/* ✅ CORRECTO */
.container {
    display: flex; /* El contenedor es flex, los items responden */
}
```

### Error 2: Confundir justify-content con align-items
```css
/* Para centrar vertical y horizontalmente */
.container {
    display: flex;
    /* ❌ Esto solo centra horizontalmente (si flex-direction: row) */
    justify-content: center;
}
```

**Solución**:
```css
/* ✅ CORRECTO - centrar en ambos ejes */
.container {
    display: flex;
    justify-content: center; /* Eje principal (horizontal) */
    align-items: center;     /* Eje transversal (vertical) */
}
```

### Error 3: No dar altura al contenedor para align-items
```css
/* ❌ align-items no tiene efecto si no hay altura */
.container {
    display: flex;
    align-items: center;
    /* Sin altura, no hay espacio para centrar */
}
```

**Solución**:
```css
/* ✅ CORRECTO */
.container {
    display: flex;
    align-items: center;
    min-height: 200px; /* Ahora sí hay espacio */
}
```

### Error 4: Margins colapsan flex items
```css
/* Los margins pueden empujar items inesperadamente */
.item {
    margin: 20px;
}
```

**Mejor práctica**:
```css
/* ✅ Usa gap en el container */
.container {
    display: flex;
    gap: 20px; /* Más limpio y predecible */
}
```

## 🔗 Recursos Adicionales

### Juegos Interactivos:
- [Flexbox Froggy](https://flexboxfroggy.com/) - Aprende jugando
- [Flexbox Defense](http://www.flexboxdefense.com/) - Tower defense con flexbox
- [Flexbox Zombies](https://mastery.games/flexboxzombies/) - Curso gamificado

### Herramientas:
- [Flexbox Generator](https://loading.io/flexbox/) - Generador visual
- [Flexulator](https://www.flexulator.com/) - Calculadora de flexbox

### Guías:
- [Flexbox Cheatsheet](https://yoksel.github.io/flex-cheatsheet/)
- [Visual Guide to Flexbox](https://scotch.io/tutorials/a-visual-guide-to-css3-flexbox-properties)

## 📸 Ejemplos Visuales

### Layout 1: Navegación
```
┌──────────────────────────────────────┐
│ [Inicio] [Servicios] [Blog] [Contacto] │ ← justify-content: space-around
└──────────────────────────────────────┘
```

### Layout 2: Cards (3 columnas)
```
┌─────────┐  ┌─────────┐  ┌─────────┐
│ [Img]   │  │ [Img]   │  │ [Img]   │
│ Título  │  │ Título  │  │ Título  │
│ Texto   │  │ Texto   │  │ Texto   │
│ [Botón] │  │ [Botón] │  │ [Botón] │
└─────────┘  └─────────┘  └─────────┘
```

### Layout 3: Barra de Herramientas
```
┌──────────────────────────────────────┐
│ Logo        Menú Menú       [Botón] │
│ ←─────      ─────────       ──────→ │
│ start       center           end     │
└──────────────────────────────────────┘
```

### Layout 4: Columna
```
┌──────────────┐
│   HEADER     │
├──────────────┤
│              │
│   CONTENT    │
│              │
├──────────────┤
│   FOOTER     │
└──────────────┘
```

## 💡 Guía Rápida de Propiedades

### Contenedor (Flex Container):
```css
.container {
    display: flex;              /* Activa flexbox */
    flex-direction: row;        /* row | column | row-reverse | column-reverse */
    justify-content: center;    /* flex-start | center | flex-end | space-between | space-around | space-evenly */
    align-items: center;        /* flex-start | center | flex-end | stretch | baseline */
    flex-wrap: wrap;            /* nowrap | wrap | wrap-reverse */
    gap: 20px;                  /* Espacio entre items */
}
```

### Items (Flex Items):
```css
.item {
    flex-grow: 1;      /* Crece para llenar espacio */
    flex-shrink: 1;    /* Se encoge si es necesario */
    flex-basis: 200px; /* Tamaño base antes de grow/shrink */
    align-self: center; /* Sobrescribe align-items para este item */
}
```

## 📋 Plantilla de Inicio

```css
/* Layout 1: Navegación Horizontal */
.nav-container {
    display: flex;
    justify-content: space-around;
    align-items: center;
    background-color: #333;
    padding: 1rem;
}

.nav-item {
    color: white;
    text-decoration: none;
    padding: 0.5rem 1rem;
}

/* Layout 2: Cards */
.cards-container {
    display: flex;
    gap: 20px;
    padding: 20px;
}

.card {
    flex: 1; /* Cada card ocupa el mismo espacio */
    border: 1px solid #ddd;
    padding: 20px;
}

/* Agrega más layouts... */
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 10 - Responsive Design](./ejercicio-10-responsive-design.md)
