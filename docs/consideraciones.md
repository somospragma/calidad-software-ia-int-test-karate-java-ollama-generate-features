# Consideraciones Importantes

## ⚠️ Limitaciones Actuales

### 1. OpenAPI 3.0 Únicamente
**Descripción:** Solo soporta OpenAPI versión 3.0.x

**Impacto:**
- ❌ No funciona con Swagger 2.0
- ❌ No funciona con OpenAPI 3.1

**Workaround:**
```bash
# Convertir Swagger 2.0 a OpenAPI 3.0
npx swagger2openapi swagger.json -o openapi.yml
```

**Roadmap:** Soporte para 3.1 en versión 1.1

---

### 2. Schemas Complejos

**No soportado actualmente:**
- `allOf`, `oneOf`, `anyOf`
- Schemas recursivos
- Referencias circulares
- Discriminators

**Ejemplo NO soportado:**
```yaml
schema:
  oneOf:
    - $ref: '#/components/schemas/Cat'
    - $ref: '#/components/schemas/Dog'
```

**Workaround:** Simplificar schema o expandir manualmente

---

### 3. Autenticación

**Soporte actual:**
- ✅ Bearer tokens (detecta header `Authorization`)
- ❌ OAuth2 flows
- ❌ API Keys
- ❌ Basic Auth

**Generado:**
```gherkin
# Solo si detecta Authorization en parameters
And header Authorization = 'Bearer ' + token
```

**Roadmap:** Soporte completo en versión 1.2

---

### 4. Ollama Requerido para IA

**Requisito:** Ollama debe estar corriendo localmente

**Problemas comunes:**
```bash
# Error: Connection refused
❌ Error al comunicarse con Ollama
💡 Verifica que Ollama esté corriendo: ollama serve
```

**Solución:**
```bash
# Terminal 1: Iniciar Ollama
ollama serve

# Terminal 2: Verificar
curl http://localhost:11434/api/tags
```

**Alternativa:** Deshabilitar IA
```properties
UseIA=false
```

---

## 🚨 Casos Edge a Considerar

### 1. Contratos Grandes

**Problema:** Contratos con 50+ endpoints generan 200+ escenarios

**Impacto:**
- Tiempo de generación: ~2-3 minutos
- Archivo .feature muy grande (>5000 líneas)
- Ejecución lenta si se corren todos

**Solución:**
```properties
# Deshabilitar algunas estrategias
GenerateEdgeCases=false
GenerateErrorCases=false
```

O dividir por tags:
```bash
# Solo críticos
mvn test -Dkarate.options="--tags @smoke"
```

---

### 2. Datos de Prueba

**Limitación:** El generador usa datos genéricos

**Ejemplo generado:**
```gherkin
* def requestBody = { username: 'test-value', age: 123 }
```

**Problema:** No siempre realista

**Solución futura:** Integración con Faker
```gherkin
* def requestBody = { username: faker.name(), age: faker.age(18, 65) }
```

---

### 3. Timeouts de IA

**Problema:** Mistral puede tardar 10-30 segundos por escenario

**Configuración actual:**
```properties
OllamaConnectTimeout=100000  # 100s
OllamaReadTimeout=300000     # 300s
```

**Con 30 escenarios:**
- Tiempo total: 5-15 minutos
- Solo en primera ejecución

**Recomendación:**
- Generar sin IA primero
- Luego enriquecer escenarios críticos

---

### 4. Formato de Respuestas

**Asunción:** Todas las respuestas 2xx son JSON

**Problema:** Si API retorna XML, texto, binario

**Ejemplo NO soportado:**
```yaml
responses:
  '200':
    content:
      application/xml:
        schema: ...
```

**Workaround:** Editar .feature manualmente después

---

## 💡 Mejores Prácticas

### 1. Organización de Contratos
```
src/main/resources/contracts/
├── users-api.yml          ← Un feature por contrato
├── products-api.yml
└── orders-api.yml
```

**No mezclar:** Múltiples servicios en un contrato

---

### 2. Configuración por Ambiente
```properties
# Development
ContractPath=src/main/resources/contracts/api-dev.yml
OutputPath=src/test/resources/features/dev/

# Production
ContractPath=src/main/resources/contracts/api-prod.yml
OutputPath=src/test/resources/features/prod/
```

---

