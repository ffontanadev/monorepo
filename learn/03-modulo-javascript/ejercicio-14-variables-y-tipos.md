# Ejercicio 14 - Variables y Tipos

## 🎯 Objetivo
Aprender los fundamentos de JavaScript: variables, tipos de datos, operadores y cómo trabajar con ellos.

## 📚 Conceptos Clave
- Declaración de variables: `const`, `let` (evitar `var`)
- Tipos de datos primitivos: string, number, boolean, null, undefined
- Tipos de datos complejos: arrays, objects
- Operadores: aritméticos, de comparación, lógicos
- Template literals
- `console.log()` para debugging

## 🔍 Investigación Previa (20 min)

Antes de empezar, investiga en estos recursos:
- [MDN - Variables](https://developer.mozilla.org/es/docs/Learn/JavaScript/First_steps/Variables)
- [MDN - Tipos de datos](https://developer.mozilla.org/es/docs/Web/JavaScript/Data_structures)
- [JavaScript.info - Variables](https://javascript.info/variables)

### Preguntas para investigar:
1. ¿Cuál es la diferencia entre `const` y `let`?
2. ¿Por qué evitar `var`?
3. ¿Qué tipos de datos existen en JavaScript?
4. ¿Qué es `typeof` y para qué sirve?
5. ¿Qué son los template literals y cómo se usan?

## 💻 Tarea

Crea dos archivos:
1. **`variables-demo.html`**
2. **`js/variables.js`**

### Tu script JavaScript debe:

#### Parte 1: Variables con Datos Personales
```javascript
// Información personal (usa const y let apropiadamente)
const nombre = "Tu nombre";
const apellido = "Tu apellido";
let edad = 25;  // Puede cambiar
const ciudadNacimiento = "Tu ciudad";
let ciudadActual = "Ciudad actual";

// Imprime cada variable con console.log
console.log(nombre);
console.log(typeof edad);  // Muestra el tipo
```

#### Parte 2: Strings y Template Literals
```javascript
// Combina las variables anteriores en frases
const nombreCompleto = nombre + " " + apellido;  // Concatenación
const presentacion = `Hola, soy ${nombre} y tengo ${edad} años`;  // Template literal

console.log(presentacion);
```

#### Parte 3: Numbers y Operaciones
```javascript
const precio1 = 29.99;
const precio2 = 15.50;
const precio3 = 45.00;

const subtotal = precio1 + precio2 + precio3;
const impuesto = subtotal * 0.21;  // 21%
const total = subtotal + impuesto;

console.log(`Subtotal: ${subtotal}`);
console.log(`Impuesto: ${impuesto}`);
console.log(`Total: ${total.toFixed(2)}`);  // 2 decimales
```

#### Parte 4: Booleans y Comparaciones
```javascript
const esMayorDeEdad = edad >= 18;
const tieneDescuento = total > 50;

console.log(`¿Es mayor de edad? ${esMayorDeEdad}`);
console.log(`¿Tiene descuento? ${tieneDescuento}`);

// Comparaciones
console.log(10 == "10");   // true (compara valor)
console.log(10 === "10");  // false (compara valor Y tipo)
```

#### Parte 5: Arrays
```javascript
const hobbies = ["programación", "lectura", "deportes"];
const numeros = [1, 2, 3, 4, 5];

console.log(hobbies[0]);  // Primer elemento
console.log(hobbies.length);  // Cantidad de elementos

// Agregar elemento
hobbies.push("música");
console.log(hobbies);
```

#### Parte 6: Objects
```javascript
const persona = {
    nombre: "Juan",
    edad: 25,
    ciudad: "Madrid",
    profesion: "Desarrollador"
};

console.log(persona.nombre);  // Acceso con punto
console.log(persona["edad"]);  // Acceso con corchetes

// Modificar propiedad
persona.edad = 26;
console.log(persona);
```

### Requisitos Específicos:
- Vincula el JavaScript al HTML (al final del body)
- Usa `const` para valores que no cambian
- Usa `let` para valores que pueden cambiar
- Usa al menos 5 `console.log()` diferentes
- Prueba todos los tipos de datos mencionados
- Usa al menos 2 template literals
- Crea al menos 1 array y 1 object

## ✅ Checklist de Autoevaluación

### Variables:
- [ ] Declaras variables con `const` para valores constantes
- [ ] Declaras variables con `let` para valores que cambian
- [ ] NO usas `var` en ningún lugar
- [ ] Los nombres de variables usan camelCase
- [ ] Los nombres son descriptivos (no `x`, `y`, `dato`)

### Tipos de Datos:
- [ ] Trabajas con strings (texto)
- [ ] Trabajas con numbers (números)
- [ ] Trabajas con booleans (true/false)
- [ ] Creas al menos 1 array
- [ ] Creas al menos 1 object
- [ ] Usas `typeof` para verificar tipos

### Strings:
- [ ] Usas concatenación con `+`
- [ ] Usas template literals con backticks `` ` ``
- [ ] Incluyes variables en template literals con `${variable}`

### Operadores Aritméticos:
- [ ] Usas suma (+), resta (-), multiplicación (*), división (/)
- [ ] Calculas correctamente el total con impuestos

### Operadores de Comparación:
- [ ] Entiendes la diferencia entre `==` y `===`
- [ ] Usas `>=`, `<=`, `>`, `<` apropiadamente
- [ ] Prefieres `===` sobre `==` (comparación estricta)

### Arrays:
- [ ] Accedes a elementos por índice: `array[0]`
- [ ] Usas `.length` para obtener tamaño
- [ ] Usas `.push()` para agregar elementos

### Objects:
- [ ] Accedes a propiedades con punto: `objeto.propiedad`
- [ ] Accedes a propiedades con corchetes: `objeto["propiedad"]`
- [ ] Modificas propiedades existentes

### Console.log:
- [ ] Usas `console.log()` extensivamente
- [ ] Abres DevTools (F12) → Console para ver resultados
- [ ] Todos los logs aparecen sin errores
- [ ] Entiendes lo que imprime cada console.log

### Código Limpio:
- [ ] El código tiene comentarios explicativos
- [ ] Cada sección está claramente separada
- [ ] No hay errores en la consola

## 🎁 Extra (Opcional)

Si quieres ir más allá:
- Usa destructuring: `const { nombre, edad } = persona`
- Experimenta con `null` y `undefined`
- Usa operador ternario: `edad >= 18 ? "adulto" : "menor"`
- Prueba métodos de strings: `.toUpperCase()`, `.toLowerCase()`, `.slice()`
- Prueba métodos de arrays: `.pop()`, `.shift()`, `.unshift()`

## 🐛 Errores Comunes

### Error 1: Reasignar const
```javascript
// ❌ ERROR
const edad = 25;
edad = 26;  // Error: no se puede reasignar const
```

**Solución**:
```javascript
// ✅ CORRECTO
let edad = 25;
edad = 26;  // Ahora sí funciona
```

### Error 2: Olvidar declarar variables
```javascript
// ❌ MAL
nombre = "Juan";  // Variable global implícita (evitar)
```

**Solución**:
```javascript
// ✅ CORRECTO
const nombre = "Juan";
```

### Error 3: Confundir == con ===
```javascript
console.log(10 == "10");   // true (convierte tipos)
console.log(10 === "10");  // false (compara tipo también)
```

**Mejor práctica**: Siempre usa `===` y `!==` (estrictos).

### Error 4: Acceso a índice que no existe
```javascript
const array = [1, 2, 3];
console.log(array[10]);  // undefined (no existe)
```

**Solución**: Verifica la longitud con `.length` antes de acceder.

## 🔗 Recursos Adicionales

- [JavaScript Visualizer](https://www.jsv9000.app/) - Ve cómo se ejecuta el código
- [MDN - Operadores](https://developer.mozilla.org/es/docs/Web/JavaScript/Guide/Expressions_and_Operators)
- [JavaScript Equality Table](https://dorey.github.io/JavaScript-Equality-Table/) - == vs ===

## 💡 Tips de Debugging

### Usa console.log estratégicamente:
```javascript
const precio = 29.99;
console.log("Precio:", precio);  // Etiqueta para claridad

const total = precio * 1.21;
console.log("Total con impuesto:", total);

// Ver el tipo
console.log("Tipo de total:", typeof total);

// Ver el valor completo de un object
console.log("Persona completa:", persona);
```

### Usa console.table para arrays y objects:
```javascript
console.table(hobbies);
console.table(persona);
```

## 📋 Plantilla de Inicio

```javascript
// ===== VARIABLES =====
console.log("=== VARIABLES ===");

const nombre = "Juan";
let edad = 25;

console.log("Nombre:", nombre);
console.log("Edad:", edad);
console.log("Tipo de edad:", typeof edad);

// ===== STRINGS =====
console.log("\n=== STRINGS ===");

const saludo = `Hola, soy ${nombre} y tengo ${edad} años`;
console.log(saludo);

// ===== NUMBERS =====
console.log("\n=== NUMBERS ===");

const precio = 29.99;
const cantidad = 3;
const total = precio * cantidad;

console.log(`${cantidad} items x $${precio} = $${total}`);

// ===== BOOLEANS =====
console.log("\n=== BOOLEANS ===");

const esMayorDeEdad = edad >= 18;
console.log("¿Es mayor de edad?", esMayorDeEdad);

// ===== ARRAYS =====
console.log("\n=== ARRAYS ===");

const frutas = ["manzana", "pera", "uva"];
console.log("Frutas:", frutas);
console.log("Primera fruta:", frutas[0]);
console.log("Total frutas:", frutas.length);

// ===== OBJECTS =====
console.log("\n=== OBJECTS ===");

const persona = {
    nombre: "Juan",
    edad: 25,
    ciudad: "Madrid"
};

console.log("Persona:", persona);
console.log("Nombre de la persona:", persona.nombre);
```

---

### 🎯 Una vez completado y validado, continúa con:
## 👉 [Ejercicio 15 - Funciones](./ejercicio-15-funciones.md)
