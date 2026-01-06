package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Media;
import org.example.application.services.AuthService;
import org.example.application.services.MediaService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;
import java.util.Map;

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
                return readAll(request);
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

    private Response readAll(Request request) {
        Map<String, String> params = request.getParams();
        String title = params.get("title");
        String genre = params.get("genre");
        String type = params.get("type");
        String minRatingStr = params.get("minRating");
        String maxAgeRestrictionStr = params.get("maxAgeRestriction");
        
        Double minRating = null;
        if (minRatingStr != null && !minRatingStr.isEmpty()) {
            try {
                minRating = Double.parseDouble(minRatingStr);
            } catch (NumberFormatException e) {
                return status(Status.BAD_REQUEST);
            }
        }
        
        Integer maxAgeRestriction = null;
        if (maxAgeRestrictionStr != null && !maxAgeRestrictionStr.isEmpty()) {
            try {
                maxAgeRestriction = Integer.parseInt(maxAgeRestrictionStr);
            } catch (NumberFormatException e) {
                return status(Status.BAD_REQUEST);
            }
        }
        
        if (title != null || genre != null || type != null || minRating != null || maxAgeRestriction != null) {
            List<Media> media = mediaService.search(title, genre, type, minRating, maxAgeRestriction);
            return json(media, Status.OK);
        }
        
        List<Media> media = mediaService.getAll();
        return json(media, Status.OK);
    }

    private Response read(Request request) {
        String id = extractPathSegment(request, 2);
        if (id != null) {
            Media media = mediaService.get(id);
            return json(media, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        String userId = getUserIdFromRequest(request, authService);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        Media media = toObject(request.getBody(), Media.class);
        media = mediaService.create(media, userId);
        return json(media, Status.CREATED);
    }

    private Response update(Request request) {
        String userId = getUserIdFromRequest(request, authService);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        String id = extractPathSegment(request, 2);
        if (id != null) {
            Media update = toObject(request.getBody(), Media.class);
            Media media = mediaService.update(id, update, userId);
            return json(media, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response delete(Request request) {
        String userId = getUserIdFromRequest(request, authService);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        String id = extractPathSegment(request, 2);
        if (id != null) {
            Media media = mediaService.delete(id, userId);
            return json(media, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }
}
