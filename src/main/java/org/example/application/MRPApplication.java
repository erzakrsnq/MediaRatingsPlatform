package org.example.application;

import org.example.application.common.Application;
import org.example.application.common.Router;
import org.example.application.controller.Mediacontroller;
import org.example.application.controller.Ratingcontroller;
import org.example.application.controller.Usercontroller;
import org.example.application.auth.AuthController;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import org.example.server.http.ContentType;

public class MRPApplication implements Application {

    private final Router router;

    public MRPApplication() {
        this.router = new Router();

        // Controller registrieren
        this.router.addRoute("/auth", new AuthController());
        this.router.addRoute("/media", new Mediacontroller());
        this.router.addRoute("/ratings", new Ratingcontroller());
        this.router.addRoute("/users", new Usercontroller());
    }

    @Override
    public Response handle(Request request) {
        return this.router.findController(request.getPath())
                .map(controller -> controller.handle(request))
                .orElseGet(() -> {
                    Response response = new Response();
                    response.setStatus(Status.NOT_FOUND);
                    response.setContentType(ContentType.TEXT_PLAIN);
                    response.setBody("404 Not Found");
                    return response;
                });
    }
}
