package org.example.server.http;

import com.sun.net.httpserver.HttpExchange;

public class Request {

    private String method;

    private String path;

    public static Request fromExchange(HttpExchange exchange) {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());

        return request;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}