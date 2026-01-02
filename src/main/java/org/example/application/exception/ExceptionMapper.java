package org.example.application.exception;

import org.example.server.http.ContentType;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMapper {

    private final Map<Class<?>, Status> map;

    public ExceptionMapper() {
        this.map = new HashMap<>();
    }

    public Response toResponse(Exception exception) {
        Response response = new Response();

        Status status = map.get(exception.getClass());
        if (null == status) {
            status = Status.INTERNAL_SERVER_ERROR;
        }

        response.setStatus(status);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setBody(exception.getMessage());

        return response;
    }

    public void register(Class<?> clazz, Status status) {
        map.put(clazz, status);
    }
}

