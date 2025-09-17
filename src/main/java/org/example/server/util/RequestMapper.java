package org.example.server.util;

import org.example.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());

        return request;
    }
}