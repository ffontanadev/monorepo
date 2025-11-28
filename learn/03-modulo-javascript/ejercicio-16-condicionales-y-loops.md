# Ejercicio 16 - Condicionales y Loops

## 🎯 Objetivo
Controlar el flujo del programa con condicionales (if/else/switch) y loops (for/while/forEach).

## 📚 Conceptos Clave
- Condicionales: if, else if, else, switch
- Operadores lógicos: &&, ||, !
- Loops: for, while, do-while
- Array methods: forEach, map, filter
- Break y continue

## 💻 Tarea

### Parte 1: If/Else
```javascript
const edad = 20;

if (edad >= 18) {
    console.log("Mayor de edad");
} else {
    console.log("Menor de edad");
}

// Múltiples condiciones
const nota = 85;

if (nota >= 90) {
    console.log("A - Excelente");
} else if (nota >= 80) {
    console.log("B - Muy Bueno");
} else if (nota >= 70) {
    console.log("C - Bueno");
} else {
    console.log("D - Insuficiente");
}
```

### Parte 2: Switch
```javascript
const dia = "lunes";

switch(dia) {
    case "lunes":
    case "martes":
    case "miércoles":
    case "jueves":
    case "viernes":
        console.log("Día laboral");
        break;
    case "sábado":
    case "domingo":
        console.log("Fin de semana");
        break;
    default:
        console.log("Día inválido");
}
```

### Parte 3: For Loop
```javascript
// Contar del 1 al 10
for (let i = 1; i <= 10; i++) {
    console.log(i);
}

// Recorrer un array
const frutas = ["manzana", "pera", "uva"];
for (let i = 0; i < frutas.length; i++) {
    console.log(frutas[i]);
}
```

### Parte 4: While Loop
```javascript
let contador = 0;
while (contador < 5) {
    console.log(`Contador: ${contador}`);
    contador++;
}
```

### Parte 5: ForEach (Array Method)
```javascript
const numeros = [1, 2, 3, 4, 5];

numeros.forEach(numero => {
    console.log(numero * 2);
});
```

## Ejercicios Prácticos

1. **FizzBuzz**: Para números 1-30, imprime "Fizz" si es divisible por 3, "Buzz" por 5, "FizzBuzz" por ambos
2. **Tabla de multiplicar**: Genera la tabla del 7 (7x1 hasta 7x10)
3. **Filtrar pares**: De un array de números, imprime solo los pares
4. **Suma acumulativa**: Suma todos los números del 1 al 100
5. **Búsqueda**: Encuentra si un elemento existe en un array

## ✅ Checklist
- [ ] Usas if/else correctamente
- [ ] Implementas switch con break
- [ ] Creas for loops que funcionan
- [ ] Usas while apropiadamente
- [ ] Pruebas forEach en arrays
- [ ] Entiendes break y continue
- [ ] Los loops no son infinitos

---

### 🎯 Continúa con: [Ejercicio 17 - DOM Selección](./ejercicio-17-dom-seleccion.md)
