package org.example.generator.strategy;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ValidationStrategy implements ScenarioStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationStrategy.class);

    @Override
    public List<KarateScenario> generateScenarios(Endpoint endpoint) {
        List<KarateScenario> scenarios = new ArrayList<>();

        // Validar campos requeridos en request body
        if (endpoint.hasRequestBody()) {
            Schema schema = endpoint.getRequestBody();

            if (schema.getRequired() != null) {
                for (String requiredField : schema.getRequired()) {
                    scenarios.add(createMissingFieldScenario(endpoint, requiredField, schema));
                }
            }

            // Validar campos vacíos
            if (schema.getProperties() != null) {
                for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                    String fieldName = entry.getKey();
                    Schema fieldSchema = entry.getValue();

                    if ("string".equals(fieldSchema.getType())) {
                        scenarios.add(createEmptyFieldScenario(endpoint, fieldName, schema));
                    }
                }
            }
        }

        // Validar parámetros requeridos
        for (Parameter param : endpoint.getParameters()) {
            if (param.isRequired()) {
                scenarios.add(createMissingParameterScenario(endpoint, param));
            }
        }

        return scenarios;
    }

    private KarateScenario createMissingFieldScenario(Endpoint endpoint,
                                                      String fieldName,
                                                      Schema schema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar error cuando falta el campo " + fieldName);
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@validation");
        scenario.addTag("@regression");

        // Construir body sin el campo requerido
        String body = generateBodyWithoutField(schema, fieldName);

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 400");
        scenario.addStep("And match response.message contains '" + fieldName + "'");

        return scenario;
    }

    private KarateScenario createEmptyFieldScenario(Endpoint endpoint,
                                                    String fieldName,
                                                    Schema schema) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar error cuando " + fieldName + " está vacío");
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@validation");
        scenario.addTag("@regression");

        // Construir body con el campo vacío
        String body = generateBodyWithEmptyField(schema, fieldName);

        scenario.addStep("* def requestBody = " + body);
        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        scenario.addStep("And request requestBody");
        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 400");
        scenario.addStep("And match response.message contains '" + fieldName + "'");

        return scenario;
    }

    private KarateScenario createMissingParameterScenario(Endpoint endpoint,
                                                          Parameter param) {
        KarateScenario scenario = new KarateScenario();
        scenario.setName("Validar error cuando falta el parámetro " + param.getName());
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@validation");
        scenario.addTag("@regression");

        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        // Agregar otros parámetros excepto el que se está probando
        for (Parameter p : endpoint.getParameters()) {
            if (!p.getName().equals(param.getName())) {
                String value = generateValidValue(p);
                if ("path".equals(p.getIn())) {
                    scenario.addStep("And path '" + p.getName() + "' = " + value);
                } else if ("query".equals(p.getIn())) {
                    scenario.addStep("And param '" + p.getName() + "' = " + value);
                }
            }
        }

        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status 400");
        scenario.addStep("And match response.message contains '" + param.getName() + "'");

        return scenario;
    }

    private String generateBodyWithoutField(Schema schema, String excludeField) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n    \"\"\"\n    {\n");

        if (schema.getProperties() != null) {
            List<String> lines = new ArrayList<>();

            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                String fieldName = entry.getKey();

                // Excluir el campo especificado
                if (fieldName.equals(excludeField)) continue;

                Schema fieldSchema = entry.getValue();
                String value = generateValidFieldValue(fieldSchema);
                lines.add("      \"" + fieldName + "\": " + value);
            }

            sb.append(String.join(",\n", lines));
        }

        sb.append("\n    }\n    \"\"\"");
        return sb.toString();
    }

    private String generateBodyWithEmptyField(Schema schema, String emptyField) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n    \"\"\"\n    {\n");

        if (schema.getProperties() != null) {
            List<String> lines = new ArrayList<>();

            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                String fieldName = entry.getKey();
                Schema fieldSchema = entry.getValue();

                String value;
                if (fieldName.equals(emptyField)) {
                    value = "\"\""; // Campo vacío
                } else {
                    value = generateValidFieldValue(fieldSchema);
                }

                lines.add("      \"" + fieldName + "\": " + value);
            }

            sb.append(String.join(",\n", lines));
        }

        sb.append("\n    }\n    \"\"\"");
        return sb.toString();
    }

    private String generateValidValue(Parameter param) {
        return switch (param.getType()) {
            case "string" -> "'test-value'";
            case "integer", "number" -> "123";
            case "boolean" -> "true";
            default -> "'test'";
        };
    }

    private String generateValidFieldValue(Schema schema) {
        return switch (schema.getType()) {
            case "string" -> "\"test-value\"";
            case "integer", "number" -> "123";
            case "boolean" -> "true";
            case "array" -> "[]";
            case "object" -> "{}";
            default -> "\"test\"";
        };
    }
}