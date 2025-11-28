# 🎨 Módulo 2: CSS - Diseño y Presentación

**Duración**: Semanas 3-5
**Ejercicios**: 7 prácticos
**Tiempo estimado**: 20-26 horas

## 🎯 Objetivos del Módulo

Al completar este módulo serás capaz de:
- ✅ Estilizar elementos HTML con CSS
- ✅ Entender y aplicar el Box Model
- ✅ Crear layouts con Flexbox
- ✅ Diseñar páginas responsive con Mobile-First
- ✅ Aplicar transiciones y animaciones
- ✅ Trabajar con pseudo-elementos (::before, ::after)
- ✅ Usar y manipular SVG con CSS

## 📚 ¿Qué es CSS?

**CSS** (Cascading Style Sheets) es el lenguaje que define la **presentación visual** de las páginas web. Si HTML es el esqueleto, CSS es la piel, ropa y maquillaje.

CSS te permite controlar:
- 🎨 **Colores** y tipografía
- 📐 **Tamaños** y espaciados
- 📱 **Layouts** y posicionamiento
- ✨ **Animaciones** y transiciones
- 🖼️ **Diseño responsive** para diferentes dispositivos

## 🗂️ Ejercicios del Módulo

### Semana 3: Fundamentos de CSS

#### [Ejercicio 07 - Primeros Estilos](./ejercicio-07-primeros-estilos.md)
Aprende selectores CSS, colores, tipografía y cómo aplicar estilos.
- Conceptos: Selectores, propiedades, valores, especificidad
- Tiempo: 2-3 horas

#### [Ejercicio 08 - Box Model](./ejercicio-08-box-model.md)
Domina el modelo de caja: margin, padding, border y content.
- Conceptos: Box model, width, height, unidades de medida
- Tiempo: 2-3 horas

---

### Semana 4: Layouts y Diseño Responsive

#### [Ejercicio 09 - Flexbox Básico](./ejercicio-09-flexbox-basico.md)
Crea layouts flexibles con Flexbox.
- Conceptos: display flex, justify-content, align-items, flex-direction
- Tiempo: 3-4 horas

#### [Ejercicio 10 - Responsive Design](./ejercicio-10-responsive-design.md)
Diseña páginas que se adapten a cualquier dispositivo.
- Conceptos: Media queries, mobile-first, breakpoints, unidades relativas
- Tiempo: 3-4 horas

---

### Semana 5: Técnicas Avanzadas

#### [Ejercicio 11 - CSS Transitions](./ejercicio-11-css-transitions.md)
Agrega animaciones suaves a tus elementos.
- Conceptos: transition, transform, hover effects, timing functions
- Tiempo: 2-3 horas

#### [Ejercicio 12 - Pseudo-elementos](./ejercicio-12-pseudo-elementos.md)
Usa ::before y ::after para efectos creativos.
- Conceptos: ::before, ::after, content, position
- Tiempo: 2-3 horas

#### [Ejercicio 13 - SVG Básico](./ejercicio-13-svg-basico.md)
Trabaja con gráficos vectoriales y estilízalos con CSS.
- Conceptos: SVG inline, fill, stroke, viewBox, responsividad
- Tiempo: 2-3 horas

---

## 📖 Recursos de Aprendizaje

