# Stack Tecnológico

## 🛠️ Tecnologías Core

### Java 17
**Versión:** 17 (LTS)  
**Rol:** Lenguaje principal del proyecto

**Características usadas:**
- Records (para DTOs inmutables)
- Switch expressions
- Text blocks (para templates)
- Pattern matching

**Ejemplo:**
```java
public record Endpoint(
        String path,
        HttpMethod method,
        String summary,
        Schema requestBody
) {}
```

---

### Gradle 8.x
**Versión:** 8.5+  
**Rol:** Build automation y gestión de dependencias

**Plugins usados:**
- `java`
- `application`

**Tareas principales:**
```bash
./gradlew build      # Compilar
./gradlew run        # Ejecutar
./gradlew test       # Tests
./gradlew clean      # Limpiar
```

---

## 📦 Dependencias Principales

### 1. SnakeYAML 2.2
**Propósito:** Parser de archivos YAML
```gradle
implementation 'org.yaml:snakeyaml:2.2'
```

**Uso:**
```java
Yaml yaml = new Yaml();
Map<String, Object> data = yaml.load(new FileInputStream("api.yml"));
```

**Alternativa considerada:** Swagger Parser (más pesada)

---

### 2. Jackson 2.16.1
**Propósito:** Serialización/deserialización JSON y YAML
```gradle
implementation 'com.fasterxml.jackson.core:jackson-databind:2.16.1'
implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1'
```

**Uso:**
```java
ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
ApiContract contract = mapper.readValue(file, ApiContract.class);
```

**Ventajas:**
- Manejo robusto de tipos
- Anotaciones para personalizar parseo
- Rendimiento optimizado

---

### 3. org.json 20231013
**Propósito:** Manipulación de JSON para requests a Ollama
```gradle
implementation 'org.json:json:20231013'
```

**Uso:**
```java
JsonNode jsonResponse = mapper.readTree(response);
String enrichedScenario = jsonResponse.get("response").asText();
```

---

### 4. SLF4J + Logback
**Propósito:** Logging estructurado
```gradle
implementation 'org.slf4j:slf4j-api:2.0.11'
implementation 'ch.qos.logback:logback-classic:1.4.14'
```

**Uso:**
```java
private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
LOGGER.info("✅ Contrato parseado: {} endpoints", count);
```

**Niveles configurados:**
- `INFO`: Progreso principal
- `DEBUG`: Detalles técnicos
- `WARN`: Advertencias
- `ERROR`: Errores críticos

---

### 5. Lombok 1.18.30 (Opcional)
**Propósito:** Reducir boilerplate
```gradle
compileOnly 'org.projectlombok:lombok:1.18.30'
annotationProcessor 'org.projectlombok:lombok:1.18.30'
```

**Anotaciones útiles:**
```java
@Data
@Builder
@Slf4j
public class Endpoint {
    private String path;
    private HttpMethod method;
}
```

**Estado:** Opcional, no se usa en versión actual

---

## 🤖 Integración con IA

### Ollama
**Versión:** Latest  
**Rol:** Servidor de modelos LLM local

**Instalación:**
```bash
# Linux/Mac
curl -fsSL https://ollama.com/install.sh | sh

# Windows
# Descargar desde https://ollama.com/download
```

**Modelo usado:** Mistral  
**Endpoint:** `http://localhost:11434/api/generate`

**Request estructura:**
```json
{
  "model": "mistral",
  "prompt": "Mejora este escenario...",
  "stream": false,
  "options": {
    "temperature": 0.7,
    "top_p": 0.9,
    "top_k": 40
  }
}
```

**Response estructura:**
```json
{
  "model": "mistral",
  "created_at": "2025-01-20T10:30:00Z",
  "response": "Scenario: ...",
  "done": true
}
```

---

### Mistral
**Versión:** Latest  
**Tamaño:** ~4GB  
**Contexto:** 8k tokens

**Instalación:**
```bash
ollama pull mistral
```

**Características:**
- Ejecución 100% local
- Sin costos de API
- Privacidad garantizada
- Respuestas en ~5-10 segundos

**Alternativas futuras:**
- Gemini (cloud, más potente)
- Llama 2 (más rápido)
- CodeLlama (especializado en código)

