# Ejercicio 07 - Primeros Estilos

## 🎯 Objetivo
Aprender los fundamentos de CSS: selectores, propiedades, colores y tipografía para estilizar tu primera página web.

## 📚 Conceptos Clave
- Sintaxis CSS: selector { propiedad: valor; }
- Tipos de selectores: elemento, clase, ID
- Propiedades de color: color, background-color
- Propiedades de tipografía: font-family, font-size, font-weight
- Especificidad de selectores
- Cómo vincular CSS con HTML

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Primeros pasos con CSS](https://developer.mozilla.org/es/docs/Learn/CSS/First_steps)
- [MDN - Selectores CSS](https://developer.mozilla.org/es/docs/Web/CSS/CSS_Selectors)
- [W3Schools - CSS Syntax](https://www.w3schools.com/css/css_syntax.asp)

### Preguntas para investigar:
1. ¿Cuál es la diferencia entre un selector de clase y un selector de ID?
2. ¿Qué significa "cascada" en CSS?
3. ¿Cuáles son las diferentes formas de definir colores en CSS?
4. ¿Qué es la especificidad y por qué importa?
5. ¿Cuáles son las formas de incluir CSS en HTML?

## 💻 Tarea

Crea dos archivos:
1. **`tarjeta-perfil.html`** - Una tarjeta de perfil personal
2. **`css/styles.css`** - El archivo de estilos

### La tarjeta de perfil debe contener:

**HTML**:
- Título con tu nombre
- Subtítulo con tu profesión/ocupación
- Párrafo "Sobre mí" (2-3 líneas)
- Lista de 3 habilidades
- Botón de "Contactar"

**CSS** que debe aplicar:
1. **Estilos globales**:
   - Fuente personalizada para todo el body
   - Color de fondo para la página
   - Reseteo de márgenes

2. **Estilos para el título**:
   - Color específico
   - Tamaño de fuente grande
   - Peso de fuente (bold)

3. **Estilos para el subtítulo**:
   - Color diferente al título
   - Tamaño de fuente mediano
   - Estilo itálica

4. **Estilos para párrafos**:
   - Color de texto
   - Interlineado (line-height)
   - Espaciado

5. **Estilos para el botón**:
   - Color de fondo
   - Color de texto
   - Sin borde o borde personalizado
   - Padding
   - Cursor pointer

### Requisitos Específicos:
- Crea una carpeta `/css/` y guarda `styles.css` ahí
- Vincula el CSS en el HTML con `<link>`
- Usa al menos 3 selectores de clase diferentes
- Usa al menos 1 selector de ID
- Define colores de al menos 2 formas diferentes (nombre, hex, rgb)
- Usa al menos 2 fuentes diferentes

## ✅ Checklist de Autoevaluación

### Estructura de Archivos:
- [ ] Existe la carpeta `/css/`
- [ ] El archivo `styles.css` está en `/css/`
- [ ] El CSS está vinculado correctamente en el HTML
- [ ] Al cargar la página, los estilos se aplican

### Selectores:
- [ ] Se usan selectores de elemento (ej: `body`, `h1`, `p`)
- [ ] Se usan al menos 3 selectores de clase (`.nombre-clase`)
- [ ] Se usa al menos 1 selector de ID (`#nombre-id`)
- [ ] Los nombres de clases son descriptivos
- [ ] No hay espacios en nombres de clases (usa guiones)

### Propiedades de Color:
- [ ] Se usa `color` para color de texto
- [ ] Se usa `background-color` para fondos
- [ ] Se usan al menos 2 formatos: hex (#3498db), rgb, o nombre
- [ ] Los colores tienen buen contraste (legibles)

### Propiedades de Tipografía:
- [ ] Se usa `font-family` (al menos 2 fuentes)
- [ ] Se usa `font-size` (al menos 3 tamaños diferentes)
- [ ] Se usa `font-weight` (normal, bold, o número)
- [ ] Se usa `line-height` para legibilidad
- [ ] Se usa `text-align` donde sea apropiado

### Espaciado:
- [ ] Se resetean margins y paddings en body
- [ ] Se usa `margin` para espaciado externo
- [ ] Se usa `padding` para espaciado interno
- [ ] Los espacios hacen la página legible

### Código Limpio:
- [ ] El CSS está correctamente indentado
- [ ] Cada propiedad termina con punto y coma (;)
- [ ] Las propiedades están en minúsculas
- [ ] Los valores están escritos correctamente

### Especificidad:
- [ ] Se entiende qué selector tiene más peso
- [ ] No se usa `!important` innecesariamente
- [ ] Los IDs se usan solo cuando es necesario (preferir clases)

### Resultado Visual:
- [ ] La página se ve estilizada (no HTML plano)
- [ ] Los colores son armoniosos
- [ ] La tipografía es legible
- [ ] El diseño es atractivo

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa Google Fonts para fuentes personalizadas
- Agrega `text-decoration` para remover subrayado de enlaces
- Usa `letter-spacing` para espaciado entre letras
- Aplica `text-transform` (uppercase, lowercase, capitalize)
- Agrega comentarios en tu CSS organizando secciones

## 🐛 Errores Comunes

### Error 1: CSS no se aplica (ruta incorrecta)
```html
<!-- ❌ INCORRECTO -->
<link rel="stylesheet" href="styles.css">
```

**Solución**:
```html
<!-- ✅ CORRECTO (si css está en carpeta /css/) -->
<link rel="stylesheet" href="css/styles.css">
```

### Error 2: Olvidar punto en selector de clase
```css
/* ❌ INCORRECTO */
boton {
    background: blue;
}
```

**Solución**:
```css
/* ✅ CORRECTO */
.boton {
    background: blue;
}
```

### Error 3: Olvidar punto y coma
```css
/* ❌ INCORRECTO */
h1 {
    color: blue
    font-size: 24px
}
```

**Solución**:
```css
/* ✅ CORRECTO */
h1 {
    color: blue;
    font-size: 24px;
}
```

### Error 4: Especificidad confusa
```css
/* El ID tiene más peso que la clase */
#titulo { color: red; }    /* Gana este */
.titulo { color: blue; }
```

**Orden de especificidad** (de menor a mayor):
1. Selectores de elemento (`h1`, `p`)
2. Selectores de clase (`.clase`)
3. Selectores de ID (`#id`)
4. Estilos inline (`style="..."`)
5. `!important` (evitar)

## 🔗 Recursos Adicionales

### Paletas de Colores:
- [Coolors](https://coolors.co/) - Generador de paletas
- [Adobe Color](https://color.adobe.com/) - Rueda de color
- [Color Hunt](https://colorhunt.co/) - Paletas prediseñadas

### Fuentes:
- [Google Fonts](https://fonts.google.com/) - Fuentes gratuitas
- [Font Pair](https://fontpair.co/) - Combinaciones de fuentes

### Herramientas:
- [CSS Reference](https://cssreference.io/) - Guía visual de propiedades
- [CSS Tricks](https://css-tricks.com/) - Tutoriales y guías

## 📸 Ejemplo de Resultado Esperado

```
┌─────────────────────────────────┐
│                                 │
│        Juan Pérez               │  ← Título grande, color
│    Desarrollador Web            │  ← Subtítulo, itálica
│                                 │
│  Soy un estudiante de          │
│  desarrollo web apasionado      │  ← Párrafo, buena legibilidad
│  por crear experiencias...      │
│                                 │
│  Habilidades:                   │
│  • HTML                         │
│  • CSS                          │  ← Lista estilizada
│  • JavaScript                   │
│                                 │
│     [ Contactar ]               │  ← Botón estilizado
│                                 │
└─────────────────────────────────┘
```

## 💡 Tips de Diseño

### Colores:
- Usa 2-3 colores principales máximo
- Asegura buen contraste (texto oscuro en fondo claro)
- Herramienta: [Contrast Checker](https://webaim.org/resources/contrastchecker/)

### Tipografía:
- Usa máximo 2 fuentes (una para títulos, otra para texto)
- Tamaño mínimo para legibilidad: 16px
- Line-height recomendado: 1.5 - 1.6

### Espaciado:
- Usa espacios consistentes (8px, 16px, 24px, 32px)
- El espacio en blanco mejora la legibilidad

## 📋 Plantilla CSS de Inicio

```css
/* Reset básico */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* Estilos globales */
body {
    font-family: Arial, sans-serif;
    background-color: #f5f5f5;
    color: #333;
    line-height: 1.6;
}

/* Contenedor de la tarjeta */
.tarjeta {
    background-color: white;
    /* Agrega más estilos aquí */
}

/* Título */
.titulo {
    color: #2c3e50;
    font-size: 32px;
    font-weight: bold;
}

/* Agrega más estilos... */
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 08 - Box Model](./ejercicio-08-box-model.md)