### Documentación Oficial:
- [MDN - CSS](https://developer.mozilla.org/es/docs/Web/CSS)
- [MDN - Guía de CSS](https://developer.mozilla.org/es/docs/Learn/CSS)
- [CSS Reference](https://cssreference.io/) - Visual guide

### Tutoriales Interactivos:
- [CSS Diner](https://flukeout.github.io/) - Aprende selectores jugando
- [Flexbox Froggy](https://flexboxfroggy.com/) - Aprende Flexbox jugando
- [Grid Garden](https://cssgridgarden.com/) - Aprende Grid jugando

### Videos Recomendados:
- [CSS Crash Course - Traversy Media](https://www.youtube.com/watch?v=yfoY53QXEnI)
- [Flexbox Tutorial - freeCodeCamp](https://www.youtube.com/watch?v=-Wlt8NRtOpo)

### Herramientas Útiles:
- [Chrome DevTools](https://developer.chrome.com/docs/devtools/) - Inspecciona y modifica CSS en vivo
- [Can I Use](https://caniuse.com/) - Compatibilidad de propiedades CSS
- [CSS Gradient Generator](https://cssgradient.io/)

---

## 💡 Consejos para Este Módulo

### ✅ Buenas Prácticas:
1. **Mobile-First**: Diseña primero para móvil, luego para desktop
2. **Nombres de clase descriptivos**: `.button-primary` mejor que `.btn1`
3. **Evita IDs para estilos**: Usa clases, son más reutilizables
4. **Organiza tu CSS**: Agrupa estilos relacionados
5. **Usa variables CSS**: Para colores y valores repetitivos
6. **Comenta tu código**: Especialmente en secciones complejas

### ❌ Errores Comunes:
- Usar `!important` en exceso (evítalo siempre que sea posible)
- No entender especificidad de selectores
- Usar unidades absolutas (px) para todo
- No probar en diferentes navegadores
- Olvidar el box-sizing: border-box
- No usar herramientas de desarrollo del navegador

---

## 🎯 Checklist del Módulo

Marca cada ejercicio al completarlo:

- [ ] Ejercicio 07 - Primeros Estilos
- [ ] Ejercicio 08 - Box Model
- [ ] Ejercicio 09 - Flexbox Básico
- [ ] Ejercicio 10 - Responsive Design
- [ ] Ejercicio 11 - CSS Transitions
- [ ] Ejercicio 12 - Pseudo-elementos
- [ ] Ejercicio 13 - SVG Básico

**Al completar todos los ejercicios**, estarás listo para continuar con:
### 👉 [Módulo 3: JavaScript - Interactividad](../03-modulo-javascript/README.md)

---

## 📝 Estructura de Archivos Recomendada

```
mi-proyecto/
├── index.html
├── css/
│   ├── styles.css
│   └── reset.css (opcional)
├── images/
│   └── ...
└── README.md
```

### Vincular CSS en HTML:
```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Página</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <!-- Tu contenido -->
</body>
</html>
```

---

## 🎨 Plantilla CSS Base

```css
/* Reset básico */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* Variables CSS */
:root {
    --color-primary: #3498db;
    --color-secondary: #2ecc71;
    --color-dark: #2c3e50;
    --color-light: #ecf0f1;
    --font-main: 'Arial', sans-serif;
    --spacing-small: 8px;
    --spacing-medium: 16px;
    --spacing-large: 32px;
}

/* Estilos generales */
body {
    font-family: var(--font-main);
    line-height: 1.6;
    color: var(--color-dark);
}

/* Mobile-first: estilos base para móvil */
.container {
    width: 100%;
    padding: var(--spacing-medium);
}

/* Tablets y superiores */
@media (min-width: 768px) {
    .container {
        max-width: 720px;
        margin: 0 auto;
    }
}

/* Desktop */
@media (min-width: 1024px) {
    .container {
        max-width: 960px;
    }
}
```

---

## 🔍 Herramientas de Debugging CSS

### Chrome DevTools:
1. Click derecho → **Inspeccionar**
2. Pestaña **Elements** → Panel **Styles**
3. Modifica CSS en tiempo real
4. Ve el box model visualmente
5. Prueba diferentes valores

### Tips de DevTools:
- `Ctrl/Cmd + Shift + C`: Inspector de elementos
- Hover sobre elementos para ver dimensiones
- Desactiva/activa propiedades con checkbox
- Copia estilos computados

---

## 📚 Glosario CSS

| Término | Significado |
|---------|-------------|
| **Selector** | Elemento que quieres estilizar |
| **Propiedad** | Característica que quieres cambiar (color, font-size) |
| **Valor** | El estilo específico que aplicas |
| **Especificidad** | Prioridad de un selector sobre otro |
| **Cascada** | Cómo CSS resuelve conflictos de estilos |
| **Responsive** | Diseño que se adapta a diferentes pantallas |
| **Breakpoint** | Punto donde el diseño cambia (media query) |

---

## 🎓 Metodologías CSS (Avanzado)

Aunque no las usarás aún, es bueno conocerlas:
- **BEM**: Block Element Modifier (naming convention)
- **CSS Modules**: Estilos con alcance local
- **Utility-First**: Como Tailwind CSS
- **CSS-in-JS**: Estilos en JavaScript (React)

Por ahora, enfócate en **entender los fundamentos** antes de adoptar metodologías.

---

**¡Comienza con el [Ejercicio 07](./ejercicio-07-primeros-estilos.md)! 🚀**
