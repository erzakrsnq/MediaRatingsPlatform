package org.example.application;

import org.example.application.common.Application;
import org.example.application.common.Controller;
import org.example.application.common.Router;
import org.example.application.controller.Usercontroller;
import org.example.application.repository.MemoryUserRepository;
import org.example.application.services.UserService;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
public class MRPApplication implements Application {

    private final Router router;

    public MRPApplication() {
        this.router = new Router();

        // Add User routes
        router.addRoute("/users", new Usercontroller(
            new UserService(new MemoryUserRepository())
        ));
        
        // TODO: Add other routes when controllers are implemented
        // router.addRoute("/media", new Mediacontroller());
        // router.addRoute("/ratings", new Ratingcontroller());
        // router.addRoute("/auth", new AuthController());
    }

    @Override
    public Response handle(Request request) {
        try {
            System.out.println("Request path: " + request.getPath());
            System.out.println("Request method: " + request.getMethod());
            
            Controller controller = router.findController(request.getPath())
                    .orElseThrow(RuntimeException::new);

            return controller.handle(request);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            // Simple error handling for now
            Response response = new Response();
            response.setStatus(Status.NOT_FOUND);
            response.setContentType("text/plain");
            response.setBody("Not Found: " + request.getPath());
            return response;
        }
    }
}
