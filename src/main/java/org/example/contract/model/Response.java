package org.example.contract.model;

public class Response {
    private int statusCode;
    private String description;
    private Schema schema;

    public Response() {}

    public Response(int statusCode, String description, Schema schema) {
        this.statusCode = statusCode;
        this.description = description;
        this.schema = schema;
    }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Schema getSchema() { return schema; }
    public void setSchema(Schema schema) { this.schema = schema; }

    @Override
    public String toString() {
        return String.format("Response{statusCode=%d, description='%s'}",
                statusCode, description);
    }
}