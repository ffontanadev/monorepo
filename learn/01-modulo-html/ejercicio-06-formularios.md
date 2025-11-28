# Ejercicio 06 - Formularios

## 🎯 Objetivo
Aprender a crear formularios HTML funcionales con diferentes tipos de inputs y entender las mejores prácticas de accesibilidad.

## 📚 Conceptos Clave
- Elemento `<form>` y sus atributos
- Tipos de input: text, email, tel, number, date, etc.
- Elementos: `<textarea>`, `<select>`, `<button>`
- Etiquetas `<label>` y su importancia
- Atributos: `required`, `placeholder`, `name`, `id`
- Validación HTML5 nativa

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Formularios web](https://developer.mozilla.org/es/docs/Learn/Forms)
- [MDN - Elemento form](https://developer.mozilla.org/es/docs/Web/HTML/Element/form)
- [W3Schools - HTML Forms](https://www.w3schools.com/html/html_forms.asp)

### Preguntas para investigar:
1. ¿Para qué sirve el atributo `action` en un formulario?
2. ¿Cuál es la diferencia entre `method="GET"` y `method="POST"`?
3. ¿Por qué cada input debe tener un `<label>` asociado?
4. ¿Qué tipos de input existen en HTML5?
5. ¿Cómo funciona la validación nativa de HTML5?

## 💻 Tarea

Crea un archivo llamado `formulario-contacto.html` que contenga un formulario de contacto completo y funcional.

### El formulario debe incluir:

1. **Información Personal**:
   - Nombre completo (input text, required)
   - Email (input email, required)
   - Teléfono (input tel, opcional)
   - Fecha de nacimiento (input date)

2. **Mensaje**:
   - Asunto (select con 3-4 opciones)
   - Mensaje (textarea, required, mínimo 10 caracteres)

3. **Preferencias**:
   - Cómo te enteraste de nosotros (radio buttons con 3 opciones)
   - Intereses (checkboxes con al menos 3 opciones)

4. **Botones**:
   - Botón de envío (submit)
   - Botón de limpiar formulario (reset)

### Requisitos Específicos:
- Cada input debe tener su `<label>` asociado
- Usa el atributo `for` en labels y `id` en inputs
- Al menos 3 campos deben ser `required`
- Usa `placeholder` en campos de texto
- Agrupa campos relacionados con `<fieldset>` y `<legend>`
- El formulario debe tener `action="#"` y `method="post"`

## ✅ Checklist de Autoevaluación

### Estructura del Formulario:
- [ ] El formulario usa la etiqueta `<form>`
- [ ] Tiene atributos `action` y `method`
- [ ] Todos los campos están dentro del `<form>`

### Labels y Accesibilidad:
- [ ] Cada input tiene su `<label>` correspondiente
- [ ] Los labels usan el atributo `for` que coincide con el `id` del input
- [ ] Los labels tienen texto descriptivo
- [ ] Al hacer clic en el label, el input se enfoca

### Tipos de Input:
- [ ] Hay un input de tipo `text`
- [ ] Hay un input de tipo `email`
- [ ] Hay un input de tipo `tel` o `number`
- [ ] Hay un input de tipo `date`
- [ ] Cada input tiene el atributo `name`

### Elementos Adicionales:
- [ ] Hay un `<textarea>` para el mensaje
- [ ] Hay un `<select>` con al menos 3 `<option>`
- [ ] Hay radio buttons (mismo `name`, diferentes `value`)
- [ ] Hay checkboxes (diferentes `name` o mismo con `[]`)

### Validación:
- [ ] Al menos 3 campos tienen `required`
- [ ] El input de email valida formato de correo
- [ ] El textarea tiene `minlength` o `maxlength`
- [ ] Los placeholders son útiles y descriptivos

### Agrupación:
- [ ] Se usa al menos un `<fieldset>`
- [ ] Cada fieldset tiene su `<legend>`
- [ ] La agrupación es lógica

### Botones:
- [ ] Hay un botón de tipo `submit`
- [ ] Hay un botón de tipo `reset` (opcional pero recomendado)
- [ ] Los botones tienen texto descriptivo

### Código Limpio:
- [ ] El código está correctamente indentado
- [ ] Todos los atributos están entre comillas
- [ ] Los `id` son únicos en toda la página
- [ ] Los `name` son descriptivos

### Funcionalidad:
- [ ] Al hacer submit sin completar campos requeridos, muestra errores
- [ ] La validación de email funciona
- [ ] Los radio buttons permiten seleccionar solo una opción
- [ ] Los checkboxes permiten múltiples selecciones
- [ ] El botón reset limpia todo el formulario

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Agrega un input de tipo `file` para adjuntar archivos
- Usa `pattern` para validación personalizada
- Agrega un input de tipo `range` (slider)
- Usa `<datalist>` para autocompletado
- Agrega un campo de tipo `color`
- Implementa un campo de contraseña con requisitos

## 🐛 Errores Comunes

### Error 1: Label no asociado correctamente
```html
<!-- ❌ INCORRECTO -->
<label>Nombre:</label>
<input type="text" name="nombre">
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<label for="nombre">Nombre:</label>
<input type="text" id="nombre" name="nombre">
```

### Error 2: Radio buttons sin el mismo `name`
```html
<!-- ❌ INCORRECTO (permite seleccionar múltiples) -->
<input type="radio" name="opcion1" value="si"> Sí
<input type="radio" name="opcion2" value="no"> No
```

**Solución**:
```html
<!-- ✅ CORRECTO (solo uno seleccionable) -->
<input type="radio" id="si" name="respuesta" value="si">
<label for="si">Sí</label>

<input type="radio" id="no" name="respuesta" value="no">
<label for="no">No</label>
```

### Error 3: Botón que envía cuando no debería
```html
<!-- ❌ Este botón enviará el formulario -->
<form>
    <button>Cancelar</button>
</form>
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<form>
    <button type="button">Cancelar</button>
    <button type="submit">Enviar</button>
</form>
```

### Error 4: Textarea mal formado
```html
<!-- ❌ INCORRECTO -->
<textarea value="Texto aquí" />
```

**Solución**:
```html
<!-- ✅ CORRECTO -->
<textarea name="mensaje" rows="5" cols="30">Texto predeterminado aquí</textarea>
```

## 🔗 Recursos Adicionales

- [MDN - Validación de formularios](https://developer.mozilla.org/es/docs/Learn/Forms/Form_validation)
- [MDN - Tipos de input](https://developer.mozilla.org/es/docs/Web/HTML/Element/input#input_types)
- [Can I Use - Input types](https://caniuse.com/?search=input%20type)
- [Web Forms - The guide](https://web.dev/learn/forms/)

## 📝 Plantilla de Referencia

```html
<form action="#" method="post">
    <fieldset>
        <legend>Información Personal</legend>

        <label for="nombre">Nombre completo:</label>
        <input type="text" id="nombre" name="nombre" required>

        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
    </fieldset>

    <fieldset>
        <legend>Mensaje</legend>

        <label for="mensaje">Tu mensaje:</label>
        <textarea id="mensaje" name="mensaje" rows="5" required></textarea>
    </fieldset>

    <button type="submit">Enviar</button>
    <button type="reset">Limpiar</button>
</form>
```

## 📊 Tipos de Input Más Comunes

| Tipo | Uso | Validación Nativa |
|------|-----|-------------------|
| `text` | Texto general | Ninguna |
| `email` | Correos electrónicos | Formato email |
| `tel` | Teléfonos | Ninguna (usar pattern) |
| `number` | Números | Solo números |
| `date` | Fechas | Formato fecha |
| `password` | Contraseñas | Ninguna (oculta texto) |
| `url` | URLs | Formato URL |
| `search` | Búsquedas | Ninguna |

## 💡 Tips de UX

1. **Placeholder vs Label**: Nunca uses placeholder como reemplazo del label
2. **Required**: Indica visualmente qué campos son requeridos (ej: asterisco)
3. **Orden lógico**: Nombre → Email → Teléfono → Mensaje
4. **Mensajes de error**: HTML5 los muestra automáticamente
5. **Botón submit**: Usa texto de acción ("Enviar Mensaje", no solo "Enviar")

## 🎨 Estructura Visual Esperada

```
Formulario de Contacto
━━━━━━━━━━━━━━━━━━━━━━━━━

Información Personal
┌─────────────────────────┐
│ Nombre completo:        │
│ [________________]      │
│                         │
│ Email:                  │
│ [________________]      │
│                         │
│ Teléfono:               │
│ [________________]      │
└─────────────────────────┘

Mensaje
┌─────────────────────────┐
│ Asunto:                 │
│ [Seleccionar ▼]         │
│                         │
│ Mensaje:                │
│ [                    ]  │
│ [                    ]  │
│ [                    ]  │
└─────────────────────────┘

Preferencias
○ Opción 1  ○ Opción 2

☐ Interés 1  ☐ Interés 2

[Enviar]  [Limpiar]
```

## 🧪 Cómo Probar Tu Formulario

1. **Prueba de validación**:
   - Intenta enviar el formulario vacío
   - Intenta enviar un email inválido
   - Verifica que los campos required funcionen

2. **Prueba de accesibilidad**:
   - Navega solo con teclado (Tab, Shift+Tab)
   - Haz clic en los labels (¿enfoca el input?)
   - Usa un lector de pantalla si es posible

3. **Prueba de UX**:
   - ¿El orden de campos tiene sentido?
   - ¿Los placeholders ayudan?
   - ¿Los mensajes de error son claros?

---

### 🎯 ¡Felicidades! Has completado el Módulo 1 de HTML

### 👉 Continúa con: [Módulo 2: CSS - Diseño y Presentación](../02-modulo-css/README.md)
