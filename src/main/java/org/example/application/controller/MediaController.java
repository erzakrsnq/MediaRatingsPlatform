package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Media;
import org.example.application.model.Token;
import org.example.application.services.AuthService;
import org.example.application.services.MediaService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class MediaController extends Controller {

    private final MediaService mediaService;
    private final AuthService authService;

    public MediaController(MediaService mediaService, AuthService authService) {
        this.mediaService = mediaService;
        this.authService = authService;
    }

    @Override
    public Response handle(Request request) {

        if (request.getMethod().equals(Method.GET.getVerb())) {
            if (request.getPath().equals("/media")) {
                return readAll();
            }
            return read(request);
        }

        if (request.getMethod().equals(Method.POST.getVerb())) {
            return create(request);
        }

        if (request.getMethod().equals(Method.PUT.getVerb())) {
            return update(request);
        }

        if (request.getMethod().equals(Method.DELETE.getVerb())) {
            return delete(request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response readAll() {
        List<Media> media = mediaService.getAll();
        return json(media, Status.OK);
    }

    private Response read(Request request) {
        // Extract ID from path like /media/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 3) {
            String id = pathParts[2];
            Media media = mediaService.get(id);
            return json(media, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        // Get user from token in request body
        String userId = getUserIdFromRequest(request);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        Media media = toObject(request.getBody(), Media.class);
        media = mediaService.create(media, userId);
        return json(media, Status.CREATED);
    }

    private Response update(Request request) {
        // Get user from token in request body
        String userId = getUserIdFromRequest(request);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        // Extract ID from path like /media/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 3) {
            String id = pathParts[2];
            Media update = toObject(request.getBody(), Media.class);
            Media media = mediaService.update(id, update, userId);
            return json(media, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response delete(Request request) {
        // Get user from token in request body
        String userId = getUserIdFromRequest(request);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        // Extract ID from path like /media/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 3) {
            String id = pathParts[2];
            Media media = mediaService.delete(id, userId);
            return json(media, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private String getUserIdFromRequest(Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var requestData = mapper.readTree(request.getBody());
            if (requestData.has("token")) {
                String tokenValue = requestData.get("token").asText();
                Optional<Token> token = authService.getToken(tokenValue);
                
                if (token.isEmpty() || token.get().isExpired()) {
                    return null;
                }

                return token.get().getUserId();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
