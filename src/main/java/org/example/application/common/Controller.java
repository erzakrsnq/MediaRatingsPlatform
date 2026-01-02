package org.example.application.common;
import org.example.application.exception.JsonConversionException;
import org.example.application.exception.NotJsonBodyException;
import org.example.server.http.ContentType;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Controller {

    public abstract Response handle(Request request);

    protected <T> T toObject(String content, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception ex) {
            throw new NotJsonBodyException(ex);
        }
    }

    protected Response ok() {
        return status(Status.OK);
    }

    protected Response status(Status status) {
        return text(status.getMessage(), status);
    }

    protected Response text(String text) {
        return text(text, Status.OK);
    }

    protected Response text(String text, Status status) {
        return r(status, ContentType.TEXT_PLAIN, text);
    }

    protected Response json(Object o, Status status) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(o);
            return r(status, ContentType.APPLICATION_JSON, json);
        } catch (Exception ex) {
            throw new JsonConversionException(ex);
        }
    }

    private Response r(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setContentType(contentType);
        response.setBody(body);
        return response;
    }
}