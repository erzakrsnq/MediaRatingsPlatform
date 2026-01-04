package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Rating;
import org.example.application.services.RatingService;
import org.example.server.http.Method;
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

        if (request.getMethod().equals(Method.GET.getVerb())) {
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

        if (request.getMethod().equals(Method.POST.getVerb())) {
            return create(request);
        }

        if (request.getMethod().equals(Method.PUT.getVerb())) {
            if (request.getPath().startsWith("/ratings/") && request.getPath().endsWith("/confirm")) {
                return confirmComment(request);
            }
            return update(request);
        }

        if (request.getMethod().equals(Method.DELETE.getVerb())) {
            return delete(request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response readAll() {
        List<Rating> ratings = ratingService.getAll();
        return json(ratings, Status.OK);
    }

    private Response read(Request request) {
        String id = extractPathSegment(request, 2);
        if (id != null) {
            Rating rating = ratingService.get(id);
            return json(rating, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response readByMediaId(Request request) {
        String mediaId = extractPathSegment(request, 3);
        if (mediaId != null) {
            List<Rating> ratings = ratingService.getByMediaId(mediaId);
            return json(ratings, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response readByUserId(Request request) {
        String userId = extractPathSegment(request, 3);
        if (userId != null) {
            List<Rating> ratings = ratingService.getByUserId(userId);
            return json(ratings, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        Rating rating = toObject(request.getBody(), Rating.class);
        rating = ratingService.create(rating);
        return json(rating, Status.CREATED);
    }

    private Response update(Request request) {
        String id = extractPathSegment(request, 2);
        if (id != null) {
            Rating update = toObject(request.getBody(), Rating.class);
            Rating rating = ratingService.update(id, update);
            return json(rating, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response confirmComment(Request request) {
        String id = extractPathSegment(request, 2);
        if (id != null) {
            try {
                Rating rating = ratingService.confirmComment(id);
                return json(rating, Status.OK);
            } catch (Exception e) {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }

    private Response delete(Request request) {
        String id = extractPathSegment(request, 2);
        if (id != null) {
            Rating rating = ratingService.delete(id);
            if (rating != null) {
                return json(rating, Status.OK);
            } else {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }
}
