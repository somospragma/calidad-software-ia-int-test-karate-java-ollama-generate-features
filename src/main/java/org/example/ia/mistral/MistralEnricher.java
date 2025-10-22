package org.example.ia.mistral;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MistralEnricher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MistralEnricher.class);
    private static final String OLLAMA_URI = ConfigReader.getPropertyByKey("OllamaURI");
    private static final String MODEL = ConfigReader.getPropertyByKey("OllamaModel");
    private static final int CONNECT_TIMEOUT = 100000; // 100s
    private static final int READ_TIMEOUT = 300000;    // 300s

    public String enrichScenario(String baseScenario, String prompt) {
        try {
            LOGGER.debug("🦙 Enviando request a Ollama...");

            URI uri = new URI(OLLAMA_URI);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configurar timeouts
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            // Configurar request
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construir JSON body
            Map<String, Object> jsonBody = buildRequestBody(prompt);
            String jsonRequest = new ObjectMapper().writeValueAsString(jsonBody);

            // Enviar request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Verificar status code
            int statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                LOGGER.error("❌ Ollama retornó status code: {}", statusCode);
                return baseScenario;
            }

            // Leer response
            String response = readResponse(conn);

            // Parsear respuesta
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response);
            String enrichedScenario = jsonResponse.get("response").asText();

            LOGGER.debug("✅ Respuesta recibida de Ollama");
            return enrichedScenario;

        } catch (java.net.SocketTimeoutException e) {
            LOGGER.error("⏱️  Timeout al conectar con Ollama: {}", e.getMessage());
            LOGGER.error("💡 Sugerencia: Aumenta los timeouts en config.properties o usa un modelo más pequeño");
            return baseScenario;
        } catch (java.net.ConnectException e) {
            LOGGER.error("❌ No se pudo conectar con Ollama en {}", OLLAMA_URI);
            LOGGER.error("💡 Verifica que Ollama esté corriendo: ollama serve");
            return baseScenario;
        } catch (IOException | java.net.URISyntaxException e) {
            LOGGER.error("❌ Error al comunicarse con Ollama: {}", e.getMessage());
            LOGGER.error("💡 Verifica que Ollama esté instalado y corriendo en {}", OLLAMA_URI);
            return baseScenario;
        }
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("prompt", prompt);
        body.put("stream", false);

        // Opciones para mejorar la salida
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);  // Creatividad moderada
        options.put("top_p", 0.9);
        options.put("top_k", 40);
        body.put("options", options);

        return body;
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        }
    }
}