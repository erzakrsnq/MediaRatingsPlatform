package org.example.application.controller;

import org.example.application.common.Controller;
import org.example.application.model.User;
import org.example.application.services.UserService;
import org.example.server.http.Method;
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

        if (request.getMethod().equals(Method.GET.getVerb())) {
            if (request.getPath().equals("/users")) {
                return readAll();
            }
            return read(request);
        }

        if (request.getMethod().equals(Method.POST.getVerb())) {
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
        String id = extractPathSegment(request, 2);
        if (id != null) {
            User user = userService.get(id);
            return json(user, Status.OK);
        }
        return status(Status.BAD_REQUEST);
    }

    private Response create(Request request) {
        User user = toObject(request.getBody(), User.class);
        user = userService.create(user);
        return json(user, Status.CREATED);
    }

    private Response login(Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var loginData = mapper.readTree(request.getBody());
            String username = loginData.get("username").asText();
            String password = loginData.get("password").asText();
            
            User user = userService.login(username, password);
            return json(user, Status.OK);
        } catch (Exception e) {
            return status(Status.BAD_REQUEST);
        }
    }
}
