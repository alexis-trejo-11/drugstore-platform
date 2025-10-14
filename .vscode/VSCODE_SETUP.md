# Configuración de VS Code para API Drugstore Microservices

Este documento explica cómo configurar VS Code correctamente para trabajar con este proyecto Gradle multi-módulo.

## Requisitos Previos

1. **Java 21** - Asegúrate de tener Java 21 instalado
2. **VS Code** - Visual Studio Code instalado
3. **Git** - Para clonar el repositorio

## Extensiones Requeridas

Instala las siguientes extensiones en VS Code:

1. **Extension Pack for Java** (`vscjava.vscode-java-pack`)
   - Incluye: Language Support for Java, Debugger for Java, Test Runner for Java, Maven for Java, Project Manager for Java, Visual Studio IntelliCode
2. **Gradle for Java** (`vscjava.vscode-gradle`)
3. **Spring Boot Extension Pack** (opcional pero recomendado)

## Configuración Automática

Este proyecto incluye configuración automática de VS Code en la carpeta `.vscode/`:

- **`settings.json`**: Configuración específica para Java y Gradle
- **`tasks.json`**: Tareas predefinidas para build y ejecución
- **`launch.json`**: Configuración de debug para los servicios

## Pasos de Configuración

### 1. Clonar y Abrir el Proyecto

```bash
git clone https://github.com/alexisTrejo11/API_Drugstore_Microservices.git
cd API_Drugstore_Microservices
code .
```

**IMPORTANTE**: Siempre abre VS Code desde la carpeta raíz del proyecto, NO desde subdirectorios individuales.

### 2. Configurar Java Runtime (si es necesario)

Si VS Code no detecta automáticamente Java 21, ve a:
- `Ctrl+Shift+P` → `Java: Configure Java Runtime`
- Asegúrate de que Java 21 esté seleccionado

### 3. Recargar el Proyecto Java

Si hay problemas con la detección del proyecto:
- `Ctrl+Shift+P` → `Java: Reload Projects`

### 4. Verificar la Configuración

1. Ejecuta el build completo:
   ```bash
   ./gradlew clean build -x test
   ```

2. Verifica que VS Code reconoce las dependencias:
   - Abre cualquier archivo Java en `order-service`
   - Debería poder importar clases de `libs:shared-kernel`

## Tareas Disponibles

Puedes ejecutar estas tareas desde VS Code:

- **Gradle: Build All** - Construye todo el proyecto
- **Gradle: Clean Build** - Limpia y construye todo
- **Gradle: Build Order Service** - Construye solo order-service
- **Gradle: Run Order Service** - Ejecuta order-service

Para ejecutar tareas: `Ctrl+Shift+P` → `Tasks: Run Task`

## Debug del Order Service

1. **Launch Configuration**: Usa "Debug Order Service" para ejecutar con debug
2. **Attach Configuration**: Usa "Attach to Order Service" para conectarte a una instancia ya ejecutándose

## Estructura del Proyecto

```
API_Drugstore_Microservices/
├── .vscode/                    # Configuración de VS Code
│   ├── settings.json
│   ├── tasks.json
│   └── launch.json
├── libs/
│   └── shared-kernel/          # Biblioteca compartida
├── order-service/              # Servicio de órdenes
├── product-service/            # Servicio de productos
├── cart-service/               # Servicio de carrito
├── auth-service/               # Servicio de autenticación
├── admin-service/              # Servicio de administración
├── config-server/              # Servidor de configuración
├── build.gradle                # Build principal
└── settings.gradle             # Configuración de módulos
```

## Solución de Problemas

### Error: "Missing mandatory Classpath entries"

1. Asegúrate de que VS Code esté abierto desde la carpeta raíz
2. Ejecuta: `./gradlew clean build -x test`
3. Recarga el proyecto: `Ctrl+Shift+P` → `Java: Reload Projects`

### Las dependencias de shared-kernel no se resuelven

1. Verifica que `settings.gradle` incluya `libs:shared-kernel`
2. Construye shared-kernel primero: `./gradlew :libs:shared-kernel:build`
3. Luego construye el servicio que lo necesita

### Java Runtime no detectado

1. `Ctrl+Shift+P` → `Java: Configure Java Runtime`
2. Añade la ruta a tu instalación de Java 21
3. Reinicia VS Code

## Migración a Nueva PC

1. Clona el repositorio
2. Instala las extensiones requeridas
3. Abre VS Code desde la carpeta raíz
4. La configuración se aplicará automáticamente

## Notas Adicionales

- Siempre ejecuta builds desde la carpeta raíz del proyecto
- Los archivos de configuración `.vscode/` están versionados en Git
- Si cambias la configuración, haz commit de los cambios para compartirlos con el equipo
