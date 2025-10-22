package org.example.ia;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;

public class PromptBuilder {

    public String buildEnrichmentPrompt(KarateScenario scenario, Endpoint endpoint) {
        StringBuilder sb = new StringBuilder();

        sb.append("Eres un experto en testing de APIs y en el framework Karate.\n\n");
        sb.append("ENDPOINT:\n");
        sb.append("- Path: ").append(endpoint.getPath()).append("\n");
        sb.append("- Método: ").append(endpoint.getMethod()).append("\n");
        sb.append("- Descripción: ").append(endpoint.getSummary()).append("\n\n");

        sb.append("ESCENARIO BASE:\n");
        sb.append(scenario.toKarateString()).append("\n\n");

        sb.append("TAREA:\n");
        sb.append("Mejora este escenario de Karate siguiendo estas reglas:\n\n");

        sb.append("1. VALIDACIONES: Añade match para todos los campos críticos de la respuesta\n");
        sb.append("   Ejemplo: And match response.id == '#string'\n\n");

        sb.append("2. TIPOS DE DATOS: Usa los validadores de Karate apropiadamente\n");
        sb.append("   - '#string' para strings\n");
        sb.append("   - '#number' para números\n");
        sb.append("   - '#boolean' para booleanos\n");
        sb.append("   - '#array' para arrays\n");
        sb.append("   - '#uuid' para UUIDs\n");
        sb.append("   - '#email' para emails\n\n");

        sb.append("3. ASSERTIONS: Añade validaciones específicas según el schema\n");
        sb.append("   Ejemplo: And match response.items == '#[3]' // array con 3 elementos\n\n");

        sb.append("4. LOGS: Si es un escenario importante, añade logs útiles\n");
        sb.append("   Ejemplo: And print 'User created with ID:', response.id\n\n");

        sb.append("5. VARIABLES: Captura IDs u otros valores para uso posterior si es necesario\n");
        sb.append("   Ejemplo: * def userId = response.id\n\n");

        sb.append("RESTRICCIONES:\n");
        sb.append("- NO cambies la estructura general del escenario\n");
        sb.append("- NO inventes campos que no existen en el endpoint\n");
        sb.append("- NO uses sintaxis que no sea válida en Karate\n");
        sb.append("- Devuelve SOLO el escenario mejorado, sin explicaciones\n");
        sb.append("- NO uses bloques de código markdown (```), solo el contenido\n\n");

        sb.append("FORMATO DE SALIDA:\n");
        sb.append("Devuelve el escenario completo desde 'Scenario:' hasta el último step.\n");

        return sb.toString();
    }

    public String buildValidationPrompt(Endpoint endpoint) {
        StringBuilder sb = new StringBuilder();

        sb.append("Genera escenarios de validación para este endpoint:\n\n");
        sb.append("ENDPOINT: ").append(endpoint.getMethod()).append(" ").append(endpoint.getPath()).append("\n\n");

        if (endpoint.hasRequestBody()) {
            sb.append("REQUEST BODY:\n");
            Schema schema = endpoint.getRequestBody();
            sb.append(formatSchema(schema)).append("\n\n");
        }

        sb.append("Genera escenarios para validar:\n");
        sb.append("1. Campos requeridos faltantes\n");
        sb.append("2. Campos vacíos\n");
        sb.append("3. Tipos de datos incorrectos\n");
        sb.append("4. Formatos inválidos (email, uuid, etc.)\n\n");

        sb.append("FORMATO:\n");
        sb.append("Devuelve 3-5 escenarios en formato Karate.\n");
        sb.append("Cada escenario debe tener tags @validation y @regression\n");
        sb.append("NO uses markdown, solo contenido Karate.\n");

        return sb.toString();
    }

    private String formatSchema(Schema schema) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo: ").append(schema.getType()).append("\n");

        if (schema.getRequired() != null && !schema.getRequired().isEmpty()) {
            sb.append("Campos requeridos: ")
                    .append(String.join(", ", schema.getRequired()))
                    .append("\n");
        }

        if (schema.getProperties() != null) {
            sb.append("Propiedades:\n");
            schema.getProperties().forEach((name, prop) -> {
                sb.append("  - ").append(name)
                        .append(": ").append(prop.getType());

                if (prop.getFormat() != null) {
                    sb.append(" (format: ").append(prop.getFormat()).append(")");
                }
                if (prop.getMinLength() != null) {
                    sb.append(" (minLength: ").append(prop.getMinLength()).append(")");
                }
                if (prop.getMaxLength() != null) {
                    sb.append(" (maxLength: ").append(prop.getMaxLength()).append(")");
                }
                sb.append("\n");
            });
        }

        return sb.toString();
    }
}