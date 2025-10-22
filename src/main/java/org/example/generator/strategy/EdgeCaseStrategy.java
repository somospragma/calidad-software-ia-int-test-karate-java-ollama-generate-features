package org.example.generator.strategy;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;

import java.util.*;

public class EdgeCaseStrategy implements ScenarioStrategy {

    @Override
    public List<KarateScenario> generateScenarios(Endpoint endpoint) {
        List<KarateScenario> scenarios = new ArrayList<>();

        if (!endpoint.hasRequestBody()) {
            return scenarios;
        }

        Schema schema = endpoint.getRequestBody();

        if (schema.getProperties() != null) {
            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                String fieldName = entry.getKey();
                Schema fieldSchema = entry.getValue();

                // Generar edge cases según tipo y restricciones
                scenarios.addAll(generateFieldEdgeCases(endpoint, fieldName, fieldSchema, schema));
            }
        }

        return scenarios;
    }

    private List<KarateScenario> generateFieldEdgeCases(Endpoint endpoint,
                                                        String fieldName,
                                                        Schema fieldSchema,
                                                        Schema fullSchema) {
        List<KarateScenario> scenarios = new ArrayList<>();

        if ("string".equals(fieldSchema.getType())) {
            // String muy largo
            if (fieldSchema.getMaxLength() != null) {
                scenarios.add(createMaxLengthScenario(endpoint, fieldName,
                        fieldSchema, fullSchema));
            }

            // String con caracteres especiales
            scenarios.add(createSpecialCharsScenario(endpoint, fieldName, fullSchema));
        }

        if ("integer".equals(fieldSchema.getType()) || "number".equals(fieldSchema.getType())) {
            // Valor mínimo/máximo
            if (fieldSchema.getMinimum() != null) {
                scenarios.add(createMinValueScenario(endpoint, fieldName,
                        fieldSchema, fullSchema));
            }
            if (fieldSchema.getMaximum() != null) {
                scenarios.add(createMaxValueScenario(endpoint, fieldName,
                        fieldSchema, fullSchema));
            }

            // Valor negativo
            scenarios.add(createNegativeValueScenario(endpoint, fieldName, fullSchema));
        }

        return scenarios;
    }

    private KarateScenario createMaxLengthScenario(Endpoint endpoint,
                                                   String fieldName,
                                                   Schema fieldSchema,
                                                   Schema fullSchema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar " + fieldName + " en longitud máxima");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@edgeCase");
        scenario.addTag("@regression");

        // Generar string de longitud máxima
        int maxLength = fieldSchema.getMaxLength();
        String longString = "a".repeat(maxLength);

        String body = generateBodyWithFieldValue(fullSchema, fieldName, "\"" + longString + "\"");

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 200");

        return scenario;
    }

    private KarateScenario createSpecialCharsScenario(Endpoint endpoint,
                                                      String fieldName,
                                                      Schema fullSchema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar " + fieldName + " con caracteres especiales");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@edgeCase");
        scenario.addTag("@regression");

        String specialChars = "!@#$%^&*()";
        String body = generateBodyWithFieldValue(fullSchema, fieldName,
                "\"" + specialChars + "\"");

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 400");

        return scenario;
    }

    private KarateScenario createMinValueScenario(Endpoint endpoint,
                                                  String fieldName,
                                                  Schema fieldSchema,
                                                  Schema fullSchema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar " + fieldName + " en valor mínimo");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@edgeCase");
        scenario.addTag("@regression");

        String minValue = String.valueOf(fieldSchema.getMinimum());
        String body = generateBodyWithFieldValue(fullSchema, fieldName, minValue);

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 200");

        return scenario;
    }

    private KarateScenario createMaxValueScenario(Endpoint endpoint,
                                                  String fieldName,
                                                  Schema fieldSchema,
                                                  Schema fullSchema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar " + fieldName + " en valor máximo");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@edgeCase");
        scenario.addTag("@regression");

        String maxValue = String.valueOf(fieldSchema.getMaximum());
        String body = generateBodyWithFieldValue(fullSchema, fieldName, maxValue);

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 200");

        return scenario;
    }

    private KarateScenario createNegativeValueScenario(Endpoint endpoint,
                                                       String fieldName,
                                                       Schema fullSchema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar " + fieldName + " con valor negativo");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@edgeCase");
        scenario.addTag("@regression");

        String body = generateBodyWithFieldValue(fullSchema, fieldName, "-1");

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 400");

        return scenario;
    }

    private String generateBodyWithFieldValue(Schema schema, String fieldName, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n    \"\"\"\n    {\n");

        if (schema.getProperties() != null) {
            List<String> lines = new ArrayList<>();

            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                String name = entry.getKey();
                Schema fieldSchema = entry.getValue();

                String fieldValue;
                if (name.equals(fieldName)) {
                    fieldValue = value;
                } else {
                    fieldValue = generateDefaultValue(fieldSchema);
                }

                lines.add("      \"" + name + "\": " + fieldValue);
            }

            sb.append(String.join(",\n", lines));
        }

        sb.append("\n    }\n    \"\"\"");
        return sb.toString();
    }

    private String generateDefaultValue(Schema schema) {
        return switch (schema.getType()) {
            case "string" -> "\"test\"";
            case "integer", "number" -> "123";
            case "boolean" -> "true";
            default -> "null";
        };
    }
}