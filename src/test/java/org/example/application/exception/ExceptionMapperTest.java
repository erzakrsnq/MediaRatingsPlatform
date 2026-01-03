package org.example.application.exception;

import org.example.server.http.ContentType;
import org.example.server.http.Response;
import org.example.server.http.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionMapperTest {

    private ExceptionMapper exceptionMapper;

    @BeforeEach
    void setUp() {
        exceptionMapper = new ExceptionMapper();
    }

    @Test
    void testToResponse_RegisteredException_ReturnsMappedStatus() {
        // Arrange
        exceptionMapper.register(EntityNotFoundException.class, Status.NOT_FOUND);
        EntityNotFoundException exception = new EntityNotFoundException("Not found");

        // Act
        Response response = exceptionMapper.toResponse(exception);

        // Assert
        assertNotNull(response);
        assertEquals(Status.NOT_FOUND.getCode(), response.getStatusCode());
        assertEquals(ContentType.TEXT_PLAIN.getMimeType(), response.getContentType());
    }

    @Test
    void testToResponse_UnregisteredException_ReturnsInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Error");

        // Act
        Response response = exceptionMapper.toResponse(exception);

        // Assert
        assertNotNull(response);
        assertEquals(Status.INTERNAL_SERVER_ERROR.getCode(), response.getStatusCode());
    }

    @Test
    void testToResponse_ExceptionMessage_IncludedInBody() {
        // Arrange
        String message = "Custom error message";
        exceptionMapper.register(EntityNotFoundException.class, Status.NOT_FOUND);
        EntityNotFoundException exception = new EntityNotFoundException(message);

        // Act
        Response response = exceptionMapper.toResponse(exception);

        // Assert
        assertEquals(message, response.getBody());
    }

}

