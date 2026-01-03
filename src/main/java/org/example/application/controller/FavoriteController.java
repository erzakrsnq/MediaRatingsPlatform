package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Media;
import org.example.application.services.AuthService;
import org.example.application.services.FavoriteService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;

public class FavoriteController extends Controller {

    private final FavoriteService favoriteService;
    private final AuthService authService;

    public FavoriteController(FavoriteService favoriteService, AuthService authService) {
        this.favoriteService = favoriteService;
        this.authService = authService;
    }

    @Override
    public Response handle(Request request) {
        String userId = getUserIdFromRequest(request, authService);
        if (userId == null) {
            return status(Status.UNAUTHORIZED);
        }

        if (request.getMethod().equals(Method.GET.getVerb())) {
            if (request.getPath().equals("/favorites")) {
                return getFavorites(userId);
            }
        }

        if (request.getMethod().equals(Method.POST.getVerb())) {
            if (request.getPath().startsWith("/favorites/")) {
                return addFavorite(request, userId);
            }
        }

        if (request.getMethod().equals(Method.DELETE.getVerb())) {
            if (request.getPath().startsWith("/favorites/")) {
                return removeFavorite(request, userId);
            }
        }

        return status(Status.NOT_FOUND);
    }

    private Response getFavorites(String userId) {
        List<Media> favorites = favoriteService.getFavoritesByUserId(userId);
        return json(favorites, Status.OK);
    }

    private Response addFavorite(Request request, String userId) {
        String mediaId = extractPathSegment(request, 2);
        if (mediaId != null) {
            favoriteService.addFavorite(userId, mediaId);
            return status(Status.CREATED);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response removeFavorite(Request request, String userId) {
        String mediaId = extractPathSegment(request, 2);
        if (mediaId != null) {
            favoriteService.removeFavorite(userId, mediaId);
            return status(Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }
}

