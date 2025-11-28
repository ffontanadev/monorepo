# Ejercicio 01 - Mi Primera Página

## 🎯 Objetivo
Crear tu primera página HTML válida entendiendo la estructura básica que todo documento web debe tener.

## 📚 Conceptos Clave
- Estructura básica de un documento HTML5
- Etiquetas `<!DOCTYPE html>`, `<html>`, `<head>`, `<body>`
- Meta etiquetas esenciales: `charset`, `viewport`
- Elemento `<title>`

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Estructura básica de HTML](https://developer.mozilla.org/es/docs/Learn/Getting_started_with_the_web/HTML_basics)
- [MDN - Elemento head](https://developer.mozilla.org/es/docs/Learn/HTML/Introduction_to_HTML/The_head_metadata_in_HTML)
- [W3Schools - HTML Introduction](https://www.w3schools.com/html/html_intro.asp)

### Preguntas para investigar:
1. ¿Qué significa DOCTYPE y por qué es importante?
2. ¿Cuál es la diferencia entre `<head>` y `<body>`?
3. ¿Por qué es importante especificar `charset="UTF-8"`?
4. ¿Qué hace la meta etiqueta `viewport`?

## 💻 Tarea

Crea un archivo llamado `index.html` que contenga:

1. **Declaración DOCTYPE** de HTML5
2. **Etiqueta html** con el atributo `lang="es"`
3. **Sección head** con:
   - Meta charset UTF-8
   - Meta viewport para dispositivos móviles
   - Un título descriptivo
4. **Sección body** con:
   - Un encabezado `<h1>` que diga "Mi Primera Página Web"
   - Un párrafo `<p>` de bienvenida (escribe lo que quieras)
   - Otro párrafo contando por qué quieres aprender desarrollo web

### Requisitos Específicos:
- El archivo debe llamarse exactamente `index.html`
- Usa minúsculas en todos los nombres de etiquetas
- Indenta correctamente el código (2 o 4 espacios)
- El título debe ser descriptivo y único

## ✅ Checklist de Autoevaluación

### Estructura HTML:
- [ ] El documento tiene `<!DOCTYPE html>` en la primera línea
- [ ] La etiqueta `<html>` tiene el atributo `lang="es"`
- [ ] Existe una sección `<head>` completa
- [ ] Existe una sección `<body>` completa
- [ ] El `<head>` contiene `<meta charset="UTF-8">`
- [ ] El `<head>` contiene la meta viewport
- [ ] El `<head>` contiene un `<title>` descriptivo

### Contenido del Body:
- [ ] Hay un `<h1>` con el texto "Mi Primera Página Web"
- [ ] Hay al menos dos párrafos `<p>` con contenido
- [ ] Todo el contenido visible está dentro del `<body>`

### Código Limpio:
- [ ] El código está correctamente indentado
- [ ] Todas las etiquetas están en minúsculas
- [ ] Todas las etiquetas de apertura tienen su cierre correspondiente
- [ ] No hay espacios extras o líneas en blanco innecesarias

### Validación:
- [ ] El archivo se abre correctamente en el navegador
- [ ] El título aparece en la pestaña del navegador
- [ ] El contenido se visualiza correctamente
- [ ] No hay errores en la consola del navegador (F12 → Console)

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Agrega una meta descripción: `<meta name="description" content="Mi primera página web">`
- Agrega un favicon (ícono en la pestaña)
- Experimenta con otros elementos de texto: `<strong>`, `<em>`, `<mark>`

## 🐛 Errores Comunes

### Error 1: Página en blanco
```html
<!-- ❌ INCORRECTO -->
<!DOCTYPE html>
<html>
<head>
    <title>Mi Página</title>
    <h1>Hola</h1>  <!-- ¡Esto está en el lugar equivocado! -->
</head>
<body>
</body>
</html>
```

**Solución**: Todo el contenido visible debe ir dentro de `<body>`, no en `<head>`.

### Error 2: Caracteres extraños (tildes, ñ)
```html
<!-- ❌ INCORRECTO -->
<head>
    <title>Mi Página</title>
    <!-- Falta el charset -->
</head>
```

**Solución**: Siempre incluye `<meta charset="UTF-8">` en el `<head>`.

### Error 3: Etiquetas sin cerrar
```html
<!-- ❌ INCORRECTO -->
<body>
    <h1>Mi Título
    <p>Mi párrafo
</body>
```

**Solución**: Asegúrate de cerrar todas las etiquetas:
```html
<!-- ✅ CORRECTO -->
<body>
    <h1>Mi Título</h1>
    <p>Mi párrafo</p>
</body>
```

## 🔗 Recursos Adicionales

- [HTML Validator](https://validator.w3.org/) - Valida tu código
- [CodePen](https://codepen.io/pen/) - Prueba código en el navegador
- [VS Code Live Server](https://marketplace.visualstudio.com/items?itemName=ritwickdey.LiveServer) - Extensión para previsualizar

## 📸 Ejemplo de Resultado Esperado

Al abrir tu `index.html` en el navegador deberías ver:

```
Mi Primera Página Web
Mi párrafo de bienvenida aquí.

Otro párrafo explicando por qué quiero aprender desarrollo web.
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 02 - Semántica Web](./ejercicio-02-semantica-web.md)
