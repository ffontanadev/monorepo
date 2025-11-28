# Guía 04 - Mejores Prácticas

## 🎯 Objetivo
Escribir código limpio, mantenible y profesional.

## 📁 1. Organización de Archivos

### Estructura Recomendada
```
mi-proyecto/
├── index.html
├── css/
│   ├── reset.css (opcional)
│   └── styles.css
├── js/
│   └── script.js
├── images/
│   ├── logo.svg
│   └── hero.jpg
├── README.md
└── .gitignore
```

## 🏷️ 2. Naming Conventions (Nombres)

### HTML/CSS
```html
<!-- ✅ BUENO: kebab-case -->
<div class="hero-section">
<button class="btn-primary">

<!-- ❌ MALO -->
<div class="HeroSection">
<div class="hero_section">
```

### JavaScript
```javascript
// ✅ BUENO: camelCase
const userName = "Juan";
function calculateTotal() {}

// ❌ MALO
const user_name = "Juan";
const UserName = "Juan";
```

### Archivos
```
✅ BUENO:
- index.html
- main-styles.css
- contact-form.js

❌ MALO:
- Index.html
- Main Styles.css (espacios)
- contactForm.js (inconsistente)
```

## 💬 3. Comentarios Útiles

### HTML
```html
<!-- Hero Section -->
<section class="hero">
    <!-- Main Call to Action -->
    <button>Get Started</button>
</section>
```

### CSS
```css
/* ========== HEADER ========== */
.header {
    /* ... */
}

/* ========== HERO SECTION ========== */
.hero {
    /* ... */
}

/* Mobile responsive */
@media (max-width: 768px) {
    /* ... */
}
```

### JavaScript
```javascript
// Calculate total with tax
function calculateTotal(subtotal, taxRate) {
    const tax = subtotal * taxRate;
    return subtotal + tax;
}

// TODO: Add validation for negative numbers
// FIXME: This breaks on mobile Safari
```

## ✅ 4. Código Limpio

### HTML
```html
<!-- ✅ BUENO: Indentado, semántico -->
<main>
    <section class="hero">
        <h1>Título</h1>
        <p>Descripción</p>
    </section>
</main>

<!-- ❌ MALO: Sin indentar, no semántico -->
<div>
<div>
<div>Título</div>
<div>Descripción</div>
</div>
</div>
```

### CSS
```css
/* ✅ BUENO: Organizado, agrupado */
.button {
    /* Display */
    display: inline-block;
    
    /* Box Model */
    padding: 1rem 2rem;
    margin: 0.5rem;
    
    /* Visual */
    background: blue;
    color: white;
    border-radius: 4px;
    
    /* Typography */
    font-size: 1rem;
    font-weight: bold;
}

/* ❌ MALO: Desordenado */
.button {
    color: white;
    padding: 1rem 2rem;
    font-size: 1rem;
    background: blue;
    display: inline-block;
}
```

### JavaScript
```javascript
// ✅ BUENO: Constantes en MAYÚSCULAS, nombres descriptivos
const MAX_RETRIES = 3;
const API_BASE_URL = "https://api.example.com";

function getUserFullName(user) {
    return `${user.firstName} ${user.lastName}`;
}

// ❌ MALO: Nombres crípticos
const x = 3;
const url = "https://api.example.com";

function gufn(u) {
    return u.fn + " " + u.ln;
}
```

## 🎨 5. Consistencia

### Usa un Style Guide
- **Prettier**: Formateador automático
- **ESLint**: Linter para JavaScript
- **Configuración VS Code**: Settings sync

### Decisiones consistentes:
- ¿Comillas simples o dobles? Elige una
- ¿Punto y coma o no? Elige una
- ¿2 espacios o 4 para indentación? Elige una

## ✅ Checklist de Mejores Prácticas

### Organización
- [ ] Archivos organizados en carpetas lógicas
- [ ] Nombres de archivos en minúsculas con guiones

### Nombres
- [ ] Classes CSS en kebab-case
- [ ] Variables JS en camelCase
- [ ] Nombres descriptivos (no `x`, `data`, `temp`)

### Comentarios
- [ ] Comentas secciones principales
- [ ] Evitas comentarios obvios
- [ ] Usas TODO/FIXME cuando corresponde

### Formato
- [ ] Código correctamente indentado
- [ ] Consistencia en comillas y punto y coma
- [ ] Espacios en blanco para legibilidad

### Mantenibilidad
- [ ] Código DRY (Don't Repeat Yourself)
- [ ] Funciones pequeñas y específicas
- [ ] Magic numbers como constantes

---

### 🎯 ¡Módulo 4 Completado!
### 👉 Ahora estás listo para: [Proyecto Final](../proyecto-final/README.md)