### 3. Versionado de Features
```bash
# Generar con timestamp
OutputPath=features/api-v1.0-20250120.feature

# O con git hash
git rev-parse --short HEAD  # a1b2c3d
# → api-a1b2c3d.feature
```

**Ventaja:** Historial de cambios en contrato

---

### 4. Review de Features Generados

⚠️ **IMPORTANTE:** Siempre revisar features generados antes de commit

**Checklist:**
- [ ] Datos de prueba son válidos
- [ ] Assertions son correctas
- [ ] No hay información sensible
- [ ] Tags son apropiados
- [ ] Background es correcto

---

## 🔒 Seguridad y Privacidad

### 1. Datos Sensibles en Contratos

**Riesgo:** Ejemplos con datos reales
```yaml
# ❌ MAL
properties:
  email:
    example: juan.perez@pragma.com.co  # Email real!

# ✅ BIEN
properties:
  email:
    example: user@example.com
```

---

### 2. Ollama y Privacidad

**Importante:**
- ✅ Todo permanece local
- ✅ No se envían datos a internet
- ✅ Mistral corre en tu máquina

**Si usas Gemini (futuro):**
- ⚠️ Los contratos se envían a Google
- ⚠️ No usar con datos sensibles

---

### 3. Logs

**Cuidado con:**
```java
LOGGER.debug("Request body: {}", requestBody);  // Puede tener datos sensibles
```

**Solución:** Sanitizar logs en producción
```xml
<!-- logback.xml -->
<root level="INFO">  <!-- No DEBUG en prod -->
```

---

## 🐛 Troubleshooting

### Error 1: "Could not parse YAML"

**Causa:** Formato YAML inválido

**Solución:**
```bash
# Validar YAML
yamllint contract.yml

# O online
https://www.yamllint.com/
```

---

### Error 2: "No endpoints found"

**Causa:** Estructura OpenAPI incorrecta

**Verificar:**
```yaml
# Debe tener esta estructura
openapi: 3.0.0
info: ...
paths:
  /endpoint:
    get: ...  # Debe ser método HTTP válido
```

---

### Error 3: "Timeout connecting to Ollama"

**Causa:** Ollama no está corriendo

**Solución:**
```bash
# Ver si está corriendo
ps aux | grep ollama

# Si no:
ollama serve

# Si sigue fallando:
lsof -i :11434  # Ver si puerto está ocupado
```

---

### Error 4: Features se sobrescriben

**Causa:** Mismo OutputPath

**Solución:**
```properties
# Usar nombres únicos
OutputPath=features/users-api.feature
OutputPath=features/products-api.feature
```

---

## 📊 Métricas y Rendimiento

### Tiempos Esperados

| Operación | Sin IA | Con IA |
|-----------|--------|--------|
| Parseo YAML (1-5 endpoints) | <1s | <1s |
| Generación escenarios (20) | ~2s | ~2s |
| Enriquecimiento IA (20) | N/A | 3-10 min |
| Escritura archivo | <1s | <1s |
| **Total** | **~3s** | **3-10 min** |

### Recursos

| Recurso | Sin IA | Con IA |
|---------|--------|--------|
| RAM | 512MB | 2-4GB |
| CPU | 1 core | 2-4 cores |
| Disco | 100MB | 5GB+ |

---

## 🔄 Mantenimiento

### Actualizar Dependencias
```bash
# Ver versiones disponibles
./gradlew dependencyUpdates

# Actualizar en build.gradle
implementation 'org.yaml:snakeyaml:2.3'  # Nueva versión
```

### Actualizar Mistral
```bash
ollama pull mistral  # Descarga última versión
ollama list          # Ver versiones instaladas
```

---

## 📞 Soporte

### Issues Conocidos

Consultar: [GitHub Issues](https://github.com/pragma/contract-to-feature-generator/issues)

### Reportar Bugs

Incluir:
1. Versión de Java (`java -version`)
2. Versión de Gradle (`./gradlew --version`)
3. Contrato YAML (sanitizado)
4. Logs completos (`logs/generator.log`)
5. config.properties (sin API keys)

---

## 🚀 Recomendaciones para Producción

1. ✅ Ejecutar en pipeline CI/CD
2. ✅ Versionar features generados
3. ✅ Deshabilitar IA para velocidad
4. ✅ Usar cache de Gradle
5. ✅ Monitorear tiempo de generación
6. ⚠️ No incluir contratos sensibles
7. ⚠️ Review manual de features críticos