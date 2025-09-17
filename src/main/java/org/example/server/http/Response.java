package org.example.server.http;

public class Response {
    private final int statusCode;
    private final String contentType;
    private final String body;

    public Response() {
        this.statusCode = 200;
        this.contentType = "application/json";
        this.body = "";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }
}
