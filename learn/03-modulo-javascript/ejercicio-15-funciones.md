# Ejercicio 15 - Funciones

## 🎯 Objetivo
Aprender a crear funciones reutilizables, trabajar con parámetros, valores de retorno y entender el scope.

## 📚 Conceptos Clave
- Declaración de funciones: function y arrow functions
- Parámetros y argumentos
- Valor de retorno con `return`
- Scope (alcance): global vs local
- Funciones anónimas y callbacks

## 🔍 Investigación Previa (20 min)
- [MDN - Funciones](https://developer.mozilla.org/es/docs/Web/JavaScript/Guide/Functions)
- [JavaScript.info - Functions](https://javascript.info/function-basics)

## 💻 Tarea

Crea `js/funciones.js` y desarrolla:

### Parte 1: Funciones Básicas
```javascript
// Función que saluda
function saludar(nombre) {
    return `Hola, ${nombre}!`;
}

console.log(saludar("Juan"));
```

### Parte 2: Funciones con Múltiples Parámetros
```javascript
function calcularAreaRectangulo(base, altura) {
    return base * altura;
}

// Con valores por defecto
function saludarConTitulo(nombre, titulo = "Sr.") {
    return `Hola ${titulo} ${nombre}`;
}
```

### Parte 3: Arrow Functions
```javascript
const sumar = (a, b) => a + b;
const duplicar = x => x * 2;

// Arrow function con múltiples líneas
const calcularTotal = (subtotal, impuesto) => {
    const total = subtotal + (subtotal * impuesto);
    return total.toFixed(2);
};
```

### Parte 4: Funciones Prácticas
Crea funciones para:
- `esPar(numero)`: retorna true si es par
- `celsiusAFahrenheit(celsius)`: convierte temperatura
- `obtenerMayor(num1, num2)`: retorna el número mayor
- `contarVocales(texto)`: cuenta vocales en un string
- `generarEmail(nombre, apellido)`: genera email (ej: juan.perez@email.com)

## ✅ Checklist
- [ ] Creas funciones con `function` keyword
- [ ] Creas arrow functions con `=>`
- [ ] Usas parámetros correctamente
- [ ] Usas `return` para devolver valores
- [ ] Pruebas cada función con console.log
- [ ] Las funciones tienen nombres descriptivos
- [ ] Entiendes el scope local vs global

## 🎁 Extra
- Experimenta con rest parameters: `function sumar(...numeros)`
- Usa destructuring en parámetros
- Crea funciones recursivas (función que se llama a sí misma)

---

### 🎯 Continúa con: [Ejercicio 16 - Condicionales y Loops](./ejercicio-16-condicionales-y-loops.md)
