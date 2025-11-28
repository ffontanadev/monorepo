# 💻 Módulo 3: JavaScript - Interactividad

**Duración**: Semanas 6-8
**Ejercicios**: 7 prácticos
**Tiempo estimado**: 24-30 horas

## 🎯 Objetivos del Módulo

Al completar este módulo serás capaz de:
- ✅ Entender la sintaxis básica de JavaScript
- ✅ Trabajar con variables, tipos de datos y operadores
- ✅ Crear y usar funciones
- ✅ Controlar el flujo con condicionales y loops
- ✅ Seleccionar y manipular elementos del DOM
- ✅ Responder a eventos del usuario
- ✅ Validar formularios con JavaScript

## 📚 ¿Qué es JavaScript?

**JavaScript** es el lenguaje de programación que da **interactividad** a las páginas web. Si HTML es el esqueleto y CSS la apariencia, JavaScript es el **comportamiento**.

JavaScript te permite:
- 🎮 **Responder** a acciones del usuario (clicks, typing, scroll)
- 🔄 **Modificar** el contenido y estilos dinámicamente
- ✅ **Validar** formularios antes de enviarlos
- 📡 **Comunicarse** con servidores (APIs)
- 🎨 **Crear** animaciones y efectos complejos

## 🗂️ Ejercicios del Módulo

### Semana 6: Fundamentos de JavaScript

#### [Ejercicio 14 - Variables y Tipos](./ejercicio-14-variables-y-tipos.md)
Aprende sobre variables, tipos de datos y operadores básicos.
- Conceptos: `let`, `const`, strings, numbers, booleans, arrays, objects
- Tiempo: 2-3 horas

#### [Ejercicio 15 - Funciones](./ejercicio-15-funciones.md)
Crea funciones reutilizables con parámetros y valores de retorno.
- Conceptos: function, arrow functions, parámetros, return, scope
- Tiempo: 2-3 horas

#### [Ejercicio 16 - Condicionales y Loops](./ejercicio-16-condicionales-y-loops.md)
Controla el flujo del programa con if/else y loops.
- Conceptos: if/else, switch, for, while, forEach
- Tiempo: 3-4 horas

---

### Semana 7: DOM Manipulation

#### [Ejercicio 17 - DOM Selección](./ejercicio-17-dom-seleccion.md)
Aprende a seleccionar elementos del HTML desde JavaScript.
- Conceptos: querySelector, getElementById, getElementsByClassName
- Tiempo: 2-3 horas

#### [Ejercicio 18 - DOM Manipulación](./ejercicio-18-dom-manipulacion.md)
Modifica el contenido, estilos y estructura del DOM.
- Conceptos: innerHTML, textContent, classList, createElement, append
- Tiempo: 3-4 horas

#### [Ejercicio 19 - Eventos](./ejercicio-19-eventos.md)
Responde a interacciones del usuario con event listeners.
- Conceptos: addEventListener, click, submit, input, event object
- Tiempo: 3-4 horas

---

### Semana 8: Aplicación Práctica

#### [Ejercicio 20 - Validación de Formularios](./ejercicio-20-validacion-formularios.md)
Crea un formulario interactivo con validación completa.
- Conceptos: Validación personalizada, mensajes de error, UX
- Tiempo: 3-4 horas

---

## 📖 Recursos de Aprendizaje

