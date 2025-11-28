# Ejercicio 03 - Textos y Formato

## 🎯 Objetivo
Dominar las diferentes etiquetas de texto y formato en HTML para crear contenido rico y bien estructurado.

## 📚 Conceptos Clave
- Jerarquía de encabezados: `<h1>` a `<h6>`
- Párrafos y saltos de línea: `<p>`, `<br>`
- Énfasis y importancia: `<strong>`, `<em>`, `<mark>`
- Separadores: `<hr>`
- Otros elementos de texto: `<small>`, `<sup>`, `<sub>`, `<code>`

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Fundamentos de texto en HTML](https://developer.mozilla.org/es/docs/Learn/HTML/Introduction_to_HTML/HTML_text_fundamentals)
- [MDN - Formateo de texto avanzado](https://developer.mozilla.org/es/docs/Learn/HTML/Introduction_to_HTML/Advanced_text_formatting)
- [W3Schools - HTML Text Formatting](https://www.w3schools.com/html/html_formatting.asp)

### Preguntas para investigar:
1. ¿Cuál es la diferencia entre `<strong>` y `<b>`?
2. ¿Cuál es la diferencia entre `<em>` y `<i>`?
3. ¿Cuándo usar `<br>` y cuándo crear un nuevo `<p>`?
4. ¿Qué niveles de encabezado existen y cómo usarlos?
5. ¿Para qué sirve la etiqueta `<mark>`?

## 💻 Tarea

Crea un archivo llamado `articulo-blog.html` que contenga un artículo de blog sobre un tema que te interese (tecnología, deportes, cocina, etc.).

### El artículo debe incluir:

1. **Título principal** (`<h1>`)
2. **Información del autor y fecha** (usa `<p>` con `<small>`)
3. **Línea separadora** después del encabezado (`<hr>`)
4. **Introducción** (1 párrafo)
5. **Tres secciones** con:
   - Subtítulo `<h2>`
   - 2-3 párrafos cada una
   - Uso de `<strong>` para palabras importantes
   - Uso de `<em>` para énfasis
   - Al menos un `<mark>` para resaltar
6. **Conclusión** con:
   - Subtítulo `<h2>`
   - 1-2 párrafos
7. **Nota al pie** usando `<small>`

### Requisitos Específicos:
- Usa al menos 4 etiquetas diferentes de formato
- Incluye 2 líneas separadoras `<hr>`
- Usa la jerarquía de encabezados correctamente
- El contenido debe ser original (escribe sobre algo que conozcas)

## ✅ Checklist de Autoevaluación

### Estructura de Encabezados:
- [ ] Hay un único `<h1>` (título principal)
- [ ] Los subtítulos usan `<h2>`
- [ ] La jerarquía es consistente (no saltas de h2 a h4)
- [ ] Cada sección tiene su encabezado

### Elementos de Texto:
- [ ] Hay múltiples párrafos `<p>` con contenido
- [ ] Se usa `<strong>` para palabras importantes (al menos 3 veces)
- [ ] Se usa `<em>` para énfasis (al menos 2 veces)
- [ ] Se usa `<mark>` para resaltar (al menos 1 vez)
- [ ] Hay información de autor con `<small>`

### Separadores y Formato:
- [ ] Hay al menos 2 separadores `<hr>`
- [ ] Los `<hr>` separan secciones lógicamente
- [ ] No se usa `<br>` innecesariamente (solo donde tiene sentido)

### Contenido:
- [ ] El artículo tiene un tema claro
- [ ] La introducción presenta el tema
- [ ] Las secciones desarrollan el contenido
- [ ] La conclusión cierra el artículo
- [ ] El texto es legible y tiene sentido

### Código Limpio:
- [ ] El código está correctamente indentado
- [ ] Las etiquetas están en minúsculas
- [ ] Todas las etiquetas están cerradas
- [ ] El archivo tiene la estructura HTML5 completa

### Validación:
- [ ] El artículo se visualiza correctamente
- [ ] El formato de texto es visible (negritas, cursivas, etc.)
- [ ] No hay errores en la consola

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa `<blockquote>` para una cita
- Agrega `<code>` si mencionas código
- Usa `<sup>` para referencias (ej: "según estudio[1]")
- Incluye `<abbr>` para abreviaturas con título
- Agrega `<time>` para la fecha de publicación

## 🐛 Errores Comunes

### Error 1: Usar `<br>` para espacio entre párrafos
```html
<!-- ❌ INCORRECTO -->
<p>Primer párrafo</p>
<br><br>
<p>Segundo párrafo</p>
```

**Solución**: Los párrafos ya tienen espacio automático:
```html
<!-- ✅ CORRECTO -->
<p>Primer párrafo</p>
<p>Segundo párrafo</p>
```

### Error 2: Usar `<b>` e `<i>` en lugar de `<strong>` y `<em>`
```html
<!-- ❌ MENOS SEMÁNTICO -->
<p>Esto es <b>muy importante</b> y merece <i>atención</i>.</p>
```

**Mejor práctica**:
```html
<!-- ✅ MÁS SEMÁNTICO -->
<p>Esto es <strong>muy importante</strong> y merece <em>atención</em>.</p>
```

**Nota**: `<strong>` y `<em>` indican importancia semántica, mientras que `<b>` e `<i>` son solo visuales.

### Error 3: Jerarquía incorrecta de encabezados
```html
<!-- ❌ INCORRECTO -->
<h1>Título Principal</h1>
<h4>Primera Sección</h4>  <!-- ¡Saltamos h2 y h3! -->
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<h1>Título Principal</h1>
<h2>Primera Sección</h2>
<h3>Subsección (si es necesario)</h3>
```

### Error 4: Texto sin etiquetas en el body
```html
<!-- ❌ INCORRECTO -->
<body>
    Este es mi texto sin etiquetas
</body>
```

**Solución**: Todo texto debe estar en etiquetas:
```html
<!-- ✅ CORRECTO -->
<body>
    <p>Este es mi texto correctamente etiquetado</p>
</body>
```

## 🔗 Recursos Adicionales

- [Typography Cheatsheet](https://www.typewolf.com/cheatsheet)
- [HTML Entity Reference](https://dev.w3.org/html5/html-author/charref)
- [Semántica de texto - MDN](https://developer.mozilla.org/es/docs/Web/HTML/Element#sem%C3%A1ntica_de_texto_en_l%C3%ADnea)

## 📸 Ejemplo de Estructura

```
Título Principal del Artículo
Por Juan Pérez • 28 de noviembre, 2025
─────────────────────────────────

Introducción al tema del artículo...

Primera Sección
Contenido de la primera sección con palabras importantes
y énfasis en ciertos conceptos clave.

Segunda Sección
Más contenido desarrollando el tema...

Tercera Sección
Continuación del artículo con información resaltada.

Conclusión
Resumen y cierre del artículo.

─────────────────────────────────
Nota: Este es un artículo de ejemplo para practicar HTML.
```

## 💡 Tips de Escritura

- **Strong**: Usa para conceptos clave, advertencias, términos importantes
- **Em**: Usa para énfasis sutil, palabras en otro idioma, títulos de obras
- **Mark**: Usa para resaltar texto relevante al contexto actual
- **Small**: Usa para letra pequeña legal, copyright, notas aclaratorias

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 04 - Listas y Enlaces](./ejercicio-04-listas-y-enlaces.md)
