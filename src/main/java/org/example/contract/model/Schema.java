package org.example.contract.model;

import java.util.*;

public class Schema {
    private String type; // "object", "array", "string", "integer", etc.
    private Map<String, Schema> properties;
    private List<String> required;
    private String format; // "email", "uuid", "date-time"
    private Integer minLength;
    private Integer maxLength;
    private Integer minimum;
    private Integer maximum;
    private String pattern;
    private Schema items; // Para arrays
    private Object example;

    public Schema() {
        this.properties = new HashMap<>();
        this.required = new ArrayList<>();
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Schema> getProperties() { return properties; }
    public void setProperties(Map<String, Schema> properties) { this.properties = properties; }
    public void addProperty(String name, Schema schema) { this.properties.put(name, schema); }

    public List<String> getRequired() { return required; }
    public void setRequired(List<String> required) { this.required = required; }
    public void addRequired(String field) { this.required.add(field); }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Integer getMinLength() { return minLength; }
    public void setMinLength(Integer minLength) { this.minLength = minLength; }

    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public Integer getMinimum() { return minimum; }
    public void setMinimum(Integer minimum) { this.minimum = minimum; }

    public Integer getMaximum() { return maximum; }
    public void setMaximum(Integer maximum) { this.maximum = maximum; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public Schema getItems() { return items; }
    public void setItems(Schema items) { this.items = items; }

    public Object getExample() { return example; }
    public void setExample(Object example) { this.example = example; }

    @Override
    public String toString() {
        return String.format("Schema{type='%s', properties=%d, required=%d}",
                type, properties.size(), required.size());
    }
}