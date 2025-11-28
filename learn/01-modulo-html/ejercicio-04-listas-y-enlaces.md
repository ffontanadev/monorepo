# Ejercicio 04 - Listas y Enlaces

## 🎯 Objetivo
Aprender a crear listas ordenadas y desordenadas, y dominar los enlaces para la navegación web.

## 📚 Conceptos Clave
- Listas desordenadas: `<ul>`, `<li>`
- Listas ordenadas: `<ol>`, `<li>`
- Listas anidadas
- Enlaces: `<a>`, atributo `href`
- Tipos de enlaces: internos, externos, anclas
- Atributos: `target`, `title`, `rel`

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Listas HTML](https://developer.mozilla.org/es/docs/Web/HTML/Element/ul)
- [MDN - Enlaces](https://developer.mozilla.org/es/docs/Learn/HTML/Introduction_to_HTML/Creating_hyperlinks)
- [W3Schools - HTML Lists](https://www.w3schools.com/html/html_lists.asp)

### Preguntas para investigar:
1. ¿Cuándo usar `<ul>` y cuándo `<ol>`?
2. ¿Qué hace el atributo `target="_blank"`?
3. ¿Cómo crear un enlace a otra sección de la misma página?
4. ¿Para qué sirve el atributo `title` en un enlace?
5. ¿Qué es y cuándo usar `rel="noopener"`?

## 💻 Tarea

Crea un archivo llamado `recursos-desarrollo.html` que contenga una página de recursos organizados con listas y enlaces.

### La página debe incluir:

1. **Header** con título "Recursos para Desarrollo Web"

2. **Menú de navegación** con enlaces a secciones:
   - Documentación
   - Tutoriales
   - Herramientas
   - Comunidades

3. **Sección "Documentación"** (id="documentacion"):
   - Lista **desordenada** con 4 enlaces a sitios de documentación:
     - MDN Web Docs
     - W3Schools
     - CSS-Tricks
     - JavaScript.info
   - Los enlaces deben abrir en nueva pestaña

4. **Sección "Tutoriales"** (id="tutoriales"):
   - Lista **ordenada** con 3 plataformas de aprendizaje:
     - freeCodeCamp
     - Codecademy
     - The Odin Project
   - Los enlaces deben abrir en nueva pestaña

5. **Sección "Herramientas"** (id="herramientas"):
   - Lista **desordenada** con sublistas anidadas:
     - Editores de código:
       - VS Code
       - Sublime Text
     - Navegadores:
       - Chrome
       - Firefox
   - Los enlaces pueden ser `#` por ahora

6. **Sección "Comunidades"** (id="comunidades"):
   - Lista desordenada con 3 comunidades
   - Enlaces externos con `target="_blank"` y `rel="noopener"`

### Requisitos Específicos:
- Usa navegación con anclas (`href="#seccion"`)
- Al menos 10 enlaces funcionales a sitios reales
- Al menos una lista anidada (2 niveles)
- Todos los enlaces externos deben abrir en nueva pestaña
- Usa el atributo `title` en al menos 3 enlaces

## ✅ Checklist de Autoevaluación

### Navegación:
- [ ] Hay un `<nav>` con enlaces a las 4 secciones
- [ ] Los enlaces de navegación usan anclas (`href="#documentacion"`)
- [ ] Al hacer clic, los enlaces llevan a la sección correcta
- [ ] Cada sección tiene su `id` correspondiente

### Listas Desordenadas:
- [ ] Hay al menos 2 listas desordenadas (`<ul>`)
- [ ] Cada item de lista usa `<li>`
- [ ] Las listas están correctamente anidadas

### Listas Ordenadas:
- [ ] Hay al menos 1 lista ordenada (`<ol>`)
- [ ] La numeración aparece automáticamente
- [ ] Los items usan `<li>`

### Enlaces:
- [ ] Hay al menos 10 enlaces `<a>` funcionales
- [ ] Los enlaces externos tienen URLs reales
- [ ] Los enlaces externos tienen `target="_blank"`
- [ ] Los enlaces externos tienen `rel="noopener"` (seguridad)
- [ ] Al menos 3 enlaces tienen atributo `title`
- [ ] El texto del enlace es descriptivo (no "click aquí")

### Listas Anidadas:
- [ ] Hay al menos una lista anidada
- [ ] La indentación muestra claramente el anidamiento
- [ ] El código HTML está correctamente estructurado

### Código Limpio:
- [ ] El código está correctamente indentado
- [ ] Las listas anidadas son fáciles de leer
- [ ] Todas las etiquetas están cerradas
- [ ] Los atributos están entre comillas

### Funcionalidad:
- [ ] Todos los enlaces de navegación interna funcionan
- [ ] Los enlaces externos abren en nueva pestaña
- [ ] No hay enlaces rotos
- [ ] El `title` aparece al pasar el mouse

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Agrega una lista de descripción `<dl>`, `<dt>`, `<dd>`
- Usa el atributo `download` en un enlace
- Crea un enlace de email: `href="mailto:ejemplo@email.com"`
- Agrega un enlace de teléfono: `href="tel:+123456789"`
- Usa el atributo `start` en una lista ordenada

## 🐛 Errores Comunes

### Error 1: Olvidar el `#` en anclas internas
```html
<!-- ❌ INCORRECTO -->
<a href="documentacion">Ir a Documentación</a>
<section id="documentacion">...</section>
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<a href="#documentacion">Ir a Documentación</a>
<section id="documentacion">...</section>
```

### Error 2: Anidar listas incorrectamente
```html
<!-- ❌ INCORRECTO -->
<ul>
    <li>Item 1</li>
    <ul>
        <li>Subitem 1</li>
    </ul>
</ul>
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<ul>
    <li>Item 1
        <ul>
            <li>Subitem 1</li>
        </ul>
    </li>
</ul>
```

### Error 3: Enlaces externos sin seguridad
```html
<!-- ❌ INSEGURO -->
<a href="https://ejemplo.com" target="_blank">Enlace</a>
```

**Solución**:
```html
<!-- ✅ SEGURO -->
<a href="https://ejemplo.com" target="_blank" rel="noopener">Enlace</a>
```

**Nota**: `rel="noopener"` previene vulnerabilidades de seguridad cuando usas `target="_blank"`.

### Error 4: Texto de enlace no descriptivo
```html
<!-- ❌ MAL PARA ACCESIBILIDAD -->
<a href="https://mdn.dev">Click aquí</a>
```

**Solución**:
```html
<!-- ✅ DESCRIPTIVO -->
<a href="https://mdn.dev">Documentación en MDN Web Docs</a>
```

## 🔗 Recursos Adicionales

- [Anatomía de un enlace - MDN](https://developer.mozilla.org/es/docs/Learn/HTML/Introduction_to_HTML/Creating_hyperlinks#anatom%C3%ADa_de_un_enlace)
- [rel=noopener explicado](https://mathiasbynens.github.io/rel-noopener/)
- [Buenas prácticas de enlaces](https://www.w3.org/WAI/WCAG21/Understanding/link-purpose-in-context.html)

## 📸 Ejemplo Visual

```
Recursos para Desarrollo Web

[Documentación] [Tutoriales] [Herramientas] [Comunidades]

─────────────────────────────────

Documentación
• MDN Web Docs
• W3Schools
• CSS-Tricks
• JavaScript.info

Tutoriales
1. freeCodeCamp
2. Codecademy
3. The Odin Project

Herramientas
• Editores de código
  • VS Code
  • Sublime Text
• Navegadores
  • Chrome
  • Firefox

Comunidades
• Stack Overflow
• Dev.to
• Discord freeCodeCamp
```

## 💡 Tips de Accesibilidad

- Usa texto descriptivo en enlaces (evita "click aquí", "más info")
- Los enlaces deben tener sentido fuera de contexto
- Usa `title` para información adicional, no esencial
- Asegúrate de que los enlaces sean visualmente distinguibles

## 🔐 Seguridad en Enlaces

Cuando uses `target="_blank"`:
```html
<a href="URL" target="_blank" rel="noopener noreferrer">Texto</a>
```
- `noopener`: Previene acceso a `window.opener`
- `noreferrer`: Previene envío de referrer (opcional)

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 05 - Imágenes y Multimedia](./ejercicio-05-imagenes-y-multimedia.md)
