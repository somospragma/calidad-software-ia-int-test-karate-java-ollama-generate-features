# Instalación y Configuración

## 📋 Pre-requisitos

### 1. Java Development Kit (JDK) 17+

**Verificar instalación:**
```bash
java -version
# Debe mostrar: openjdk version "17.x.x" o superior
```

**Instalar si no está:**

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Linux (RHEL/CentOS)
```bash
sudo yum install java-17-openjdk-devel
```

#### macOS
```bash
# Con Homebrew
brew install openjdk@17

# Configurar JAVA_HOME
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
```

#### Windows
1. Descargar desde [Adoptium](https://adoptium.net/)
2. Ejecutar instalador
3. Configurar `JAVA_HOME`:
    - Panel de Control → Sistema → Variables de entorno
    - Nueva variable: `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-17.x.x`
    - Agregar a `Path`: `%JAVA_HOME%\bin`

**Verificar:**
```bash
echo $JAVA_HOME
# Linux/Mac: /usr/lib/jvm/java-17-openjdk-amd64
# Windows: C:\Program Files\Eclipse Adoptium\jdk-17.x.x
```

---

### 2. Git

**Verificar:**
```bash
git --version
# git version 2.x.x
```

**Instalar:**
```bash
# Linux
sudo apt install git

# macOS
brew install git

# Windows: https://git-scm.com/download/win
```

---

### 3. Ollama (Opcional - para IA)

**Solo necesario si `UseIA=true`**

#### Linux
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

#### macOS
```bash
# Descargar desde https://ollama.com/download
# O con Homebrew
brew install ollama
```

#### Windows
1. Descargar desde [https://ollama.com/download](https://ollama.com/download)
2. Ejecutar instalador `OllamaSetup.exe`

**Verificar instalación:**
```bash
ollama --version
# ollama version is 0.x.x
```

**Instalar Mistral:**
```bash
ollama pull mistral
```

**Iniciar servicio:**
```bash
# Linux/Mac
ollama serve

# Windows: Se inicia automáticamente como servicio
```

**Verificar que funciona:**
```bash
curl http://localhost:11434/api/tags
# Debe retornar JSON con modelos instalados
```

---

## 🚀 Instalación del Proyecto

### Opción 1: Clonar desde Git
```bash
# 1. Clonar repositorio
git clone https://github.com/pragma/contract-to-feature-generator.git
cd contract-to-feature-generator

# 2. Verificar estructura
ls -la
# Debe mostrar: build.gradle, src/, README.md, etc.

# 3. Dar permisos al gradlew (Linux/Mac)
chmod +x gradlew

# 4. Build inicial (descarga dependencias)
./gradlew build

# Windows:
# gradlew.bat build
```

---

### Opción 2: Descargar ZIP
```bash
# 1. Descargar desde GitHub
# https://github.com/pragma/contract-to-feature-generator/archive/main.zip

# 2. Extraer
unzip contract-to-feature-generator-main.zip
cd contract-to-feature-generator-main

# 3. Continuar con paso 3 de Opción 1
```

---

## ⚙️ Configuración

### 1. Crear Archivo de Configuración

El archivo `src/main/resources/config.properties` debe existir. Si no:
```bash
# Crear desde plantilla
cat > src/main/resources/config.properties << 'EOF'
# Rutas de archivos
ContractPath=src/main/resources/contracts/example-api.yml
OutputPath=src/test/resources/features/generated-api.feature

# Configuración de Ollama
OllamaURI=http://127.0.0.1:11434/api/generate
OllamaModel=mistral
OllamaConnectTimeout=100000
OllamaReadTimeout=300000

# Configuración de IA
IA=Mistral
UseIA=true

# Estrategias de generación
GenerateHappyPath=true
GenerateValidations=true
GenerateErrorCases=true
GenerateEdgeCases=true
EOF
```

---

### 2. Agregar Tu Contrato
```bash
# Copiar tu contrato YAML
cp /path/to/mi-api.yml src/main/resources/contracts/

# Actualizar config.properties
nano src/main/resources/config.properties
# Cambiar: ContractPath=src/main/resources/contracts/mi-api.yml
```

---

### 3. Configurar Rutas de Salida
```properties
# Para un solo servicio
OutputPath=src/test/resources/features/mi-api.feature

# Para múltiples servicios
OutputPath=src/test/resources/features/users-api.feature
OutputPath=src/test/resources/features/products-api.feature
```

---

### 4. Configurar IA (Opcional)

#### Si NO quieres usar IA:
```properties
UseIA=false
```

#### Si quieres usar IA:
```properties
UseIA=true
IA=Mistral

# Verificar que Ollama esté corriendo
# ollama serve
```

---

## 🔧 Configuración Avanzada

### Variables de Entorno (Alternativa a config.properties)
```bash
# Linux/Mac - Agregar a ~/.bashrc o ~/.zshrc
export CONTRACT_PATH="src/main/resources/contracts/api.yml"
export OUTPUT_PATH="src/test/resources/features/api.feature"
export USE_IA="true"
export OLLAMA_URI="http://localhost:11434/api/generate"

# Windows - PowerShell
$env:CONTRACT_PATH="src\main\resources\contracts\api.yml"
$env:OUTPUT_PATH="src\test\resources\features\api.feature"
```

**Modificar ConfigReader.java:**
```java
public static String getPropertyByKey(String key) {
    // Primero buscar en variables de entorno
    String envValue = System.getenv(key.toUpperCase().replace(".", "_"));
    if (envValue != null) return envValue;
    
    // Luego en properties
    return properties.getProperty(key);
}
```

---

### Configuración por Perfil
```bash
# Crear perfiles
src/main/resources/
├── config.properties              # Default
├── config-dev.properties          # Development
├── config-staging.properties      # Staging
└── config-prod.properties         # Production
```

**Ejecutar con perfil:**
```bash
./gradlew run -Dconfig.profile=dev
```

---

## ✅ Verificación de Instalación

### Test de Compilación
```bash
./gradlew clean build

# Salida esperada:
# BUILD SUCCESSFUL in 5s
# 5 actionable tasks: 5 executed
```

---

### Test de Ejecución
```bash
./gradlew run

# Salida esperada:
# 🚀 Contract-to-Feature Generator - Iniciando...
# 📄 Paso 1/5: Parseando contrato YAML...
# ✅ Contrato parseado: 4 endpoints
# ...
# ✅ GENERACIÓN COMPLETADA EXITOSAMENTE
```

---

### Test de Ollama (si UseIA=true)
```bash
# Test manual
curl -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "mistral",
    "prompt": "Hola",
    "stream": false
  }'

# Debe retornar JSON con response
```

---

## 🐛 Troubleshooting de Instalación

### Error: "Could not find Java"

**Síntoma:**
```bash
./gradlew build
# Error: JAVA_HOME is not set
```

**Solución:**
```bash
# Encontrar Java
which java
# /usr/bin/java

# Encontrar JAVA_HOME
readlink -f $(which java) | sed "s:/bin/java::"
# /usr/lib/jvm/java-17-openjdk-amd64

# Configurar
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

---

### Error: "Permission denied: ./gradlew"

**Síntoma:**
```bash
./gradlew build
# bash: ./gradlew: Permission denied
```

**Solución:**
```bash
chmod +x gradlew
./gradlew build
```

---

### Error: "Could not resolve dependencies"

**Síntoma:**
```bash
./gradlew build
# Could not resolve org.yaml:snakeyaml:2.2
```

**Solución 1 - Proxy:**
```bash
# Configurar proxy en ~/.gradle/gradle.properties
systemProp.http.proxyHost=proxy.company.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.company.com
systemProp.https.proxyPort=8080
```

**Solución 2 - Limpiar caché:**
```bash
rm -rf ~/.gradle/caches
./gradlew build --refresh-dependencies
```

---

### Error: "Ollama connection refused"

**Síntoma:**
```bash
❌ Error al comunicarse con Ollama
Connection refused: connect
```

**Solución:**
```bash
# 1. Verificar que Ollama esté instalado
ollama --version

# 2. Iniciar servicio
ollama serve

# 3. Verificar puerto
netstat -an | grep 11434
# Debe mostrar: LISTEN en 11434

# 4. Test de conexión
curl http://localhost:11434/api/tags
```

---

### Error: "Model not found: mistral"

**Síntoma:**
```bash
❌ Error: model 'mistral' not found
```

**Solución:**
```bash
# Listar modelos instalados
ollama list

# Si Mistral no está:
ollama pull mistral

# Esperar descarga (~4GB)
# Verificar:
ollama list
# Should show mistral
```

---

## 📦 Instalación en Docker (Alternativa)
```dockerfile
# Dockerfile
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/src/main/resources /app/resources

# Si se usa IA, instalar Ollama
# RUN curl -fsSL https://ollama.com/install.sh | sh

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build y Run:**
```bash
# Build imagen
docker build -t contract-generator .

# Run
docker run -v $(pwd)/contracts:/app/contracts \
           -v $(pwd)/output:/app/output \
           contract-generator
```

---

## 🔄 Actualización

### Actualizar el Proyecto
```bash
# 1. Backup de configuración
cp src/main/resources/config.properties config.properties.backup

# 2. Pull cambios
git pull origin main

# 3. Restaurar configuración
cp config.properties.backup src/main/resources/config.properties

# 4. Rebuild
./gradlew clean build
```

---

### Actualizar Dependencias
```bash
# Ver versiones disponibles
./gradlew dependencyUpdates

# Editar build.gradle con nuevas versiones
nano build.gradle

# Rebuild
./gradlew clean build --refresh-dependencies
```

---

### Actualizar Ollama y Mistral
```bash
# Actualizar Ollama
# Linux/Mac: Reinstalar desde https://ollama.com/
# Windows: Descargar nuevo instalador

# Actualizar Mistral
ollama pull mistral
```

---

## 📱 Configuración IDE

### IntelliJ IDEA

1. **Abrir proyecto:**
    - File → Open → Seleccionar carpeta del proyecto
    - Esperar a que Gradle importe

2. **Configurar JDK:**
    - File → Project Structure → Project
    - SDK: 17 (corretto-17, openjdk-17, etc.)

3. **Run Configuration:**
    - Run → Edit Configurations
    - '+' → Application
    - Main class: `org.example.Main`
    - Working directory: raíz del proyecto

---

### VS Code

1. **Instalar extensiones:**
```bash
   # Extension Pack for Java
   code --install-extension vscjava.vscode-java-pack
   
   # Gradle for Java
   code --install-extension vscjava.vscode-gradle
```

2. **Abrir proyecto:**
```bash
   code /path/to/contract-to-feature-generator
```

3. **Run:**
    - F5 → Seleccionar "Java"
    - Seleccionar `Main.java`

---

### Eclipse

1. **Importar proyecto:**
    - File → Import → Gradle → Existing Gradle Project
    - Seleccionar carpeta

2. **Configurar JDK:**
    - Window → Preferences → Java → Installed JREs
    - Add → Standard VM → Seleccionar JDK 17

3. **Run:**
    - Right-click en `Main.java` → Run As → Java Application

---

## ✅ Checklist Final

Antes de ejecutar por primera vez:

- [ ] Java 17+ instalado (`java -version`)
- [ ] Gradle funciona (`./gradlew --version`)
- [ ] Git instalado (`git --version`)
- [ ] Proyecto clonado y en carpeta correcta
- [ ] `config.properties` existe y está configurado
- [ ] Contrato YAML copiado a `src/main/resources/contracts/`
- [ ] Si UseIA=true: Ollama instalado y corriendo
- [ ] Si UseIA=true: Mistral descargado (`ollama list`)
- [ ] Build exitoso (`./gradlew build`)
- [ ] Primera ejecución exitosa (`./gradlew run`)

---

## 🎓 Próximos Pasos

1. ✅ [Ver ejemplos de uso](../README.md#ejemplos)
2. ✅ [Ejecutar tests](tests.md)
3. ✅ [Leer consideraciones](consideraciones.md)
4. ✅ [Revisar tecnologías](tecnologias.md)

---

## 📞 Ayuda

Si tienes problemas:
1. Revisar [Troubleshooting](#troubleshooting-de-instalación)
2. Consultar [Issues en GitHub](https://github.com/pragma/contract-to-feature-generator/issues)
3. Contactar al equipo QA