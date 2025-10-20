package org.example.application;

import org.example.application.common.Application;
import org.example.application.common.Controller;
import org.example.application.common.Router;
import org.example.application.controller.Usercontroller;
import org.example.application.controller.AuthController;
import org.example.application.controller.MediaController;
import org.example.application.controller.RatingController;
import org.example.application.repository.MemoryUserRepository;
import org.example.application.repository.MemoryMediaRepository;
import org.example.application.repository.MemoryRatingRepository;
import org.example.application.services.UserService;
import org.example.application.services.AuthService;
import org.example.application.services.MediaService;
import org.example.application.services.RatingService;
import org.example.server.http.ContentType;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class MRPApplication implements Application {
    private final Router router;

    public MRPApplication() {
        this.router = new Router();

        // Initialize services
        UserService userService = new UserService(new MemoryUserRepository());
        AuthService authService = new AuthService(userService);
        MediaService mediaService = new MediaService(new MemoryMediaRepository());
        RatingService ratingService = new RatingService(new MemoryRatingRepository());

        // Add User routes
        router.addRoute("/users", new Usercontroller(userService));
        
        // Add Auth routes
        router.addRoute("/auth", new AuthController(authService));
        
        // Add Media routes
        router.addRoute("/media", new MediaController(mediaService));
        
        // Add Rating routes
        router.addRoute("/ratings", new RatingController(ratingService));
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
            response.setContentType(ContentType.TEXT_PLAIN);
            response.setBody("Not Found: " + request.getPath());
            return response;
        }
    }
}
