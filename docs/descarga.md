# Descarga e Instalación Rápida

## 📥 Opciones de Descarga

### Opción 1: Git Clone (Recomendado)
```bash
# HTTPS
git clone https://github.com/pragma/contract-to-feature-generator.git

# SSH
git clone git@github.com:pragma/contract-to-feature-generator.git

# Entrar al directorio
cd contract-to-feature-generator
```

**Ventajas:**
- ✅ Fácil actualización (`git pull`)
- ✅ Historial completo
- ✅ Contribuciones simplificadas

---

### Opción 2: Descargar ZIP
```bash
# Descargar última versión
wget https://github.com/pragma/contract-to-feature-generator/archive/refs/heads/main.zip

# Extraer
unzip main.zip
cd contract-to-feature-generator-main
```

**Ventajas:**
- ✅ No requiere Git
- ✅ Más rápido para pruebas

---

### Opción 3: Release Específica
```bash
# Ver releases disponibles
# https://github.com/pragma/contract-to-feature-generator/releases

# Descargar release específica
wget https://github.com/pragma/contract-to-feature-generator/archive/refs/tags/v1.0.0.zip

# Extraer
unzip v1.0.0.zip
cd contract-to-feature-generator-1.0.0
```

**Ventajas:**
- ✅ Versión estable
- ✅ Changelog incluido

---

## 🚀 Setup en 3 Minutos

### 1. Descargar
```bash
git clone https://github.com/pragma/contract-to-feature-generator.git
cd contract-to-feature-generator
```

### 2. Configurar
```bash
# Editar configuración
nano src/main/resources/config.properties

# Cambiar:
# - ContractPath: ruta a tu contrato
# - OutputPath: dónde guardar el .feature
# - UseIA: true/false
```

### 3. Ejecutar
```bash
./gradlew run
```

**¡Listo!** Tu archivo `.feature` está en `OutputPath`

---

## 📦 Contenido del Repositorio
```
contract-to-feature-generator/
├── 📄 README.md                          # Documentación principal
├── 📄 LICENSE                            # Licencia MIT
├── 📄 .gitignore                         # Archivos ignorados
├── 📄 build.gradle                       # Configuración Gradle
├── 📄 settings.gradle                    # Settings Gradle
├── 📄 catalog-info.yaml                  # Backstage metadata
├── 📄 mkdocs.yml                         # Documentación MkDocs
├── 📁 docs/                              # Documentación detallada
│   ├── index.md
│   ├── topicos.md
│   ├── tecnologias.md
│   ├── consideraciones.md
│   ├── instalacion.md
│   ├── descarga.md
│   └── tests.md
├── 📁 src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── Main.java                # Punto de entrada
│   │   │   ├── contract/                # Parser de contratos
│   │   │   ├── generator/               # Generador de escenarios
│   │   │   ├── ia/                      # Enriquecimiento IA
│   │   │   ├── writer/                  # Escritor de features
│   │   │   └── utils/                   # Utilidades
│   │   └── resources/
│   │       ├── config.properties        # Configuración
│   │       ├── logback.xml              # Configuración logs
│   │       └── contracts/
│   │           └── example-api.yml      # Contrato de ejemplo
│   └── test/
│       └── resources/
│           └── features/                # Features generados
└── 📁 gradle/                            # Wrapper Gradle
    └── wrapper/
```

---

## 🔍 Verificación Post-Descarga

### Check 1: Estructura de Archivos
```bash
# Verificar que existen archivos clave
ls -la | grep -E "(build.gradle|README.md|src)"
```

### Check 2: Permisos Gradlew
```bash
# Linux/Mac
ls -la gradlew
# Debe mostrar: -rwxr-xr-x (ejecutable)

# Si no tiene permisos:
chmod +x gradlew
```

### Check 3: Build Inicial
```bash
./gradlew build

# Salida esperada:
# BUILD SUCCESSFUL in XXs
```

---

## 📋 Requisitos Previos

Antes de descargar, asegúrate de tener:

| Requisito | Versión | Comando para verificar |
|-----------|---------|------------------------|
| Java | 17+ | `java -version` |
| Git (opcional) | 2.x+ | `git --version` |
| Ollama (opcional) | Latest | `ollama --version` |

