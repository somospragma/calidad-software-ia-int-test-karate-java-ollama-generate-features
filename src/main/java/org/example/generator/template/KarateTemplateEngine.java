package org.example.generator.template;

import org.example.contract.model.ApiContract;
import org.example.generator.model.KarateScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KarateTemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(KarateTemplateEngine.class);

    public String generateFeature(ApiContract contract, List<KarateScenario> scenarios) {
        LOGGER.info("📝 Generando archivo .feature");

        StringBuilder feature = new StringBuilder();

        // Feature header con tags
        String featureTag = "@" + sanitizeTag(contract.getTitle());
        feature.append(featureTag).append("\n");
        feature.append("Feature: ").append(contract.getTitle()).append("\n");

        if (contract.getDescription() != null) {
            feature.append("  ").append(contract.getDescription()).append("\n");
        }
        feature.append("\n");

        // Background
        feature.append(generateBackground(contract));
        feature.append("\n");

        // Scenarios
        for (KarateScenario scenario : scenarios) {
            feature.append(scenario.toKarateString());
            feature.append("\n");
        }

        LOGGER.info("Feature generado: {} escenarios", scenarios.size());
        return feature.toString();
    }

    private String generateBackground(ApiContract contract) {
        StringBuilder bg = new StringBuilder();

        bg.append("  Background:\n");
        bg.append("    * def config = read('classpath:karate-config.js')\n");
        bg.append("    * def baseUrl = '").append(contract.getBaseUrl()).append("'\n");
        bg.append("    * def token = Java.type('utils.TokenGenerator').getToken()\n");
        bg.append("    * header Content-Type = 'application/json'\n");
        bg.append("    * header Accept = 'application/json'\n");

        return bg.toString();
    }

    private String sanitizeTag(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}