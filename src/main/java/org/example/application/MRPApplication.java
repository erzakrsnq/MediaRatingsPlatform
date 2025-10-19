package org.example.application;

import org.example.application.common.Application;
import org.example.application.common.Controller;
import org.example.application.common.Router;
import org.example.application.controller.Mediacontroller;
import org.example.application.controller.Ratingcontroller;
import org.example.application.controller.Usercontroller;
import org.example.application.auth.AuthController;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.Optional;
public class MRPApplication implements Application {

    private final Router router;

    public MRPApplication() {
        this.router = new Router();


    @Override
    public Response handle(Request request) {
        try {
            Controller controller = router.findController(request.getPath())
                    .orElseThrow(RuntimeException::new);

            return controller.handle(request);
        } catch (Exception ex) {
            // Simple error handling for now
            Response response = new Response();
            response.setStatus(Status.NOT_FOUND);
            response.setContentType("text/plain");
            response.setBody("Not Found");
            return response;
        }
    }
}
