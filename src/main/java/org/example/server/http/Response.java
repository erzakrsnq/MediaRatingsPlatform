package org.example.server.http;

public class Response {

    private Status status;
    private String contentType;
    private String body;

    public Response() {
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getStatusMessage() {
        return status.getMessage();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
