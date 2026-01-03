package org.example.application.common;

import org.example.server.http.Request;
import org.example.server.http.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RouterTest {

    private Router router;
    private Controller controller1;

    @BeforeEach
    void setUp() {
        router = new Router();
        controller1 = new TestController();
    }

    private static class TestController extends Controller {
        @Override
        public Response handle(Request request) {
            return null;
        }
    }

    @Test
    void testFindController_ExactPathMatch_ReturnsController() {
        router.addRoute("/users", controller1);
        Optional<Controller> result = router.findController("/users");
        assertTrue(result.isPresent());
        assertEquals(controller1, result.get());
    }

    @Test
    void testFindController_NoMatch_ReturnsEmpty() {
        router.addRoute("/users", controller1);
        Optional<Controller> result = router.findController("/media");
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddRoute_AddsRouteToRouter() {
        router.addRoute("/test", controller1);
        Optional<Controller> result = router.findController("/test");
        assertTrue(result.isPresent());
        assertEquals(controller1, result.get());
    }
}
