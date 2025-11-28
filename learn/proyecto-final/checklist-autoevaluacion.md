# ✅ Checklist de Autoevaluación

Marca cada ítem al completarlo. Asegúrate de cumplir TODOS antes de considerar el proyecto terminado.

## 📄 HTML (Estructura)

### Estructura Básica
- [ ] El HTML tiene DOCTYPE, html, head, body
- [ ] Meta viewport está presente
- [ ] Título descriptivo en `<title>`
- [ ] Charset UTF-8 declarado

### Semántica
- [ ] Uso de `<header>`, `<main>`, `<footer>`
- [ ] Uso de `<section>` para secciones
- [ ] Encabezados jerárquicos (h1, h2, h3)
- [ ] Solo UN `<h1>` por página
- [ ] No hay `<div>` donde debería haber etiquetas semánticas

### Hero Section
- [ ] Título principal presente (`<h1>`)
- [ ] Subtítulo o descripción
- [ ] Botón Call-to-Action
- [ ] Imagen o background visual

### Formulario
- [ ] Campos: Nombre, Email, Mensaje
- [ ] Todos los campos tienen `<label>` asociado
- [ ] Atributo `type` correcto en inputs
- [ ] Botón de submit presente

### Footer
- [ ] Información de copyright
- [ ] Al menos un dato de contacto o enlaces

### Accesibilidad
- [ ] Todas las imágenes tienen atributo `alt`
- [ ] Los enlaces tienen texto descriptivo
- [ ] Contraste de colores adecuado

---

## 🎨 CSS (Diseño)

### Organización
- [ ] CSS en archivo externo (`css/styles.css`)
- [ ] Secciones del CSS están comentadas
- [ ] Uso de variables CSS para colores y espaciados
- [ ] Reset básico o normalize aplicado

### Layout
- [ ] Uso de Flexbox o Grid
- [ ] Elementos correctamente alineados
- [ ] Espaciados consistentes
- [ ] Sin elementos superpuestos inesperadamente

### Responsive Design
- [ ] Enfoque Mobile-First (estilos base para móvil)
- [ ] Media query para tablet (≥ 768px)
- [ ] Media query para desktop (≥ 1024px)
- [ ] Ningún scroll horizontal en ningún tamaño
- [ ] Imágenes responsive (`max-width: 100%`)
- [ ] Texto legible en todos los tamaños

### Visual
- [ ] Paleta de colores coherente (2-4 colores principales)
- [ ] Tipografía legible (tamaño mínimo 16px)
- [ ] Contraste adecuado entre texto y fondo
- [ ] Espaciado visual agradable (no amontonado)

### Animaciones
- [ ] Al menos 3 transitions o animations
- [ ] Animaciones suaves (no bruscas)
- [ ] Hover states en elementos interactivos
- [ ] Las animaciones mejoran la UX (no molestan)

---

## 💻 JavaScript (Interactividad)

### Formulario - Validación
- [ ] El formulario no se envía si hay errores
- [ ] Validación de campo Nombre (no vacío, min 3 caracteres)
- [ ] Validación de Email (formato válido con regex)
- [ ] Validación de Mensaje (no vacío, min 10 caracteres)
- [ ] Mensajes de error específicos y claros
- [ ] Los errores se limpian al corregir

### Formulario - UX
- [ ] Mensaje de éxito al enviar correctamente
- [ ] El formulario se limpia después de envío exitoso
- [ ] Feedback visual (colores, íconos)
- [ ] No hay refresco de página al submit

### Interactividad Adicional
- [ ] Al menos 2 interacciones más allá del formulario
- [ ] Ejemplos: smooth scroll, menú móvil, contador, modal, etc.
- [ ] Las interacciones funcionan sin errores

### Código Limpio
- [ ] Código JavaScript en archivo externo (`js/script.js`)
- [ ] Variables con nombres descriptivos
- [ ] Funciones con un propósito claro
- [ ] Comentarios explicativos en lógica compleja
- [ ] Sin errores en la consola del navegador

---

## 🗂️ Organización y Mejores Prácticas

### Estructura de Archivos
- [ ] Carpeta `css/` con archivos CSS
- [ ] Carpeta `js/` con archivos JavaScript
- [ ] Carpeta `images/` con imágenes
- [ ] Nombres de archivos en minúsculas con guiones

### README.md
- [ ] Descripción del proyecto
- [ ] Tecnologías usadas
- [ ] Screenshots o link al sitio
- [ ] Instrucciones para ver el proyecto

### Git
- [ ] Proyecto inicializado con Git
- [ ] Commits con mensajes descriptivos
- [ ] Repositorio subido a GitHub
- [ ] .gitignore presente (si aplica)

### Código Limpio
- [ ] Código correctamente indentado
- [ ] Sin código comentado innecesario
- [ ] Sin console.logs de debugging olvidados
- [ ] Nombres de clases descriptivos
- [ ] Consistencia en naming (camelCase JS, kebab-case CSS)

---

## 🧪 Testing

### Funcionalidad
- [ ] Todos los links funcionan (o son # si es demo)
- [ ] El formulario envía correctamente
- [ ] Las validaciones funcionan como esperado
- [ ] No hay errores JavaScript en console

### Navegadores
- [ ] Probado en Chrome
- [ ] Probado en Firefox (opcional pero recomendado)
- [ ] Probado en Safari (si tienes Mac)

### Dispositivos
- [ ] Probado en móvil real o DevTools mobile
- [ ] Probado redimensionando la ventana
- [ ] Todas las secciones son visibles y usables

### Performance
- [ ] Imágenes optimizadas (< 500KB cada una)
- [ ] Sin CSS/JS bloqueante innecesario
- [ ] Carga en < 3 segundos (en conexión normal)

---

## 🎯 Criterios de Excelencia (Opcional)

Si quieres ir más allá:
- [ ] Animaciones CSS avanzadas (@keyframes)
- [ ] Uso de SVG inline con animación
- [ ] Implementación de lazy loading en imágenes
- [ ] Accesibilidad avanzada (ARIA labels)
- [ ] Lighthouse score > 90
- [ ] Deployment en GitHub Pages o Netlify

---

## 📊 Resumen Final

**Total de ítems obligatorios**: ~90

**Mínimo para aprobar**: 80% (72 ítems)
**Bueno**: 90% (81 ítems)
**Excelente**: 100% (90+ ítems)

### Tu puntuación: _____ / 90

---

**¿Completaste todos los ítems obligatorios?**
✅ **SÍ** - ¡Felicidades! Estás listo para entregar
❌ **NO** - Revisa los ítems faltantes y completa el proyecto

---

### 👉 Volver a: [README del Proyecto](./README.md)
