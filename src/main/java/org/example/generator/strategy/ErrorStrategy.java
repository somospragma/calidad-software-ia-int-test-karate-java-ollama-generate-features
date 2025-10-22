package org.example.generator.strategy;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;

import java.util.*;

public class ErrorStrategy implements ScenarioStrategy {

    @Override
    public List<KarateScenario> generateScenarios(Endpoint endpoint) {
        List<KarateScenario> scenarios = new ArrayList<>();

        // Generar escenarios para cada código de error definido
        for (Map.Entry<Integer, Response> entry : endpoint.getResponses().entrySet()) {
            int statusCode = entry.getKey();

            // Solo códigos de error (4xx, 5xx)
            if (statusCode >= 400) {
                scenarios.add(createErrorScenario(endpoint, statusCode, entry.getValue()));
            }
        }

        return scenarios;
    }

    private KarateScenario createErrorScenario(Endpoint endpoint,
                                               int statusCode,
                                               Response response) {
        KarateScenario scenario = new KarateScenario();

        String errorType = getErrorType(statusCode);
        scenario.setName(endpoint.getSummary() + " - Error " + statusCode + " " + errorType);
        scenario.setOperationId(endpoint.getOperationId());
        scenario.addTag("@error");
        scenario.addTag("@regression");

        scenario.addStep("Given url baseUrl + '" + endpoint.getPath() + "'");

        // Configurar condiciones para provocar el error
        if (statusCode == 401) {
            scenario.addStep("And header Authorization = 'Bearer invalid-token'");
        } else if (endpoint.needsAuth()) {
            scenario.addStep("And header Authorization = 'Bearer ' + token");
        }

        if (statusCode == 404) {
            // Usar ID inexistente
            scenario.addStep("And path 'id' = 'nonexistent-id'");
        }

        if (statusCode == 409 && endpoint.hasRequestBody()) {
            // Duplicado - usar datos existentes
            scenario.addStep("* def requestBody = existingData");
            scenario.addStep("And request requestBody");
        }

        scenario.addStep("When method " + endpoint.getMethod());
        scenario.addStep("Then status " + statusCode);

        if (response.getDescription() != null) {
            scenario.addStep("And match response.message == '#string'");
        }

        return scenario;
    }

    private String getErrorType(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 422 -> "Unprocessable Entity";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
    }
}