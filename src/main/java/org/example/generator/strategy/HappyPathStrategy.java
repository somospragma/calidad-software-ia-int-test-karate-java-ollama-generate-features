package org.example.generator.strategy;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HappyPathStrategy implements ScenarioStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HappyPathStrategy.class);

    @Override
    public List<KarateScenario> generateScenarios(Endpoint endpoint) {
        LOGGER.debug("Generando Happy Path para: {} {}", endpoint.getMethod(), endpoint.getPath());

        KarateScenario scenario = new KarateScenario();
        scenario.setName(endpoint.getSummary() + " - Happy Path");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@smoke");
        scenario.addTag("@happyPath");
        scenario.addTag("@regression");

        // URL base
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        // Headers de autenticación
        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        // Path parameters
        for (Parameter param : endpoint.getPathParams()) {
            String value = generateValidValue(param);
            scenario.addStep("And path '" + param.getName() + "' = " + value);
        }

        // Query parameters
        for (Parameter param : endpoint.getQueryParams()) {
            String value = generateValidValue(param);
            scenario.addStep("And param '" + param.getName() + "' = " + value);
        }

        // Request body
        if (endpoint.hasRequestBody()) {
            String body = generateValidBody(endpoint.getRequestBody());
            scenario.addStep("* def requestBody = " + body);
            scenario.addStep("And request requestBody");
        }

        // Método HTTP
        scenario.addStep("When method " + endpoint.getMethod());

        // Assertions de respuesta exitosa
        Response successResponse = findSuccessResponse(endpoint);
        if (successResponse != null) {
            scenario.addStep("Then status " + successResponse.getStatusCode());

            if (successResponse.getSchema() != null) {
                generateAssertions(successResponse.getSchema(), scenario, "response");
            }
        } else {
            scenario.addStep("Then status 200");
        }

        return List.of(scenario);
    }

    private Response findSuccessResponse(Endpoint endpoint) {
        // Buscar respuesta 200, 201, etc.
        for (int code : new int[]{200, 201, 204}) {
            Response response = endpoint.getResponse(code);
            if (response != null) return response;
        }
        return null;
    }

    private String generateValidValue(Parameter param) {
        if (param.getExample() != null) {
            return formatValue(param.getExample(), param.getType());
        }

        return switch (param.getType()) {
            case "string" -> {
                if ("uuid".equals(param.getFormat())) {
                    yield "'123e4567-e89b-12d3-a456-426614174000'";
                } else if ("email".equals(param.getFormat())) {
                    yield "'user@example.com'";
                } else {
                    yield "'test-value'";
                }
            }
            case "integer", "number" -> "123";
            case "boolean" -> "true";
            default -> "'test'";
        };
    }

    private String generateValidBody(Schema schema) {
        if (schema.getExample() != null) {
            return formatJsonObject(schema.getExample());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n    \"\"\"\n    {\n");

        if ("object".equals(schema.getType()) && schema.getProperties() != null) {
            List<String> lines = new ArrayList<>();

            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                String fieldName = entry.getKey();
                Schema fieldSchema = entry.getValue();
                String value = generateValidFieldValue(fieldSchema);
                lines.add("      \"" + fieldName + "\": " + value);
            }

            sb.append(String.join(",\n", lines));
        }

        sb.append("\n    }\n    \"\"\"");
        return sb.toString();
    }

    private String generateValidFieldValue(Schema schema) {
        if (schema.getExample() != null) {
            return formatValue(schema.getExample(), schema.getType());
        }

        return switch (schema.getType()) {
            case "string" -> {
                if ("email".equals(schema.getFormat())) {
                    yield "\"user@example.com\"";
                } else if ("uuid".equals(schema.getFormat())) {
                    yield "\"123e4567-e89b-12d3-a456-426614174000\"";
                } else if ("date-time".equals(schema.getFormat())) {
                    yield "\"2025-01-20T10:30:00Z\"";
                } else {
                    yield "\"test-value\"";
                }
            }
            case "integer", "number" -> "123";
            case "boolean" -> "true";
            case "array" -> "[]";
            case "object" -> "{}";
            default -> "\"test\"";
        };
    }

    private void generateAssertions(Schema schema, KarateScenario scenario, String path) {
        if (schema == null || schema.getProperties() == null) return;

        for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
            String fieldName = entry.getKey();
            Schema fieldSchema = entry.getValue();
            String fullPath = path + "." + fieldName;

            // Assertion básica de tipo
            String karateType = getKarateType(fieldSchema);
            scenario.addStep("And match " + fullPath + " == " + karateType);

            // Validaciones adicionales según formato
            if (fieldSchema.getFormat() != null) {
                switch (fieldSchema.getFormat()) {
                    case "email":
                        scenario.addStep("And match " + fullPath + " == '#email'");
                        break;
                    case "uuid":
                        scenario.addStep("And match " + fullPath + " == '#uuid'");
                        break;
                }
            }
        }
    }

    private String getKarateType(Schema schema) {
        return switch (schema.getType()) {
            case "string" -> "'#string'";
            case "integer", "number" -> "'#number'";
            case "boolean" -> "'#boolean'";
            case "array" -> "'#array'";
            case "object" -> "'#object'";
            default -> "'#present'";
        };
    }

    private String formatValue(Object value, String type) {
        if ("string".equals(type)) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }

    private String formatJsonObject(Object obj) {
        // Simplificado - en producción usar Jackson
        return "\n    \"\"\"\n    " + obj.toString() + "\n    \"\"\"";
    }
}