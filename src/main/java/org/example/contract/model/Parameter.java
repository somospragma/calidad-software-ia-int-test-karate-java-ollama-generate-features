package org.example.contract.model;

public class Parameter {
    private String name;
    private String in;
    private String type;
    private boolean required;
    private String description;
    private Object example;
    private Integer minLength;
    private Integer maxLength;
    private String format;

    public Parameter() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIn() { return in; }
    public void setIn(String in) { this.in = in; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Object getExample() { return example; }
    public void setExample(Object example) { this.example = example; }

    public Integer getMinLength() { return minLength; }
    public void setMinLength(Integer minLength) { this.minLength = minLength; }

    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    @Override
    public String toString() {
        return String.format("Parameter{name='%s', in='%s', type='%s', required=%s}",
                name, in, type, required);
    }
}