package org.example.generator.strategy;

import org.example.contract.model.Endpoint;
import org.example.generator.model.KarateScenario;

import java.util.List;

public interface ScenarioStrategy {
    List<KarateScenario> generateScenarios(Endpoint endpoint);
}