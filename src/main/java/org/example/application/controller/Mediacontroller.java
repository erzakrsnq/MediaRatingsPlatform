package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Media;
import org.example.application.services.MediaService;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;

public class MediaController extends Controller {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    public Response handle(Request request) {

        if (request.getMethod().equals("GET")) {
            if (request.getPath().equals("/media")) {
                return readAll();
            }
            return read(request);
        }

        if (request.getMethod().equals("POST")) {
            return create(request);
        }

        if (request.getMethod().equals("PUT")) {
            return update(request);
        }

        if (request.getMethod().equals("DELETE")) {
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
            try {
                Media media = mediaService.get(id);
                return json(media, Status.OK);
            } catch (RuntimeException e) {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        try {
            Media media = toObject(request.getBody(), Media.class);
            media = mediaService.create(media);
            return json(media, Status.CREATED);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }

    private Response update(Request request) {
        try {
            // Extract ID from path like /media/123
            String[] pathParts = request.getPath().split("/");
            if (pathParts.length >= 3) {
                String id = pathParts[2];
                Media update = toObject(request.getBody(), Media.class);
                Media media = mediaService.update(id, update);
                return json(media, Status.OK);
            }
            return status(Status.BAD_REQUEST);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }

    private Response delete(Request request) {
        try {
            // Extract ID from path like /media/123
            String[] pathParts = request.getPath().split("/");
            if (pathParts.length >= 3) {
                String id = pathParts[2];
                Media media = mediaService.delete(id);
                if (media != null) {
                    return json(media, Status.OK);
                } else {
                    return status(Status.NOT_FOUND);
                }
            }
            return status(Status.BAD_REQUEST);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }
}

