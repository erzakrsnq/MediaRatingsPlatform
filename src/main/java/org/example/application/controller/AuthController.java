package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.Token;
import org.example.application.services.AuthService;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthController extends Controller {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals("POST")) {
            if (request.getPath().equals("/auth/login")) {
                return login(request);
            }
            if (request.getPath().equals("/auth/logout")) {
                return logout(request);
            }
        }
        
        return status(Status.NOT_FOUND);
    }

    private Response login(Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var loginData = mapper.readTree(request.getBody());
            String username = loginData.get("username").asText();
            String password = loginData.get("password").asText();

            Token token = authService.login(username, password);
            return json(token, Status.OK);
        } catch (Exception e) {
            return status(Status.UNAUTHORIZED);
        }
    }

    private Response logout(Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var logoutData = mapper.readTree(request.getBody());
            String tokenString = logoutData.get("token").asText();
            authService.logout(tokenString);
            return status(Status.OK);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }
}
