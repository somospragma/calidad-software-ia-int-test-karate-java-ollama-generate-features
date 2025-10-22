package org.example.contract.model;

import java.util.*;
import java.util.stream.Collectors;

public class Endpoint {
    private String path;
    private HttpMethod method;
    private String summary;
    private String description;
    private String operationId;
    private List<Parameter> parameters;
    private Schema requestBody;
    private Map<Integer, Response> responses;
    private List<String> tags;

    public Endpoint() {
        this.parameters = new ArrayList<>();
        this.responses = new HashMap<>();
        this.tags = new ArrayList<>();
    }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public HttpMethod getMethod() { return method; }
    public void setMethod(HttpMethod method) { this.method = method; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public List<Parameter> getParameters() { return parameters; }
    public void setParameters(List<Parameter> parameters) { this.parameters = parameters; }
    public void addParameter(Parameter parameter) { this.parameters.add(parameter); }

    public Schema getRequestBody() { return requestBody; }
    public void setRequestBody(Schema requestBody) { this.requestBody = requestBody; }
    public boolean hasRequestBody() { return requestBody != null; }

    public Map<Integer, Response> getResponses() { return responses; }
    public void setResponses(Map<Integer, Response> responses) { this.responses = responses; }
    public void addResponse(int statusCode, Response response) {
        response.setStatusCode(statusCode);
        this.responses.put(statusCode, response);
    }
    public Response getResponse(int statusCode) { return responses.get(statusCode); }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void addTag(String tag) { this.tags.add(tag); }

    public List<Parameter> getPathParams() {
        return parameters.stream()
                .filter(p -> "path".equals(p.getIn()))
                .collect(Collectors.toList());
    }

    public List<Parameter> getQueryParams() {
        return parameters.stream()
                .filter(p -> "query".equals(p.getIn()))
                .collect(Collectors.toList());
    }

    public List<Parameter> getHeaderParams() {
        return parameters.stream()
                .filter(p -> "header".equals(p.getIn()))
                .collect(Collectors.toList());
    }

    public boolean needsAuth() {
        return getHeaderParams().stream()
                .anyMatch(p -> "Authorization".equalsIgnoreCase(p.getName()));
    }

    @Override
    public String toString() {
        return String.format("Endpoint{%s %s, operationId='%s', summary='%s'}",
                method, path, operationId, summary);
    }
}