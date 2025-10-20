package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.User;
import org.example.application.services.UserService;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Usercontroller extends Controller {

    private final UserService userService;

    public Usercontroller(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {

        if (request.getMethod().equals("GET")) {
            if (request.getPath().equals("/users")) {
                return readAll();
            }
            return read(request);
        }

        if (request.getMethod().equals("POST")) {
            if (request.getPath().equals("/users/login")) {
                return login(request);
            }
            return create(request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response readAll() {
        List<User> users = userService.getAll();
        return json(users, Status.OK);
    }

    private Response read(Request request) {
        // Extract ID from path like /users/123
        String[] pathParts = request.getPath().split("/");
        if (pathParts.length >= 3) {
            String id = pathParts[2];
            try {
                User user = userService.get(id);
                return json(user, Status.OK);
            } catch (RuntimeException e) {
                return status(Status.NOT_FOUND);
            }
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        try {
            User user = toObject(request.getBody(), User.class);
            user = userService.create(user);
            return json(user, Status.CREATED);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }

    private Response login(Request request) {
        try {
            // Simple login with username and password in JSON
            ObjectMapper mapper = new ObjectMapper();
            var loginData = mapper.readTree(request.getBody());
            String username = loginData.get("username").asText();
            String password = loginData.get("password").asText();
            
            User user = userService.login(username, password);
            return json(user, Status.OK);
        } catch (Exception e) {
            return status(Status.UNAUTHORIZED);
        }
    }
}
