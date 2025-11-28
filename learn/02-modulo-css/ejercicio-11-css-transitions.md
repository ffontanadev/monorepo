# Ejercicio 11 - CSS Transitions

## 🎯 Objetivo
Aprender a crear transiciones suaves entre estados de elementos usando CSS, mejorando la experiencia de usuario con animaciones simples.

## 📚 Conceptos Clave
- Propiedad `transition`
- Propiedades animables: color, background, transform, opacity, etc.
- `transition-property`, `transition-duration`, `transition-timing-function`, `transition-delay`
- Timing functions: ease, linear, ease-in, ease-out, ease-in-out, cubic-bezier
- Pseudo-clase `:hover`
- Transform: translate, scale, rotate

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Using CSS transitions](https://developer.mozilla.org/es/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions)
- [MDN - transition](https://developer.mozilla.org/es/docs/Web/CSS/transition)
- [CSS Tricks - transitions](https://css-tricks.com/almanac/properties/t/transition/)

### Preguntas para investigar:
1. ¿Qué propiedades CSS se pueden animar?
2. ¿Cuál es la diferencia entre `ease` y `linear`?
3. ¿Qué hace `transition: all 0.3s ease`?
4. ¿Cuándo usar `transform` vs cambiar propiedades directamente?
5. ¿Qué es cubic-bezier y cómo se usa?

## 💻 Tarea

Crea dos archivos:
1. **`transitions-demo.html`**
2. **`css/transitions.css`**

### La página debe contener 6 ejemplos diferentes de transitions:

#### Ejemplo 1: Botón con Hover
- Cambia color de fondo al hacer hover
- Transición suave de 0.3 segundos
- Cambia también el color del texto

#### Ejemplo 2: Imagen con Zoom
- Al hacer hover, la imagen se agranda (scale)
- Usa `transform: scale(1.1)`
- Transición de 0.5 segundos

#### Ejemplo 3: Card con Elevación
- Card que se "eleva" al hacer hover
- Usa `transform: translateY(-10px)`
- Agrega sombra más pronunciada
- Múltiples propiedades en transición

#### Ejemplo 4: Menu Item con Underline Animado
- Línea que aparece debajo del texto al hover
- Usa pseudo-elemento `::after` con `transform: scaleX()`
- Animación de izquierda a derecha

#### Ejemplo 5: Progress Bar
- Barra que se llena al hacer hover en el contenedor
- Usa `width` con transition
- Timing function: ease-out

#### Ejemplo 6: Rotación de Ícono
- Ícono que rota 180° al hover
- Usa `transform: rotate(180deg)`
- Experimenta con diferentes timing functions

### Requisitos Específicos:
- Todos los ejemplos deben usar `transition`
- Usa al menos 3 timing functions diferentes
- Usa `transform` en al menos 3 ejemplos
- Al menos un ejemplo debe animar múltiples propiedades
- Usa delays en al menos un ejemplo
- Todos los cambios deben ser reversibles (vuelven al estado original)

## ✅ Checklist de Autoevaluación

### Conceptos Básicos:
- [ ] Entiendes qué es una transition (cambio suave entre estados)
- [ ] Sabes que necesitas dos estados: normal y :hover
- [ ] Entiendes que la transition va en el estado normal, no en :hover

### Propiedad Transition:
- [ ] Usas la sintaxis shorthand: `transition: property duration timing-function delay`
- [ ] O usas propiedades individuales cuando es necesario
- [ ] Todas las transitions tienen una duración definida

### Duration (Duración):
- [ ] Usas segundos (s) o milisegundos (ms)
- [ ] Las duraciones son apropiadas (0.2s-0.5s típicamente)
- [ ] No son demasiado lentas (>1s puede frustrar)

### Timing Functions:
- [ ] Usas `ease` (por defecto, acelera y desacelera)
- [ ] Usas `linear` (velocidad constante)
- [ ] Usas `ease-in` (acelera al inicio)
- [ ] Usas `ease-out` (desacelera al final)
- [ ] Pruebas con `ease-in-out`

### Transform:
- [ ] Usas `translateX()` o `translateY()` para mover
- [ ] Usas `scale()` para agrandar/achicar
- [ ] Usas `rotate()` para rotar
- [ ] Entiendes que transform es más eficiente que cambiar position

### Múltiples Propiedades:
- [ ] Al menos un ejemplo anima 2+ propiedades simultáneamente
- [ ] Usas `transition: all` o listas las propiedades separadas por coma
- [ ] Cada propiedad puede tener diferente duración si es necesario

### Estados Reversibles:
- [ ] Al quitar el hover, el elemento vuelve a su estado original
- [ ] La transition funciona en ambas direcciones
- [ ] No hay "saltos" bruscos

### Ejemplos Completados:
- [ ] Ejemplo 1: Botón funciona correctamente
- [ ] Ejemplo 2: Imagen hace zoom suave
- [ ] Ejemplo 3: Card se eleva con sombra
- [ ] Ejemplo 4: Línea aparece bajo el texto
- [ ] Ejemplo 5: Barra de progreso se llena
- [ ] Ejemplo 6: Ícono rota

### UX y Performance:
- [ ] Las animaciones son suaves, sin tirones
- [ ] Las duraciones son cómodas (ni muy rápidas ni muy lentas)
- [ ] No marea al usuario con demasiado movimiento

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa `transition-delay` para secuenciar animaciones
- Combina múltiples transforms: `transform: scale(1.1) rotate(5deg)`
- Crea un loading spinner con `rotate` y `animation` (no transition)
- Usa `cubic-bezier()` personalizado
- Experimenta con `will-change` para performance

## 🐛 Errores Comunes

### Error 1: Poner transition en :hover en lugar del estado base
```css
/* ❌ INCORRECTO - solo anima al hacer hover, no al salir */
.boton:hover {
    background: blue;
    transition: 0.3s;
}
```

**Solución**:
```css
/* ✅ CORRECTO - anima en ambas direcciones */
.boton {
    background: gray;
    transition: background 0.3s ease;
}

.boton:hover {
    background: blue;
}
```

### Error 2: Animar propiedades no animables
```css
/* ❌ NO SE PUEDE ANIMAR */
.elemento {
    display: block;
    transition: display 0.3s; /* display no es animable */
}

.elemento:hover {
    display: none;
}
```

**Alternativa**:
```css
/* ✅ USA OPACITY Y VISIBILITY */
.elemento {
    opacity: 1;
    visibility: visible;
    transition: opacity 0.3s, visibility 0.3s;
}

.elemento.oculto {
    opacity: 0;
    visibility: hidden;
}
```

### Error 3: Olvidar unidades en duration
```css
/* ❌ INCORRECTO */
.elemento {
    transition: all 300; /* Falta "ms" */
}
```

**Solución**:
```css
/* ✅ CORRECTO */
.elemento {
    transition: all 300ms; /* o 0.3s */
}
```

### Error 4: Usar top/left sin position
```css
/* ❌ NO FUNCIONA */
.elemento {
    top: 0;
    transition: top 0.3s;
}

.elemento:hover {
    top: 20px; /* No tiene efecto sin position */
}
```

**Solución**:
```css
/* ✅ CORRECTO - Usa transform (más eficiente) */
.elemento {
    transform: translateY(0);
    transition: transform 0.3s;
}

.elemento:hover {
    transform: translateY(20px);
}
```

## 🔗 Recursos Adicionales

### Herramientas:
- [Cubic-Bezier Generator](https://cubic-bezier.com/) - Crea timing functions personalizadas
- [Ceaser](https://matthewlein.com/tools/ceaser) - Easing functions visualizadas
- [Animista](https://animista.net/) - Generador de animaciones CSS

### Guías:
- [List of Animatable Properties](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_animated_properties)
- [High Performance Animations](https://web.dev/animations-guide/)

## 📊 Timing Functions Explicadas

```css
/* LINEAR: Velocidad constante */
transition: all 0.3s linear;

/* EASE (default): Lento-rápido-lento */
transition: all 0.3s ease;

/* EASE-IN: Empieza lento, termina rápido */
transition: all 0.3s ease-in;

/* EASE-OUT: Empieza rápido, termina lento */
transition: all 0.3s ease-out;

/* EASE-IN-OUT: Muy lento al inicio y final */
transition: all 0.3s ease-in-out;

/* CUBIC-BEZIER personalizado */
transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55); /* bouncy */
```

## 💡 Plantilla de Inicio

```css
/* Ejemplo 1: Botón Simple */
.boton {
    background-color: #3498db;
    color: white;
    padding: 1rem 2rem;
    border: none;
    cursor: pointer;
    transition: background-color 0.3s ease, transform 0.2s ease;
}

.boton:hover {
    background-color: #2980b9;
    transform: translateY(-2px);
}

/* Ejemplo 2: Card con Elevación */
.card {
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
    transform: translateY(-10px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}

/* Ejemplo 3: Imagen con Zoom */
.imagen-container {
    overflow: hidden; /* Para que el zoom no se salga */
}

.imagen {
    transition: transform 0.5s ease;
}

.imagen:hover {
    transform: scale(1.1);
}

/* Agrega más ejemplos... */
```

## 🎨 Propiedades Comúnmente Animadas

| Propiedad | Uso | Performance |
|-----------|-----|-------------|
| `opacity` | Fade in/out | ⚡ Excelente |
| `transform` | Mover, rotar, escalar | ⚡ Excelente |
| `background-color` | Cambio de color | ✅ Buena |
| `color` | Color de texto | ✅ Buena |
| `box-shadow` | Sombras | ⚠️ Media |
| `width/height` | Tamaño | ❌ Pobre (causa reflow) |
| `left/top` | Posición | ❌ Pobre (usa transform) |

**Tip**: Para mejor performance, prefiere `transform` y `opacity`.

## 📸 Ejemplo Visual de Timing Functions

```
LINEAR
─────────────────────────► Velocidad constante

EASE
    ┌──────────┐
───┘            └────────► Acelera, luego desacelera

EASE-IN
             ┌────────────
────────────┘             ► Empieza lento, acelera

EASE-OUT
┌────────────
│             └──────────► Empieza rápido, desacelera
```

## 🧪 Testing Tips

1. **Hover repetidamente** sobre los elementos para probar la reversibilidad
2. **Prueba en diferentes navegadores** (Chrome, Firefox, Safari)
3. **Usa DevTools** para modificar timing functions en tiempo real
4. **Reduce motion**: Respeta `prefers-reduced-motion` para accesibilidad

```css
/* Respeta preferencias de accesibilidad */
@media (prefers-reduced-motion: reduce) {
    * {
        transition: none !important;
    }
}
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 12 - Pseudo-elementos](./ejercicio-12-pseudo-elementos.md)
