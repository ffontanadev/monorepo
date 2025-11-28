# Ejercicio 08 - Box Model

## 🎯 Objetivo
Dominar el modelo de caja de CSS (Box Model) entendiendo margin, padding, border y content para controlar el espaciado y dimensiones de elementos.

## 📚 Conceptos Clave
- El Box Model: content, padding, border, margin
- Propiedades: `width`, `height`, `padding`, `margin`, `border`
- `box-sizing: border-box` vs `content-box`
- Unidades de medida: px, em, rem, %, vh, vw
- Margin collapse

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - El modelo de caja](https://developer.mozilla.org/es/docs/Learn/CSS/Building_blocks/The_box_model)
- [MDN - box-sizing](https://developer.mozilla.org/es/docs/Web/CSS/box-sizing)
- [CSS Tricks - Box Sizing](https://css-tricks.com/box-sizing/)

### Preguntas para investigar:
1. ¿Cuáles son las 4 partes del Box Model?
2. ¿Cuál es la diferencia entre `padding` y `margin`?
3. ¿Qué hace `box-sizing: border-box`?
4. ¿Cuándo usar px vs em vs rem?
5. ¿Qué es el margin collapse?

## 💻 Tarea

Crea dos archivos:
1. **`box-model-demo.html`**
2. **`css/box-model.css`**

### La página debe contener:

**4 cajas diferentes** que demuestren el Box Model:

1. **Caja 1 - Content Box (Default)**:
   - Fondo azul
   - Ancho: 200px
   - Padding: 20px
   - Border: 5px sólido negro
   - Margin: 10px
   - Texto que diga "Content Box"

2. **Caja 2 - Border Box**:
   - Fondo verde
   - Ancho: 200px (total incluyendo padding y border)
   - Padding: 20px
   - Border: 5px sólido negro
   - Margin: 10px
   - `box-sizing: border-box`
   - Texto que diga "Border Box"

3. **Caja 3 - Espaciado Asimétrico**:
   - Fondo rojo
   - Padding diferente en cada lado
   - Margin diferente en cada lado
   - Border solo en algunos lados
   - Texto que diga "Asimétrico"

4. **Caja 4 - Unidades Relativas**:
   - Fondo naranja
   - Width: 50% del contenedor
   - Padding en em
   - Margin en rem
   - Texto que diga "Unidades Relativas"

### Requisitos Específicos:
- Cada caja debe tener una clase distinta
- Usa `box-sizing: border-box` en al menos 2 cajas
- Demuestra padding con 4 valores, 2 valores, y 1 valor
- Demuestra margin con sintaxis diferentes
- Incluye borders de diferentes estilos (solid, dashed, dotted)
- Usa al menos 3 tipos de unidades (px, em, rem, %)

## ✅ Checklist de Autoevaluación

### Comprensión del Box Model:
- [ ] Entiendes que cada elemento es una caja
- [ ] Sabes cuál es el orden: content → padding → border → margin
- [ ] Puedes visualizar el Box Model en DevTools

### Propiedades de Tamaño:
- [ ] Usas `width` y/o `height` en las cajas
- [ ] Entiendes cómo `box-sizing` afecta el tamaño total
- [ ] Al menos 2 cajas usan `box-sizing: border-box`

### Padding:
- [ ] Aplicas `padding` a todas las cajas
- [ ] Usas diferentes sintaxis: `padding: 20px` (todos lados iguales)
- [ ] Usas `padding: 10px 20px` (vertical horizontal)
- [ ] Usas `padding: 10px 20px 30px 40px` (top right bottom left)
- [ ] O usas propiedades individuales: `padding-top`, `padding-left`, etc.

### Border:
- [ ] Todas las cajas tienen border visible
- [ ] Usas diferentes estilos: solid, dashed, dotted
- [ ] Usas la propiedad shorthand: `border: 2px solid black`
- [ ] O usas propiedades individuales: `border-width`, `border-style`, `border-color`
- [ ] Al menos una caja tiene border solo en algunos lados

### Margin:
- [ ] Aplicas `margin` para separar las cajas
- [ ] Entiendes cómo funciona margin collapse
- [ ] Usas diferentes valores para cada lado en al menos una caja
- [ ] Los margins crean espacio visible entre cajas

### Unidades de Medida:
- [ ] Usas `px` (píxeles absolutos)
- [ ] Usas `em` (relativo al font-size del elemento)
- [ ] Usas `rem` (relativo al font-size del root)
- [ ] Usas `%` (porcentaje del contenedor padre)

### Visualización en DevTools:
- [ ] Abres DevTools (F12) y ves el Box Model
- [ ] Identificas cada parte: content, padding, border, margin
- [ ] Experimentas modificando valores en vivo

### Código Limpio:
- [ ] El CSS está organizado con comentarios
- [ ] Cada caja tiene su sección claramente delimitada
- [ ] El código está correctamente indentado

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa `outline` (diferente a border, no afecta el Box Model)
- Implementa `max-width` y `min-width`
- Usa `vh` (viewport height) y `vw` (viewport width)
- Agrega `border-radius` para esquinas redondeadas
- Experimenta con `margin: auto` para centrar

## 🐛 Errores Comunes

### Error 1: No entender box-sizing
```css
/* Sin box-sizing: border-box */
.caja {
    width: 200px;
    padding: 20px;
    border: 5px solid black;
}
/* Ancho total = 200 + 40 (padding) + 10 (border) = 250px */
```

**Solución**:
```css
/* Con box-sizing: border-box */
.caja {
    width: 200px;
    padding: 20px;
    border: 5px solid black;
    box-sizing: border-box;
}
/* Ancho total = 200px (incluyendo todo) */
```

### Error 2: Orden incorrecto en shorthand
```css
/* ❌ INCORRECTO */
.caja {
    /* No existe esta sintaxis */
    padding: top right bottom left;
}
```

**Solución**:
```css
/* ✅ CORRECTO - Orden de reloj: arriba, derecha, abajo, izquierda */
.caja {
    padding: 10px 20px 30px 40px;
}
```

### Error 3: Margin collapse inesperado
```html
<div class="caja1" style="margin-bottom: 20px;">Caja 1</div>
<div class="caja2" style="margin-top: 30px;">Caja 2</div>
<!-- Espacio entre cajas = 30px (NO 50px) - se usa el mayor -->
```

**Solución**: Entender que los márgenes verticales "colapsan" y se usa el mayor.

### Error 4: Usar margin en inline elements
```css
/* ❌ margin-top/bottom no funciona en elementos inline */
span {
    margin-top: 20px; /* No tiene efecto */
}
```

**Solución**:
```css
/* ✅ Convertir a inline-block o block */
span {
    display: inline-block;
    margin-top: 20px; /* Ahora sí funciona */
}
```

## 🔗 Recursos Adicionales

- [Box Model Visualizer](https://redstapler.co/css-box-model-visualizer/)
- [Every Layout - Box](https://every-layout.dev/layouts/box/)
- [MDN - Unidades CSS](https://developer.mozilla.org/es/docs/Web/CSS/length)

## 📸 Visualización del Box Model

```
┌─────────────────────────────────┐
│      MARGIN (transparente)      │  ← Espacio exterior
│  ┌───────────────────────────┐  │
│  │   BORDER (visible)        │  │  ← Borde
│  │  ┌─────────────────────┐  │  │
│  │  │  PADDING (fondo)    │  │  │  ← Espacio interior
│  │  │  ┌───────────────┐  │  │  │
│  │  │  │   CONTENT     │  │  │  │  ← Contenido
│  │  │  │   (texto)     │  │  │  │
│  │  │  └───────────────┘  │  │  │
│  │  │                     │  │  │
│  │  └─────────────────────┘  │  │
│  │                           │  │
│  └───────────────────────────┘  │
│                                 │
└─────────────────────────────────┘
```

## 📊 Guía de Unidades

| Unidad | Tipo | Ejemplo | Uso Recomendado |
|--------|------|---------|-----------------|
| `px` | Absoluta | `16px` | Borders, detalles precisos |
| `em` | Relativa | `1.5em` | Padding/margin relativo al texto |
| `rem` | Relativa | `2rem` | Font-sizes, espaciados consistentes |
| `%` | Relativa | `50%` | Widths, layouts responsivos |
| `vh/vw` | Viewport | `100vh` | Alturas/anchos de pantalla completa |

## 💡 Tips Prácticos

### Reset Box-Sizing (Recomendado Globalmente):
```css
* {
    box-sizing: border-box;
}
```
Esto hace el cálculo de tamaños mucho más intuitivo.

### Sintaxis de Padding/Margin:
```css
/* 1 valor = todos los lados */
padding: 20px;

/* 2 valores = vertical horizontal */
padding: 10px 20px;

/* 3 valores = top, horizontal, bottom */
padding: 10px 20px 30px;

/* 4 valores = top, right, bottom, left (sentido horario) */
padding: 10px 20px 30px 40px;
```

### Centrar un elemento:
```css
.elemento {
    width: 80%;
    margin: 0 auto; /* Centra horizontalmente */
}
```

## 🧪 Ejercicio de Debugging

Abre DevTools (F12) → Elements → Selecciona una caja
- Ve la pestaña "Computed" o "Estilos"
- Observa el diagrama del Box Model
- Pasa el mouse sobre cada sección
- Modifica valores en tiempo real

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 09 - Flexbox Básico](./ejercicio-09-flexbox-basico.md)
