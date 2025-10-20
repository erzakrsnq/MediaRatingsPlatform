package org.example.server.util;

import org.example.server.http.Request;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());

        // Read request body
        InputStream is = exchange.getRequestBody();
        if (is != null) {
            byte[] buf = is.readAllBytes();
            request.setBody(new String(buf, StandardCharsets.UTF_8));
        }

        return request;
    }
}