### Documentación Oficial:
- [MDN - JavaScript](https://developer.mozilla.org/es/docs/Web/JavaScript)
- [MDN - Guía de JavaScript](https://developer.mozilla.org/es/docs/Web/JavaScript/Guide)
- [JavaScript.info](https://javascript.info/) - Tutorial moderno

### Tutoriales Interactivos:
- [freeCodeCamp - JavaScript](https://www.freecodecamp.org/learn/javascript-algorithms-and-data-structures/)
- [Codecademy - Learn JavaScript](https://www.codecademy.com/learn/introduction-to-javascript)
- [Eloquent JavaScript](https://eloquentjavascript.net/) - Libro gratis

### Videos Recomendados:
- [JavaScript Crash Course - Traversy Media](https://www.youtube.com/watch?v=hdI2bqOjy3c)
- [JavaScript DOM Manipulation - freeCodeCamp](https://www.youtube.com/watch?v=5fb2aPlgoys)

### Herramientas Útiles:
- **Chrome DevTools Console**: Para probar código rápidamente (F12 → Console)
- **VS Code**: Con extensiones para JavaScript
- **Node.js**: Para ejecutar JavaScript fuera del navegador (opcional)

---

## 💡 Consejos para Este Módulo

### ✅ Buenas Prácticas:
1. **Usa `const` por defecto**, `let` cuando necesites reasignar
2. **Nombres descriptivos**: `calculateTotal` mejor que `calc`
3. **Usa camelCase**: `miVariable`, no `mi_variable`
4. **Comenta código complejo**: Explica el "por qué", no el "qué"
5. **Usa console.log()**: Para debugging y entender el flujo
6. **Prueba en la consola**: Experimenta con código antes de escribirlo
7. **Evita `var`**: Es legacy, usa `let` o `const`

### ❌ Errores Comunes:
- Olvidar punto y coma (;) - Aunque es opcional, es buena práctica
- Confundir `=` (asignación) con `===` (comparación)
- No usar `console.log()` para debugging
- Olvidar que JavaScript es case-sensitive (`myVar` ≠ `myvar`)
- No revisar la consola de errores
- Modificar el DOM antes de que cargue la página

---

## 🎯 Checklist del Módulo

Marca cada ejercicio al completarlo:

- [ ] Ejercicio 14 - Variables y Tipos
- [ ] Ejercicio 15 - Funciones
- [ ] Ejercicio 16 - Condicionales y Loops
- [ ] Ejercicio 17 - DOM Selección
- [ ] Ejercicio 18 - DOM Manipulación
- [ ] Ejercicio 19 - Eventos
- [ ] Ejercicio 20 - Validación de Formularios

**Al completar todos los ejercicios**, estarás listo para continuar con:
### 👉 [Módulo 4: Herramientas y Mejores Prácticas](../04-modulo-herramientas/README.md)

---

## 📝 Estructura de Archivos Recomendada

```
mi-proyecto/
├── index.html
├── css/
│   └── styles.css
├── js/
│   └── script.js  ← Tu código JavaScript
└── README.md
```

### Vincular JavaScript en HTML:
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
    <!-- Tu contenido HTML -->

    <!-- JavaScript al FINAL del body -->
    <script src="js/script.js"></script>
</body>
</html>
```

**¿Por qué al final del body?** Para que el HTML cargue primero, antes de ejecutar el JavaScript.

---

## 🔧 Plantilla JavaScript Base

```javascript
// script.js

// 1. Variables
const nombre = "Juan";
let edad = 25;

// 2. Función
function saludar(nombre) {
    return `Hola, ${nombre}!`;
}

// 3. Selección del DOM
const boton = document.querySelector('#mi-boton');
const titulo = document.querySelector('h1');

// 4. Event Listener
boton.addEventListener('click', function() {
    titulo.textContent = saludar(nombre);
    console.log('Botón clickeado!');
});

// 5. Console.log para debugging
console.log('Script cargado correctamente');
```

---

## 🐛 Debugging con Chrome DevTools

### Cómo usar la Consola:
1. **Abrir DevTools**: F12 o Click derecho → Inspeccionar
2. **Ir a Console**: Pestaña "Console"
3. **Ver errores**: Se muestran en rojo
4. **Probar código**: Escribe JavaScript directamente

### Comandos Útiles:
```javascript
// Imprimir en consola
console.log('Mensaje');
console.log(miVariable);

// Ver el tipo de dato
console.log(typeof miVariable);

// Tabla (para arrays/objects)
console.table(miArray);

// Advertencias y errores
console.warn('Esto es una advertencia');
console.error('Esto es un error');
```

---

## 📚 Glosario JavaScript

| Término | Significado |
|---------|-------------|
| **Variable** | Contenedor para almacenar datos |
| **Función** | Bloque de código reutilizable |
| **DOM** | Document Object Model (estructura HTML) |
| **Evento** | Acción del usuario (click, hover, submit) |
| **Event Listener** | Código que "escucha" eventos |
| **Scope** | Alcance de una variable (global, local) |
| **Callback** | Función pasada como argumento a otra función |
| **Array** | Lista ordenada de valores |
| **Object** | Colección de pares clave-valor |

---

## 🎓 Conceptos Importantes

### ES6+ (JavaScript Moderno)

JavaScript ha evolucionado. Estas son características modernas que usarás:

```javascript
// const y let (NO var)
const PI = 3.14159;
let contador = 0;

// Arrow functions
const sumar = (a, b) => a + b;

// Template literals
const mensaje = `Hola ${nombre}, tienes ${edad} años`;

// Destructuring
const { nombre, edad } = persona;
const [primero, segundo] = array;

// Spread operator
const nuevoArray = [...arrayViejo, nuevoElemento];
```

---

## 💻 Tipos de Datos en JavaScript

```javascript
// String (texto)
const nombre = "Juan";
const saludo = 'Hola';
const frase = `Template literal con ${variable}`;

// Number (números)
const edad = 25;
const precio = 19.99;

// Boolean (verdadero/falso)
const esMayorDeEdad = true;
const tieneDescuento = false;

// Array (lista)
const frutas = ['manzana', 'pera', 'uva'];
const numeros = [1, 2, 3, 4, 5];

// Object (objeto)
const persona = {
    nombre: 'Juan',
    edad: 25,
    ciudad: 'Madrid'
};

// Null y Undefined
const vacio = null;
let sinDefinir;  // undefined
```

---

## 🔄 Control de Flujo

```javascript
// IF / ELSE
if (edad >= 18) {
    console.log('Mayor de edad');
} else {
    console.log('Menor de edad');
}

// FOR Loop
for (let i = 0; i < 5; i++) {
    console.log(i);  // 0, 1, 2, 3, 4
}

// WHILE Loop
let contador = 0;
while (contador < 5) {
    console.log(contador);
    contador++;
}

// FOREACH (para arrays)
const frutas = ['manzana', 'pera', 'uva'];
frutas.forEach(fruta => {
    console.log(fruta);
});
```

---

## 🎯 Selectores DOM Más Comunes

```javascript
// Por ID
const elemento = document.getElementById('mi-id');

// Por selector CSS (el primero que encuentre)
const elemento = document.querySelector('.mi-clase');
const elemento = document.querySelector('#mi-id');

// Todos los que coincidan
const elementos = document.querySelectorAll('.mi-clase');

// Por clase (antiguo, mejor usar querySelector)
const elementos = document.getElementsByClassName('mi-clase');

// Por tag
const parrafos = document.getElementsByTagName('p');
```

---

## 📊 Roadmap de Aprendizaje

```
Semana 6: Fundamentos
├─ Variables y tipos de datos
├─ Funciones básicas
└─ Control de flujo

Semana 7: DOM
├─ Seleccionar elementos
├─ Modificar contenido
└─ Responder a eventos

Semana 8: Integración
└─ Proyecto: Formulario completo con validación
```

---

**¡Comienza con el [Ejercicio 14](./ejercicio-14-variables-y-tipos.md)! 🚀**
