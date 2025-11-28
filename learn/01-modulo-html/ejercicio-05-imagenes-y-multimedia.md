# Ejercicio 05 - Imágenes y Multimedia

## 🎯 Objetivo
Aprender a insertar y optimizar imágenes en páginas web, entendiendo la importancia de la accesibilidad y el rendimiento.

## 📚 Conceptos Clave
- Etiqueta `<img>` y sus atributos esenciales
- Atributo `alt` para accesibilidad
- Atributos `width` y `height`
- Formatos de imagen: JPG, PNG, SVG, WebP
- Rutas relativas vs absolutas
- Elemento `<figure>` y `<figcaption>`

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Imágenes en HTML](https://developer.mozilla.org/es/docs/Learn/HTML/Multimedia_and_embedding/Images_in_HTML)
- [MDN - Elemento img](https://developer.mozilla.org/es/docs/Web/HTML/Element/img)
- [Web.dev - Optimización de imágenes](https://web.dev/fast/#optimize-your-images)

### Preguntas para investigar:
1. ¿Por qué es importante el atributo `alt`?
2. ¿Cuál es la diferencia entre JPG, PNG y SVG?
3. ¿Qué son las rutas relativas y absolutas?
4. ¿Cuándo usar `<figure>` en lugar de solo `<img>`?
5. ¿Qué pasa si omites `width` y `height`?

## 💻 Tarea

Crea un archivo llamado `galeria.html` que contenga una galería de imágenes sobre un tema de tu elección (naturaleza, tecnología, arte, deportes, etc.).

### La página debe incluir:

1. **Header** con título de la galería

2. **Sección "Hero"**:
   - Una imagen grande principal
   - Usa `<figure>` y `<figcaption>`
   - La imagen debe tener `alt` descriptivo

3. **Sección "Galería Principal"**:
   - 6 imágenes organizadas
   - Cada imagen con su descripción usando `<figure>` y `<figcaption>`
   - Todas las imágenes con atributos `width`, `height`, y `alt`

4. **Sección "Tipos de Imágenes"**:
   - Al menos un ejemplo de imagen JPG/PNG
   - Al menos un ejemplo de SVG (puede ser un ícono simple)
   - Explicación breve de cuándo usar cada formato

5. **Footer** con información sobre el origen de las imágenes

### Requisitos Específicos:
- Crea una carpeta `/images/` junto a tu HTML
- Descarga o crea al menos 6 imágenes
- Usa rutas relativas (ej: `images/foto1.jpg`)
- Todas las imágenes deben tener `alt` descriptivo
- Al menos 3 imágenes deben usar `<figure>` y `<figcaption>`
- Especifica `width` y `height` en al menos 4 imágenes

## ✅ Checklist de Autoevaluación

### Estructura de Archivos:
- [ ] Existe una carpeta `/images/` en el mismo nivel que el HTML
- [ ] Las imágenes están guardadas en `/images/`
- [ ] Los nombres de archivo no tienen espacios ni caracteres especiales
- [ ] Los nombres son descriptivos (ej: `paisaje-montaña.jpg`)

### Uso de `<img>`:
- [ ] Hay al menos 6 elementos `<img>` en la página
- [ ] Todas las imágenes tienen el atributo `src`
- [ ] Todas las imágenes tienen el atributo `alt`
- [ ] El texto `alt` es descriptivo (no "imagen1.jpg")
- [ ] Al menos 4 imágenes tienen `width` y `height`

### Uso de `<figure>`:
- [ ] Hay al menos 3 elementos `<figure>`
- [ ] Cada `<figure>` contiene un `<img>`
- [ ] Cada `<figure>` tiene su `<figcaption>`
- [ ] El `<figcaption>` proporciona contexto útil

### Rutas de Imágenes:
- [ ] Se usan rutas relativas (no absolutas de tu computadora)
- [ ] Las rutas funcionan correctamente
- [ ] Todas las imágenes se cargan sin errores

### Accesibilidad:
- [ ] Ningún `alt` está vacío (a menos que sea decorativo)
- [ ] Los textos `alt` describen el contenido, no "imagen de..."
- [ ] Las imágenes decorativas tienen `alt=""` (vacío)

### Formatos:
- [ ] Hay al menos una imagen JPG o PNG
- [ ] Hay al menos un SVG
- [ ] Los formatos son apropiados para su uso

### Código Limpio:
- [ ] El código está correctamente indentado
- [ ] Los atributos están entre comillas
- [ ] Las etiquetas están en minúsculas
- [ ] No hay código duplicado innecesariamente

### Funcionalidad:
- [ ] Todas las imágenes se visualizan correctamente
- [ ] No hay imágenes rotas (icono de "imagen no encontrada")
- [ ] Las dimensiones de las imágenes son apropiadas
- [ ] La página carga rápidamente

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa el elemento `<picture>` para responsive images
- Implementa `srcset` para diferentes densidades de pantalla
- Agrega `loading="lazy"` para carga diferida
- Usa el formato WebP para mejor compresión
- Agrega un `<svg>` inline (no como archivo externo)

## 🐛 Errores Comunes

### Error 1: Ruta absoluta en lugar de relativa
```html
<!-- ❌ INCORRECTO (específico de tu computadora) -->
<img src="C:/Users/MiUsuario/Desktop/proyecto/images/foto.jpg" alt="Foto">
```

**Solución**:
```html
<!-- ✅ CORRECTO (ruta relativa) -->
<img src="images/foto.jpg" alt="Descripción de la foto">
```

### Error 2: Alt vacío o genérico
```html
<!-- ❌ MAL PARA ACCESIBILIDAD -->
<img src="images/paisaje.jpg" alt="">
<img src="images/paisaje.jpg" alt="imagen">
```

**Solución**:
```html
<!-- ✅ DESCRIPTIVO -->
<img src="images/paisaje.jpg" alt="Montañas nevadas al atardecer con cielo naranja">
```

### Error 3: Imagen sin dimensiones especificadas
```html
<!-- ❌ PUEDE CAUSAR LAYOUT SHIFT -->
<img src="images/foto.jpg" alt="Foto">
```

**Mejor práctica**:
```html
<!-- ✅ PREVIENE LAYOUT SHIFT -->
<img src="images/foto.jpg" alt="Foto" width="800" height="600">
```

### Error 4: Figure sin figcaption
```html
<!-- ❌ INCOMPLETO -->
<figure>
    <img src="images/foto.jpg" alt="Foto">
</figure>
```

**Solución**:
```html
<!-- ✅ COMPLETO -->
<figure>
    <img src="images/foto.jpg" alt="Atardecer en la playa">
    <figcaption>Atardecer capturado en la Playa del Carmen, México</figcaption>
</figure>
```

## 🔗 Recursos Adicionales

### Bancos de Imágenes Gratuitas:
- [Unsplash](https://unsplash.com/) - Fotos de alta calidad
- [Pexels](https://pexels.com/) - Fotos y videos gratis
- [Pixabay](https://pixabay.com/) - Imágenes libres de derechos

### Herramientas:
- [TinyPNG](https://tinypng.com/) - Comprime imágenes PNG/JPG
- [SVGOMG](https://jakearchibald.github.io/svgomg/) - Optimiza SVG
- [Squoosh](https://squoosh.app/) - Comprime y convierte imágenes

### Íconos SVG Gratuitos:
- [Font Awesome](https://fontawesome.com/)
- [Heroicons](https://heroicons.com/)
- [Feather Icons](https://feathericons.com/)

## 📸 Guía de Formatos

| Formato | Cuándo Usarlo | Ventajas | Desventajas |
|---------|---------------|----------|-------------|
| **JPG** | Fotos, imágenes con muchos colores | Buen tamaño de archivo | Pierde calidad al comprimir |
| **PNG** | Logos, imágenes con transparencia | Sin pérdida de calidad | Archivos más grandes |
| **SVG** | Íconos, logos, gráficos simples | Escalable sin pérdida | No bueno para fotos |
| **WebP** | Alternativa moderna a JPG/PNG | Mejor compresión | Menor soporte en navegadores viejos |

## 💡 Tips de Accesibilidad

### Buenos textos `alt`:
```html
<!-- ✅ EXCELENTE -->
<img src="perro.jpg" alt="Golden Retriever jugando con pelota en el parque">

<!-- ✅ BUENO -->
<img src="logo.png" alt="Logo de Empresa XYZ">

<!-- ❌ MALO -->
<img src="img1.jpg" alt="Imagen">
<img src="photo.jpg" alt="Foto de un perro">
```

### Imágenes decorativas:
```html
<!-- Para imágenes puramente decorativas, usa alt vacío -->
<img src="decoracion.png" alt="">
```

## 📐 Estructura de Carpetas Recomendada

```
mi-proyecto/
├── galeria.html
└── images/
    ├── hero-principal.jpg
    ├── galeria-1.jpg
    ├── galeria-2.jpg
    ├── galeria-3.jpg
    ├── galeria-4.jpg
    ├── galeria-5.jpg
    ├── galeria-6.jpg
    └── icono.svg
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 06 - Formularios](./ejercicio-06-formularios.md)
