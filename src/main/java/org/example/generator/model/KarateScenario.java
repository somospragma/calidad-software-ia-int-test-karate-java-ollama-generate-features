package org.example.generator.model;

import java.util.*;

public class KarateScenario {
    private String name;
    private String operationId;
    private List<String> tags;
    private List<String> steps;
    private String description;

    public KarateScenario() {
        this.tags = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void addTag(String tag) { this.tags.add(tag); }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }
    public void addStep(String step) { this.steps.add(step); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String toKarateString() {
        StringBuilder sb = new StringBuilder();

        // Tags
        if (!tags.isEmpty()) {
            sb.append("  ");
            for (String tag : tags) {
                sb.append(tag).append(" ");
            }
            sb.append("\n");
        }

        // Scenario name
        sb.append("  Scenario: ").append(name).append("\n");

        // Steps
        for (String step : steps) {
            sb.append("    ").append(step).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toKarateString();
    }
}