---

## 🧪 Testing

### JUnit 5
**Versión:** 5.10.1
```gradle
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
```

**Ejemplo:**
```java
@Test
void testParseBasicContract() {
    YamlContractParser parser = new YamlContractParser();
    ApiContract contract = parser.parse("test-api.yml");

    assertEquals("Test API", contract.getTitle());
    assertEquals(3, contract.getEndpoints().size());
}
```

---

## 🌐 Protocolos y Formatos

### OpenAPI 3.0
**Propósito:** Especificación de contratos API

**Estructura soportada:**
```yaml
openapi: 3.0.0
info:
  title: API Name
  version: 1.0.0
servers:
  - url: https://api.example.com
paths:
  /endpoint:
    post:
      summary: Description
      requestBody: ...
      responses: ...
```

**Componentes soportados:**
- ✅ paths
- ✅ parameters (path, query, header)
- ✅ requestBody
- ✅ responses
- ✅ schemas (object, array, primitivos)
- ❌ security (futuro)
- ❌ callbacks (futuro)

---

### Karate DSL
**Versión framework objetivo:** 1.4.0+

**Sintaxis generada:**
```gherkin
Feature: API Tests

Background:
  * def baseUrl = 'https://api.example.com'

Scenario: Test name
  Given url baseUrl + '/endpoint'
  And request { field: 'value' }
  When method POST
  Then status 200
  And match response.id == '#uuid'
```

**Validadores soportados:**
- `#string`, `#number`, `#boolean`
- `#uuid`, `#email`
- `#array`, `#object`
- `#present`, `#null`

---

## 🔄 Integración Continua (Futuro)

### GitHub Actions (Planeado)
```yaml
name: Generate Features
on: [push]
jobs:
  generate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Java
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Generate features
        run: ./gradlew run
      - name: Commit generated files
        run: |
          git add src/test/resources/features/
          git commit -m "Auto-generate features"
```

---

## 📊 Comparación de Tecnologías

### Parser YAML

| Librería | Pros | Contras | Elegida |
|----------|------|---------|---------|
| SnakeYAML | Ligera, rápida | Menos features | ✅ |
| Jackson YAML | Potente, typesafe | Más compleja | ✅ (complementaria) |
| Swagger Parser | OpenAPI nativo | Muy pesada | ❌ |

### IA Local

| Tecnología | Pros | Contras | Elegida |
|-----------|------|---------|---------|
| Ollama | Fácil setup, varios modelos | Requiere recursos | ✅ |
| LM Studio | UI amigable | Más manual | ❌ |
| LocalAI | API compatible OpenAI | Setup complejo | ❌ |

### Modelos LLM

| Modelo | Tamaño | Velocidad | Calidad | Elegido |
|--------|--------|-----------|---------|---------|
| Mistral | 4GB | Rápido | Excelente | ✅ |
| Llama 2 7B | 3.8GB | Muy rápido | Buena | ⚪ |
| CodeLlama | 3.8GB | Muy rápido | Buena (código) | ⚪ |
| Phi-2 | 1.7GB | Ultra rápido | Moderada | ❌ |

---

## 🔐 Seguridad

### Datos Sensibles
- ❌ No se envían datos fuera del localhost (con Ollama)
- ✅ Contratos permanecen locales
- ✅ No se almacenan logs de IA

### API Keys
- Solo necesaria para Gemini (futuro)
- Se lee de variables de entorno
- Nunca en código fuente

---

## 🎯 Requisitos del Sistema

### Mínimos
- **Java:** 17+
- **RAM:** 2GB
- **Disk:** 500MB (sin Ollama)

### Recomendados (con IA)
- **Java:** 17+
- **RAM:** 8GB
- **Disk:** 10GB (Ollama + Mistral)
- **GPU:** Opcional (acelera IA)

---

## 📚 Referencias

- [OpenAPI Specification](https://spec.openapis.org/oas/v3.0.0)
- [Karate Framework](https://github.com/karatelabs/karate)
- [Ollama Documentation](https://github.com/ollama/ollama)
- [Mistral AI](https://mistral.ai/)
- [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml)
- [Jackson](https://github.com/FasterXML/jackson)