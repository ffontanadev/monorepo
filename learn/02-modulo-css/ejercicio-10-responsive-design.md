# Ejercicio 10 - Responsive Design

## 🎯 Objetivo
Aprender a crear diseños responsive que se adapten a diferentes tamaños de pantalla usando media queries y la metodología Mobile-First.

## 📚 Conceptos Clave
- Media queries: `@media`
- Breakpoints comunes (mobile, tablet, desktop)
- Mobile-First vs Desktop-First
- Unidades relativas: rem, em, %, vw, vh
- Viewport meta tag
- Flexbox responsive

## 🔍 Investigación Previa (30 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Media queries](https://developer.mozilla.org/es/docs/Web/CSS/Media_Queries/Using_media_queries)
- [MDN - Responsive design](https://developer.mozilla.org/es/docs/Learn/CSS/CSS_layout/Responsive_Design)
- [Google - Responsive Web Design Basics](https://web.dev/responsive-web-design-basics/)

### Preguntas para investigar:
1. ¿Qué es Mobile-First y por qué es importante?
2. ¿Cuáles son los breakpoints más comunes?
3. ¿Qué hace el viewport meta tag?
4. ¿Cuál es la diferencia entre `min-width` y `max-width` en media queries?
5. ¿Cuándo usar unidades relativas vs absolutas?

## 💻 Tarea

Crea dos archivos:
1. **`pagina-responsive.html`**
2. **`css/responsive.css`**

### La página debe contener:

**Estructura HTML**:
- Header con logo y navegación
- Hero section con título y llamado a la acción
- Grid de 3 características/servicios con iconos
- Sección "Sobre nosotros" con imagen y texto
- Footer con información de contacto

**Comportamiento Responsive**:

#### Mobile (< 768px) - BASE (Mobile-First):
- Navegación en columna o hamburguesa simple
- Hero ocupa 100% del ancho
- Características en 1 columna
- Imagen de "Sobre nosotros" arriba, texto abajo
- Footer en columna

#### Tablet (768px - 1023px):
- Navegación horizontal
- Características en 2 columnas
- Imagen y texto lado a lado (50/50)

#### Desktop (≥ 1024px):
- Navegación extendida con más espacio
- Características en 3 columnas
- Contenedor con max-width centrado
- Espaciados más generosos

### Requisitos Específicos:
- Incluye el viewport meta tag en HTML
- Usa enfoque Mobile-First (estilos base para móvil)
- Define al menos 2 breakpoints con `@media (min-width: ...)`
- Usa unidades relativas (rem, em) para texto
- Usa % o flexbox para layouts
- Las imágenes deben ser responsive (`max-width: 100%`)
- Ningún scroll horizontal en ningún tamaño

## ✅ Checklist de Autoevaluación

### Meta Tag:
- [ ] El HTML incluye: `<meta name="viewport" content="width=device-width, initial-scale=1.0">`
- [ ] Sin este tag, el responsive no funcionará en móviles reales

### Mobile-First:
- [ ] Los estilos base (sin media query) son para móvil
- [ ] Las media queries usan `min-width` (no `max-width`)
- [ ] El diseño funciona en pantallas pequeñas sin media queries

### Media Queries:
- [ ] Hay al menos 2 breakpoints definidos
- [ ] Se usa `@media (min-width: 768px)` para tablet
- [ ] Se usa `@media (min-width: 1024px)` para desktop
- [ ] Las media queries están al final del CSS

### Layouts Responsive:
- [ ] La navegación cambia de columna a row
- [ ] El grid de características va de 1 → 2 → 3 columnas
- [ ] La sección "Sobre nosotros" cambia de columna a row
- [ ] El footer se adapta según el tamaño

### Imágenes Responsive:
- [ ] Todas las imágenes tienen `max-width: 100%`
- [ ] Las imágenes tienen `height: auto`
- [ ] No se deforman al cambiar tamaño de pantalla

### Unidades Relativas:
- [ ] Font-sizes usan rem o em (no solo px)
- [ ] Paddings y margins usan unidades relativas cuando es apropiado
- [ ] Los contenedores usan % o max-width

### Contenedor:
- [ ] En desktop, hay un contenedor con max-width (ej: 1200px)
- [ ] El contenedor está centrado con `margin: 0 auto`
- [ ] En móvil, el contenedor es 100% width

### Testing:
- [ ] Probaste en Chrome DevTools con diferentes dispositivos
- [ ] Probaste redimensionando la ventana del navegador
- [ ] No hay scroll horizontal en ningún tamaño
- [ ] El contenido es legible en todos los tamaños

### Código Limpio:
- [ ] Los breakpoints están organizados y comentados
- [ ] No hay código duplicado innecesariamente
- [ ] Los comentarios explican qué hace cada media query

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa `clamp()` para tamaños de fuente fluidos
- Implementa un menú hamburguesa funcional (con checkbox hack)
- Usa CSS Grid para el layout de características
- Agrega orientación: `@media (orientation: landscape)`
- Usa `picture` element para imágenes responsive diferentes

## 🐛 Errores Comunes

### Error 1: Olvidar el viewport meta tag
```html
<!-- ❌ SIN ESTO, EL RESPONSIVE NO FUNCIONA EN MÓVILES -->
<head>
    <title>Mi Página</title>
</head>
```

**Solución**:
```html
<!-- ✅ SIEMPRE INCLUIR -->
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Página</title>
</head>
```

### Error 2: Usar max-width en Mobile-First
```css
/* ❌ ESTO ES DESKTOP-FIRST (más difícil de mantener) */
.grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
}

@media (max-width: 768px) {
    .grid {
        grid-template-columns: 1fr;
    }
}
```

**Solución Mobile-First**:
```css
/* ✅ BASE PARA MÓVIL */
.grid {
    display: grid;
    grid-template-columns: 1fr;
}

/* Tablet */
@media (min-width: 768px) {
    .grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

/* Desktop */
@media (min-width: 1024px) {
    .grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
```

### Error 3: Imágenes que rompen el layout
```css
/* ❌ La imagen puede ser más ancha que el contenedor */
img {
    width: 800px;
}
```

**Solución**:
```css
/* ✅ SIEMPRE responsive */
img {
    max-width: 100%;
    height: auto;
}
```

### Error 4: Usar solo píxeles
```css
/* ❌ No escala bien */
.texto {
    font-size: 16px;
    padding: 20px;
}
```

**Mejor práctica**:
```css
/* ✅ Más flexible */
.texto {
    font-size: 1rem;      /* 16px por defecto */
    padding: 1.25rem;     /* 20px por defecto */
}
```

## 🔗 Recursos Adicionales

### Herramientas de Testing:
- [Chrome DevTools Device Mode](https://developer.chrome.com/docs/devtools/device-mode/)
- [Responsive Design Checker](https://responsivedesignchecker.com/)
- [Am I Responsive?](https://ui.dev/amiresponsive) - Screenshot multi-device

### Guías:
- [Every Layout](https://every-layout.dev/) - Patrones responsive
- [Responsive Typography](https://www.smashingmagazine.com/2016/05/fluid-typography/)

## 📊 Breakpoints Comunes

```css
/* Mobile-First Approach */

/* Mobile (base, sin media query) */
/* < 768px */

/* Tablet */
@media (min-width: 768px) {
    /* Estilos para tablet */
}

/* Desktop */
@media (min-width: 1024px) {
    /* Estilos para desktop */
}

/* Large Desktop (opcional) */
@media (min-width: 1440px) {
    /* Estilos para pantallas grandes */
}
```

## 📸 Comportamiento Visual Esperado

### Mobile (< 768px):
```
┌─────────────┐
│   LOGO      │
│  ─────────  │
│ [Nav Item]  │
│ [Nav Item]  │
│ [Nav Item]  │
├─────────────┤
│    HERO     │
│   Título    │
│  [Botón]    │
├─────────────┤
│ [Feature 1] │
│ [Feature 2] │
│ [Feature 3] │
├─────────────┤
│   [Imagen]  │
│    Texto    │
└─────────────┘
```

### Desktop (≥ 1024px):
```
┌──────────────────────────────────┐
│ LOGO    [Nav] [Nav] [Nav] [Nav]  │
├──────────────────────────────────┤
│         HERO - Título            │
│          [Botón]                 │
├──────────────────────────────────┤
│ [Feature] [Feature] [Feature]    │
├──────────────────────────────────┤
│ [Imagen] │   Texto Sobre         │
│          │   Nosotros...         │
└──────────────────────────────────┘
```

## 💡 Plantilla Mobile-First

```css
/* ===== MOBILE BASE (< 768px) ===== */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: Arial, sans-serif;
    font-size: 16px;
    line-height: 1.6;
}

.container {
    width: 100%;
    padding: 1rem;
}

.nav {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1rem;
}

img {
    max-width: 100%;
    height: auto;
}

/* ===== TABLET (≥ 768px) ===== */
@media (min-width: 768px) {
    .container {
        padding: 2rem;
    }

    .nav {
        flex-direction: row;
        justify-content: space-around;
    }

    .grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

/* ===== DESKTOP (≥ 1024px) ===== */
@media (min-width: 1024px) {
    .container {
        max-width: 1200px;
        margin: 0 auto;
    }

    .grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
```

## 🧪 Cómo Testear

1. **Chrome DevTools**:
   - F12 → Toggle Device Toolbar (Ctrl+Shift+M)
   - Prueba diferentes dispositivos predefinidos
   - Prueba con "Responsive" y arrastra para cambiar tamaño

2. **Redimensiona la ventana**:
   - Arrastra la ventana del navegador
   - Observa cómo cambia el layout en los breakpoints

3. **Dispositivos reales**:
   - Prueba en tu smartphone
   - Prueba en una tablet si tienes acceso

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 11 - CSS Transitions](./ejercicio-11-css-transitions.md)
