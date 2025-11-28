# Guía 03 - Git Básico

## 🎯 Objetivo
Aprender control de versiones con Git y subir tu código a GitHub.

## 📚 ¿Qué es Git?
Git es un sistema de control de versiones que te permite:
- Guardar "snapshots" (commits) de tu código
- Volver a versiones anteriores
- Trabajar en equipo sin conflictos
- Tener un historial de cambios

## 🔧 Instalación

### Windows
- Descarga [Git for Windows](https://git-scm.com/download/win)

### Mac
```bash
brew install git
```

### Linux
```bash
sudo apt-get install git
```

## 💻 Comandos Básicos

### 1. Configuración inicial (solo una vez)
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

### 2. Crear un repositorio
```bash
cd mi-proyecto
git init  # Inicializa Git en la carpeta actual
```

### 3. Ver estado
```bash
git status  # Ve qué archivos cambiaron
```

### 4. Agregar archivos al staging
```bash
git add index.html       # Agrega un archivo
git add .                # Agrega todos los archivos
git add *.css            # Agrega todos los CSS
```

### 5. Hacer commit (guardar snapshot)
```bash
git commit -m "Mensaje descriptivo del cambio"
```

Ejemplos de buenos mensajes:
- "Add hero section to landing page"
- "Fix responsive layout on mobile"
- "Update contact form validation"

### 6. Ver historial
```bash
git log                  # Historial completo
git log --oneline        # Versión resumida
```

## 🌐 Subir a GitHub

### Paso 1: Crear repositorio en GitHub
1. Ve a [github.com](https://github.com)
2. Click en "New repository"
3. Ponle un nombre
4. Click "Create repository"

### Paso 2: Conectar y subir
```bash
# Agregar remoto (solo primera vez)
git remote add origin https://github.com/tu-usuario/tu-repo.git

# Subir código
git push -u origin main
```

### Paso 3: Futuros cambios
```bash
# Flujo normal de trabajo:
git add .
git commit -m "Descripción del cambio"
git push
```

## ✅ Checklist de Habilidades
- [ ] Instalas Git
- [ ] Configuras tu nombre y email
- [ ] Inicializas un repositorio con `git init`
- [ ] Haces commits con mensajes claros
- [ ] Creas una cuenta en GitHub
- [ ] Subes un repositorio a GitHub
- [ ] Entiendes el flujo: add → commit → push

## 💡 Consejos
- **Commits frecuentes**: Haz commits cada vez que completes una feature pequeña
- **Mensajes claros**: Usa mensajes descriptivos, no "fix", "update", "changes"
- **.gitignore**: Crea un archivo `.gitignore` para excluir `node_modules/`, `.env`, etc.
- **README.md**: Siempre incluye un README explicando tu proyecto

## 📝 Ejemplo de .gitignore
```
# Node
node_modules/
npm-debug.log

# Environment variables
.env

# IDE
.vscode/
.idea/

# OS
.DS_Store
Thumbs.db
```

---

### 👉 Continúa con: [Guía 04 - Mejores Prácticas](./guia-04-mejores-practicas.md)
