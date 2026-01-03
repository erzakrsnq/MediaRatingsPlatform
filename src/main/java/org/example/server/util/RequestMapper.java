package org.example.server.util;

import org.example.server.http.Method;
import org.example.server.http.Request;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(Method.valueOf(exchange.getRequestMethod()));
        
        String fullPath = exchange.getRequestURI().toString();
        String path = exchange.getRequestURI().getPath();
        request.setPath(path);
        
        // Query-Parameter extrahieren
        if (fullPath.contains("?")) {
            String query = fullPath.substring(fullPath.indexOf("?") + 1);
            Map<String, String> params = new HashMap<>();
            if (!query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        params.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                    }
                }
            }
            request.setParams(params);
        }

        // Read request body
        InputStream is = exchange.getRequestBody();
        if (is != null) {
            byte[] buf = is.readAllBytes();
            request.setBody(new String(buf, StandardCharsets.UTF_8));
        }

        return request;
    }
}