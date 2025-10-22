package org.example.contract.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.contract.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unchecked")
public class YamlContractParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlContractParser.class);
    private final ObjectMapper yamlMapper;

    public YamlContractParser() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public ApiContract parse(String yamlPath) throws IOException {
        LOGGER.info("📄 Parseando contrato: {}", yamlPath);

        File yamlFile = new File(yamlPath);
        Map<String, Object> data = yamlMapper.readValue(yamlFile, Map.class);

        ApiContract contract = new ApiContract();

        // Info básica
        Map<String, Object> info = (Map<String, Object>) data.get("info");
        if (info != null) {
            contract.setTitle((String) info.get("title"));
            contract.setVersion((String) info.get("version"));
            contract.setDescription((String) info.get("description"));
        }

        // Base URL
        List<Map<String, Object>> servers = (List<Map<String, Object>>) data.get("servers");
        if (servers != null && !servers.isEmpty()) {
            contract.setBaseUrl((String) servers.get(0).get("url"));
        }

        // Parsear paths (endpoints)
        Map<String, Object> paths = (Map<String, Object>) data.get("paths");
        if (paths != null) {
            for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
                String path = pathEntry.getKey();
                Map<String, Object> methods = (Map<String, Object>) pathEntry.getValue();

                for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                    String method = methodEntry.getKey().toUpperCase();

                    // Ignorar si no es un método HTTP válido
                    if (!isValidHttpMethod(method)) continue;

                    Map<String, Object> spec = (Map<String, Object>) methodEntry.getValue();
                    Endpoint endpoint = parseEndpoint(path, method, spec);
                    contract.addEndpoint(endpoint);

                    LOGGER.debug("  ✓ Parseado: {} {}", method, path);
                }
            }
        }

        LOGGER.info("Contrato parseado: {} endpoints encontrados", contract.getEndpoints().size());
        return contract;
    }

    private boolean isValidHttpMethod(String method) {
        try {
            HttpMethod.valueOf(method);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Endpoint parseEndpoint(String path, String method, Map<String, Object> spec) {
        Endpoint endpoint = new Endpoint();
        endpoint.setPath(path);
        endpoint.setMethod(HttpMethod.valueOf(method));
        endpoint.setSummary((String) spec.get("summary"));
        endpoint.setDescription((String) spec.get("description"));
        endpoint.setOperationId((String) spec.get("operationId"));

        // Tags
        List<String> tags = (List<String>) spec.get("tags");
        if (tags != null) {
            endpoint.setTags(tags);
        }

        // Parámetros
        List<Map<String, Object>> parameters = (List<Map<String, Object>>) spec.get("parameters");
        if (parameters != null) {
            for (Map<String, Object> paramMap : parameters) {
                endpoint.addParameter(parseParameter(paramMap));
            }
        }

        // Request Body
        Map<String, Object> requestBody = (Map<String, Object>) spec.get("requestBody");
        if (requestBody != null) {
            endpoint.setRequestBody(parseRequestBody(requestBody));
        }

        // Responses
        Map<String, Object> responses = (Map<String, Object>) spec.get("responses");
        if (responses != null) {
            for (Map.Entry<String, Object> entry : responses.entrySet()) {
                int statusCode = Integer.parseInt(entry.getKey());
                Map<String, Object> responseSpec = (Map<String, Object>) entry.getValue();
                endpoint.addResponse(statusCode, parseResponse(responseSpec));
            }
        }

        return endpoint;
    }

    private Parameter parseParameter(Map<String, Object> paramMap) {
        Parameter param = new Parameter();
        param.setName((String) paramMap.get("name"));
        param.setIn((String) paramMap.get("in"));
        param.setDescription((String) paramMap.get("description"));

        Boolean required = (Boolean) paramMap.get("required");
        param.setRequired(required != null && required);

        // Schema del parámetro
        Map<String, Object> schema = (Map<String, Object>) paramMap.get("schema");
        if (schema != null) {
            param.setType((String) schema.get("type"));
            param.setFormat((String) schema.get("format"));
            param.setExample(schema.get("example"));

            if (schema.get("minLength") != null) {
                param.setMinLength((Integer) schema.get("minLength"));
            }
            if (schema.get("maxLength") != null) {
                param.setMaxLength((Integer) schema.get("maxLength"));
            }
        }

        return param;
    }

    private Schema parseRequestBody(Map<String, Object> requestBodyMap) {
        Map<String, Object> content = (Map<String, Object>) requestBodyMap.get("content");
        if (content == null) return null;

        // Buscar application/json
        Map<String, Object> jsonContent = (Map<String, Object>) content.get("application/json");
        if (jsonContent == null) return null;

        Map<String, Object> schemaMap = (Map<String, Object>) jsonContent.get("schema");
        return schemaMap != null ? parseSchema(schemaMap) : null;
    }

    private Response parseResponse(Map<String, Object> responseMap) {
        Response response = new Response();
        response.setDescription((String) responseMap.get("description"));

        Map<String, Object> content = (Map<String, Object>) responseMap.get("content");
        if (content != null) {
            Map<String, Object> jsonContent = (Map<String, Object>) content.get("application/json");
            if (jsonContent != null) {
                Map<String, Object> schemaMap = (Map<String, Object>) jsonContent.get("schema");
                if (schemaMap != null) {
                    response.setSchema(parseSchema(schemaMap));
                }
            }
        }

        return response;
    }

    private Schema parseSchema(Map<String, Object> schemaMap) {
        Schema schema = new Schema();
        schema.setType((String) schemaMap.get("type"));
        schema.setFormat((String) schemaMap.get("format"));
        schema.setPattern((String) schemaMap.get("pattern"));
        schema.setExample(schemaMap.get("example"));

        if (schemaMap.get("minLength") != null) {
            schema.setMinLength((Integer) schemaMap.get("minLength"));
        }
        if (schemaMap.get("maxLength") != null) {
            schema.setMaxLength((Integer) schemaMap.get("maxLength"));
        }
        if (schemaMap.get("minimum") != null) {
            schema.setMinimum((Integer) schemaMap.get("minimum"));
        }
        if (schemaMap.get("maximum") != null) {
            schema.setMaximum((Integer) schemaMap.get("maximum"));
        }

        // Required fields
        List<String> required = (List<String>) schemaMap.get("required");
        if (required != null) {
            schema.setRequired(required);
        }

        // Properties (para objects)
        Map<String, Object> properties = (Map<String, Object>) schemaMap.get("properties");
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String propName = entry.getKey();
                Map<String, Object> propSchema = (Map<String, Object>) entry.getValue();
                schema.addProperty(propName, parseSchema(propSchema));
            }
        }

        // Items (para arrays)
        Map<String, Object> items = (Map<String, Object>) schemaMap.get("items");
        if (items != null) {
            schema.setItems(parseSchema(items));
        }

        return schema;
    }
}