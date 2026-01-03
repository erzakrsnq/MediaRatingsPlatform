package org.example.application.services;

import org.example.application.model.Token;
import org.example.application.model.User;
import org.example.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        authService = new AuthService(userService);
    }

    @Test
    void testLogin_ValidCredentials_ReturnsToken() {
        // Arrange
        User user = new User();
        user.setId("user-id");
        user.setPasswordHash("hashed_password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        Token token = authService.login("testuser", "password123");

        // Assert
        assertNotNull(token.getToken());
        assertEquals("user-id", token.getUserId());
        assertTrue(token.getExpiresAt() > System.currentTimeMillis());
    }

    @Test
    void testLogin_InvalidCredentials_ThrowsException() {
        // Arrange
        User user = new User();
        user.setPasswordHash("hashed_differentpassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login("testuser", "wrongpassword"));
    }

}

