package org.example;

import org.example.contract.model.ApiContract;
import org.example.contract.parser.YamlContractParser;
import org.example.generator.ScenarioGenerator;
import org.example.generator.model.KarateScenario;
import org.example.generator.template.KarateTemplateEngine;
import org.example.ia.ScenarioEnricher;
import org.example.utils.ConfigReader;
import org.example.writer.FeatureFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("Contract-to-Feature Generator - Iniciando...\n");

            // 1. Leer configuración
            String contractPath = ConfigReader.getPropertyByKey("ContractPath");
            String outputPath = ConfigReader.getPropertyByKey("OutputPath");
            boolean useIA = Boolean.parseBoolean(ConfigReader.getPropertyByKey("UseIA"));

            LOGGER.info("📋 Configuración:");
            LOGGER.info("  - Contrato: {}", contractPath);
            LOGGER.info("  - Salida: {}", outputPath);
            LOGGER.info("  - IA: {}", useIA ? "Habilitada" : "Deshabilitada");
            LOGGER.info("");

            // 2. Parsear contrato
            LOGGER.info("📄 Paso 1/5: Parseando contrato YAML...");
            YamlContractParser parser = new YamlContractParser();
            ApiContract contract = parser.parse(contractPath);
            LOGGER.info("Contrato parseado:");
            LOGGER.info("  - Título: {}", contract.getTitle());
            LOGGER.info("  - Versión: {}", contract.getVersion());
            LOGGER.info("  - Endpoints: {}", contract.getEndpoints().size());
            LOGGER.info("");

            // 3. Generar escenarios base
            LOGGER.info("Paso 2/5: Generando escenarios base...");
            ScenarioGenerator generator = new ScenarioGenerator();
            List<KarateScenario> scenarios = generator.generateAll(contract);
            LOGGER.info("Escenarios generados: {}", scenarios.size());

            // Desglose por endpoint
            List<KarateScenario> finalScenarios = scenarios;
            contract.getEndpoints().forEach(endpoint -> {
                long count = finalScenarios.stream()
                        .filter(s -> endpoint.getOperationId().equals(s.getOperationId()))
                        .count();
                LOGGER.info("  - {} {}: {} escenarios",
                        endpoint.getMethod(), endpoint.getPath(), count);
            });
            LOGGER.info("");

            // 4. Enriquecer con IA
            if (useIA) {
                LOGGER.info("Paso 3/5: Enriqueciendo escenarios con IA...");
                ScenarioEnricher enricher = new ScenarioEnricher();
                scenarios = enricher.enrich(scenarios, contract);
                LOGGER.info("");
            } else {
                LOGGER.info("Paso 3/5: Saltando enriquecimiento con IA");
                LOGGER.info("");
            }

            // 5. Generar archivo .feature
            LOGGER.info("Paso 4/5: Generando archivo .feature...");
            KarateTemplateEngine templateEngine = new KarateTemplateEngine();
            String featureContent = templateEngine.generateFeature(contract, scenarios);
            LOGGER.info("");

            // 6. Escribir archivo
            LOGGER.info("Paso 5/5: Escribiendo archivo...");
            FeatureFileWriter writer = new FeatureFileWriter();
            writer.write(featureContent, outputPath);
            LOGGER.info("");

            // Resumen final
            LOGGER.info("═══════════════════════════════════════════");
            LOGGER.info("GENERACIÓN COMPLETADA EXITOSAMENTE");
            LOGGER.info("═══════════════════════════════════════════");
            LOGGER.info("Resumen:");
            LOGGER.info("  - Endpoints procesados: {}", contract.getEndpoints().size());
            LOGGER.info("  - Escenarios generados: {}", scenarios.size());
            LOGGER.info("  - Archivo: {}", outputPath);
            LOGGER.info("═══════════════════════════════════════════");

        } catch (Exception e) {
            LOGGER.error("❌ Error durante la ejecución:", e);
            System.exit(1);
        }
    }
}