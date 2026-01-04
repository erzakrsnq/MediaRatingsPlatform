package org.example.application;

import org.example.application.common.Application;
import org.example.application.common.Controller;
import org.example.application.common.Router;
import org.example.application.controller.Usercontroller;
import org.example.application.controller.AuthController;
import org.example.application.controller.MediaController;
import org.example.application.controller.RatingController;
import org.example.application.controller.FavoriteController;
import org.example.application.common.ConnectionPool;
import org.example.application.repository.DbUserRepository;
import org.example.application.repository.DbMediaRepository;
import org.example.application.repository.DbRatingRepository;
import org.example.application.repository.DbRatingLikeRepository;
import org.example.application.repository.DbFavoriteRepository;
import org.example.application.services.UserService;
import org.example.application.services.AuthService;
import org.example.application.services.MediaService;
import org.example.application.services.RatingService;
import org.example.application.services.RatingLikeService;
import org.example.application.services.FavoriteService;
import org.example.application.exception.ExceptionMapper;
import org.example.application.exception.EntityNotFoundException;
import org.example.application.exception.RouteNotFoundException;
import org.example.application.exception.JsonConversionException;
import org.example.application.exception.NotJsonBodyException;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class MRPApplication implements Application {
    private final Router router;
    private final ExceptionMapper exceptionMapper;
    private final ConnectionPool connectionPool;

    public MRPApplication() {
        this.router = new Router();

        // Initialize ConnectionPool
        this.connectionPool = new ConnectionPool(
                "postgresql",
                "localhost",
                5433,
                "swen1user",
                "swen1db",
                "mrpdb"
        );

        // Initialize repositories with DB
        DbUserRepository userRepository = new DbUserRepository(connectionPool);
        DbMediaRepository mediaRepository = new DbMediaRepository(connectionPool);
        DbRatingRepository ratingRepository = new DbRatingRepository(connectionPool);
        DbRatingLikeRepository ratingLikeRepository = new DbRatingLikeRepository(connectionPool);
        DbFavoriteRepository favoriteRepository = new DbFavoriteRepository(connectionPool);

        // Initialize services
        UserService userService = new UserService(userRepository);
        AuthService authService = new AuthService(userService);
        MediaService mediaService = new MediaService(mediaRepository);
        RatingService ratingService = new RatingService(ratingRepository);
        RatingLikeService ratingLikeService = new RatingLikeService(ratingLikeRepository);
        FavoriteService favoriteService = new FavoriteService(favoriteRepository, mediaRepository);

        // Add User routes
        router.addRoute("/users", new Usercontroller(userService));
        
        // Add Auth routes
        router.addRoute("/auth", new AuthController(authService));
        
        // Add Media routes
        router.addRoute("/media", new MediaController(mediaService, authService));
        
        // Add Rating routes
        router.addRoute("/ratings", new RatingController(ratingService, ratingLikeService, authService));
        
        // Add Favorite routes
        router.addRoute("/favorites", new FavoriteController(favoriteService, authService));

        // Initialize ExceptionMapper
        this.exceptionMapper = new ExceptionMapper();
        this.exceptionMapper.register(EntityNotFoundException.class, Status.NOT_FOUND);
        this.exceptionMapper.register(RouteNotFoundException.class, Status.NOT_FOUND);
        this.exceptionMapper.register(NotJsonBodyException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(JsonConversionException.class, Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Response handle(Request request) {
        try {
            Controller controller = router.findController(request.getPath())
                    .orElseThrow(
                            () -> new RouteNotFoundException(
                                    "%s not found".formatted(request.getPath())
                            )
                    );

            return controller.handle(request);
        } catch (Exception ex) {
            return exceptionMapper.toResponse(ex);
        }
    }
}
