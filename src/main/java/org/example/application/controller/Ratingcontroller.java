package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Rating;
import org.example.application.services.RatingService;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;

public class RatingController extends Controller {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Override
    public Response handle(Request request) {

        if (request.getMethod().equals("GET")) {
            if (request.getPath().equals("/ratings")) {
                return readAll();
            }
            if (request.getPath().startsWith("/ratings/media/")) {
                return readByMediaId(request);
            }
            if (request.getPath().startsWith("/ratings/user/")) {
                return readByUserId(request);
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
        List<Rating> ratings = ratingService.getAll();
        return json(ratings, Status.OK);
    }

    private Response read(Request request) {
        // Extract ID from path like /ratings/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 3) {
            String id = pathParts[2];
            try {
                Rating rating = ratingService.get(id);
                return json(rating, Status.OK);
            } catch (RuntimeException e) {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }

    private Response readByMediaId(Request request) {
        // Extract mediaId from path like /ratings/media/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 4) {
            String mediaId = pathParts[3];
            try {
                List<Rating> ratings = ratingService.getByMediaId(mediaId);
                return json(ratings, Status.OK);
            } catch (RuntimeException e) {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }

    private Response readByUserId(Request request) {
        // Extract userId from path like /ratings/user/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 4) {
            String userId = pathParts[3];
            try {
                List<Rating> ratings = ratingService.getByUserId(userId);
                return json(ratings, Status.OK);
            } catch (RuntimeException e) {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        try {
            Rating rating = toObject(request.getBody(), Rating.class);
            rating = ratingService.create(rating);
            return json(rating, Status.CREATED);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }

    private Response update(Request request) {
        try {
            // Extract ID from path like /ratings/123
            String[] pathParts = request.getPath().split("/");
            if (pathParts.length >= 3) {
                String id = pathParts[2];
                Rating update = toObject(request.getBody(), Rating.class);
                Rating rating = ratingService.update(id, update);
                return json(rating, Status.OK);
            }
            return status(Status.BAD_REQUEST);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }

    private Response delete(Request request) {
        try {
            // Extract ID from path like /ratings/123
            String[] pathParts = request.getPath().split("/");
            if (pathParts.length >= 3) {
                String id = pathParts[2];
                Rating rating = ratingService.delete(id);
                if (rating != null) {
                    return json(rating, Status.OK);
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

