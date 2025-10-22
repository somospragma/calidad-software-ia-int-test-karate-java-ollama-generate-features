package org.example.ia;

import org.example.contract.model.*;
import org.example.generator.model.KarateScenario;
import org.example.ia.mistral.MistralEnricher;
import org.example.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ScenarioEnricher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioEnricher.class);
    private static final String IA_PROVIDER = ConfigReader.getPropertyByKey("IA");
    private static final boolean USE_IA = Boolean.parseBoolean(
            ConfigReader.getPropertyByKey("UseIA")
    );

    private final MistralEnricher mistralEnricher;
    private final PromptBuilder promptBuilder;

    public ScenarioEnricher() {
        this.mistralEnricher = new MistralEnricher();
        this.promptBuilder = new PromptBuilder();
    }

    public List<KarateScenario> enrich(List<KarateScenario> scenarios, ApiContract contract) {
        if (!USE_IA) {
            LOGGER.info("⏭️  Enriquecimiento con IA deshabilitado");
            return scenarios;
        }

        LOGGER.info("🤖 Iniciando enriquecimiento con IA ({})", IA_PROVIDER);

        List<KarateScenario> enrichedScenarios = new ArrayList<>();
        int count = 0;

        for (KarateScenario scenario : scenarios) {
            count++;
            LOGGER.info("  Procesando {}/{}: {}", count, scenarios.size(), scenario.getName());

            try {
                KarateScenario enriched = enrichSingleScenario(scenario, contract);
                enrichedScenarios.add(enriched);
            } catch (Exception e) {
                LOGGER.warn("  ⚠️  Error al enriquecer escenario, usando original: {}",
                        e.getMessage());
                enrichedScenarios.add(scenario);
            }
        }

        LOGGER.info("✅ Enriquecimiento completado");
        return enrichedScenarios;
    }

    private KarateScenario enrichSingleScenario(KarateScenario scenario, ApiContract contract) {
        Endpoint endpoint = contract.getEndpointByOperationId(scenario.getOperationId());
        if (endpoint == null) {
            LOGGER.warn("  ⚠️  Endpoint no encontrado para operationId: {}",
                    scenario.getOperationId());
            return scenario;
        }

        // Construir prompt
        String prompt = promptBuilder.buildEnrichmentPrompt(scenario, endpoint);

        // Llamar a IA según proveedor
        String enrichedContent = switch (IA_PROVIDER.toLowerCase()) {
            case "mistral" -> {
                LOGGER.debug("  → Usando Mistral (Ollama)");
                yield mistralEnricher.enrichScenario(scenario.toKarateString(), prompt);
            }
            default -> {
                LOGGER.warn("  ⚠️  Proveedor de IA desconocido: {}", IA_PROVIDER);
                yield scenario.toKarateString();
            }
        };

        // Parsear escenario enriquecido
        return parseEnrichedScenario(enrichedContent, scenario);
    }

    private KarateScenario parseEnrichedScenario(String content, KarateScenario original) {
        KarateScenario enriched = new KarateScenario();
        enriched.setName(original.getName());
        enriched.setTags(original.getTags());
        enriched.setOperationId(original.getOperationId());

        // Parsear steps
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();

            // Identificar steps de Karate
            if (line.startsWith("Given ") || line.startsWith("And ") ||
                    line.startsWith("When ") || line.startsWith("Then ") ||
                    line.startsWith("* ")) {
                enriched.addStep(line);
            }
        }

        // Si no se pudieron parsear steps, retornar original
        if (enriched.getSteps().isEmpty()) {
            LOGGER.warn("  ⚠️  No se pudo parsear el escenario enriquecido, usando original");
            return original;
        }

        return enriched;
    }
}