**Si falta algo:**
- [Instalar Java](https://adoptium.net/)
- [Instalar Git](https://git-scm.com/)
- [Instalar Ollama](https://ollama.com/)

---

## 🔄 Actualización

### Si descargaste con Git:
```bash
cd contract-to-feature-generator
git pull origin main
./gradlew clean build
```

### Si descargaste ZIP:
```bash
# 1. Backup de configuración
cp src/main/resources/config.properties ~/config.backup

# 2. Descargar nuevo ZIP
wget https://github.com/pragma/contract-to-feature-generator/archive/main.zip

# 3. Extraer y reemplazar
unzip -o main.zip

# 4. Restaurar configuración
cp ~/config.backup src/main/resources/config.properties

# 5. Rebuild
./gradlew clean build
```

---

## 🌐 Descarga Alternativa (Sin Internet)

### Preparar en máquina con internet:
```bash
# 1. Clonar proyecto
git clone https://github.com/pragma/contract-to-feature-generator.git
cd contract-to-feature-generator

# 2. Descargar dependencias
./gradlew build

# 3. Crear bundle
tar -czf contract-generator-bundle.tar.gz \
    ./* \
    ~/.gradle/caches/modules-2/files-2.1/  # Dependencias

# 4. Transferir contract-generator-bundle.tar.gz a máquina sin internet
```

### Instalar en máquina sin internet:
```bash
# 1. Extraer
tar -xzf contract-generator-bundle.tar.gz

# 2. Copiar dependencias a ~/.gradle/
cp -r modules-2/files-2.1/ ~/.gradle/caches/modules-2/

# 3. Build offline
./gradlew build --offline
```

---

## 🐳 Docker Image (Alternativa)

### Descargar imagen:
```bash
# Pull desde Docker Hub (cuando esté disponible)
docker pull pragma/contract-generator:latest

# O build local
git clone https://github.com/pragma/contract-to-feature-generator.git
cd contract-to-feature-generator
docker build -t contract-generator .
```

### Ejecutar:
```bash
docker run -v $(pwd)/contracts:/app/contracts \
           -v $(pwd)/output:/app/output \
           contract-generator
```

---

## 📱 Descarga Mobile/Tablet

**No soportado directamente**, pero puedes:

1. **Termux (Android):**
```bash
   # Instalar Termux desde F-Droid
   pkg install git openjdk-17
   git clone https://github.com/pragma/contract-to-feature-generator.git
   cd contract-to-feature-generator
   ./gradlew build
```

2. **iSH (iOS):**
```bash
   # Instalar iSH desde App Store
   apk add git openjdk17
   git clone https://github.com/pragma/contract-to-feature-generator.git
```

---

## 🔐 Verificación de Integridad

### Verificar checksum (cuando esté disponible):
```bash
# SHA256
sha256sum contract-generator-1.0.0.zip
# Comparar con: https://github.com/pragma/.../releases/SHA256SUMS

# MD5
md5sum contract-generator-1.0.0.zip
```

### Verificar firma GPG (cuando esté disponible):
```bash
gpg --verify contract-generator-1.0.0.zip.sig contract-generator-1.0.0.zip
```

---

## 🆘 Problemas de Descarga

### Error: "Connection timed out"
```bash
# Usar mirror o proxy
git clone --config http.proxy=http://proxy:8080 \
  https://github.com/pragma/contract-to-feature-generator.git
```

### Error: "Repository not found"
```bash
# Verificar URL
# Debe ser exactamente:
https://github.com/pragma/contract-to-feature-generator.git

# Si es privado, usar SSH:
git clone git@github.com:pragma/contract-to-feature-generator.git
```

### Error: "Permission denied (publickey)"
```bash
# Generar SSH key
ssh-keygen -t ed25519 -C "tu@email.com"

# Agregar a GitHub
cat ~/.ssh/id_ed25519.pub
# Copiar y pegar en: https://github.com/settings/keys
```

---

## 📊 Tamaños de Descarga

| Componente | Tamaño |
|-----------|--------|
| Código fuente | ~500 KB |
| Dependencias (primera vez) | ~50 MB |
| Ollama (opcional) | ~600 MB |
| Mistral model (opcional) | ~4 GB |
| **Total (sin IA)** | **~50 MB** |
| **Total (con IA)** | **~5 GB** |

---

## ⏱️ Tiempos de Descarga

| Conexión | Sin IA | Con IA |
|----------|--------|--------|
| 10 Mbps | ~40s | ~1h |
| 50 Mbps | ~8s | ~12min |
| 100 Mbps | ~4s | ~6min |
| 1 Gbps | <1s | ~40s |

---

## ✅ Checklist Post-Descarga

- [ ] Proyecto descargado y extraído
- [ ] `./gradlew --version` funciona
- [ ] `./gradlew build` exitoso
- [ ] `config.properties` configurado
- [ ] Contrato de ejemplo ejecutado
- [ ] Output generado correctamente

---

## 🎓 Siguiente Paso

Una vez descargado:
1. ✅ [Configurar el proyecto](instalacion.md)
2. ✅ [Ver ejemplos de uso](index.md#ejemplo)
3. ✅ [Ejecutar primera generación](../README.md#ejecución)

---

## 📞 Soporte de Descarga

**Problemas con descarga:**
- GitHub Issues: https://github.com/pragma/contract-to-feature-generator/issues
- Email: qa-team@pragma.com.co
- Slack: #contract-generator