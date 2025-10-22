package org.example.contract.model;

import java.util.*;

public class ApiContract {
    private String title;
    private String version;
    private String description;
    private String baseUrl;
    private List<Endpoint> endpoints;

    public ApiContract() {
        this.endpoints = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public List<Endpoint> getEndpoints() { return endpoints; }
    public void setEndpoints(List<Endpoint> endpoints) { this.endpoints = endpoints; }
    public void addEndpoint(Endpoint endpoint) { this.endpoints.add(endpoint); }

    public Endpoint getEndpointByOperationId(String operationId) {
        return endpoints.stream()
                .filter(e -> operationId.equals(e.getOperationId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return String.format("ApiContract{title='%s', version='%s', endpoints=%d}",
                title, version, endpoints.size());
    }
}