package org.example.generator;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;
import org.example.generator.strategy.*;
import org.example.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ScenarioGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioGenerator.class);
    private final List<ScenarioStrategy> strategies;

    public ScenarioGenerator() {
        this.strategies = new ArrayList<>();

        if (Boolean.parseBoolean(ConfigReader.getPropertyByKey("GenerateHappyPath"))) {
            strategies.add(new HappyPathStrategy());
        }

        if (Boolean.parseBoolean(ConfigReader.getPropertyByKey("GenerateValidations"))) {
            strategies.add(new ValidationStrategy());
        }

        if (Boolean.parseBoolean(ConfigReader.getPropertyByKey("GenerateErrorCases"))) {
            strategies.add(new ErrorStrategy());
        }

        if (Boolean.parseBoolean(ConfigReader.getPropertyByKey("GenerateEdgeCases"))) {
            strategies.add(new EdgeCaseStrategy());
        }
    }

    public List<KarateScenario> generate(Endpoint endpoint) {
        LOGGER.info("🔨 Generando escenarios para: {} {}",
                endpoint.getMethod(), endpoint.getPath());

        List<KarateScenario> allScenarios = new ArrayList<>();

        for (ScenarioStrategy strategy : strategies) {
            List<KarateScenario> scenarios = strategy.generateScenarios(endpoint);
            allScenarios.addAll(scenarios);

            LOGGER.debug("  ✓ {}: {} escenarios",
                    strategy.getClass().getSimpleName(), scenarios.size());
        }

        return allScenarios;
    }

    public List<KarateScenario> generateAll(ApiContract contract) {
        List<KarateScenario> allScenarios = new ArrayList<>();

        for (Endpoint endpoint : contract.getEndpoints()) {
            allScenarios.addAll(generate(endpoint));
        }

        return allScenarios;
    }
}