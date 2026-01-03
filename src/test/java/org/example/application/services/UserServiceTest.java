package org.example.application.services;

import org.example.application.exception.EntityNotFoundException;
import org.example.application.model.User;
import org.example.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void testCreateUser_GeneratesId() {
        // Arrange
        User user = new User();
        user.setPasswordHash("password123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.create(user);

        // Assert
        assertNotNull(result.getId());
        assertTrue(result.getPasswordHash().startsWith("hashed_"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetUser_ReturnsUser() {
        // Arrange
        User user = new User();
        user.setId("user-123");
        when(userRepository.find("user-123")).thenReturn(Optional.of(user));

        // Act
        User result = userService.get("user-123");

        // Assert
        assertEquals("user-123", result.getId());
        verify(userRepository).find("user-123");
    }

    @Test
    void testGetUser_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(userRepository.find("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.get("nonexistent"));
        verify(userRepository).find("nonexistent");
    }

    @Test
    void testLogin_ValidCredentials_ReturnsUser() {
        // Arrange
        User user = new User();
        user.setPasswordHash("hashed_password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        User result = userService.login("testuser", "password123");

        // Assert
        assertNotNull(result);
        verify(userRepository).findByUsername("testuser");
    }

}

