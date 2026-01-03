package org.example.application.services;

import org.example.application.model.Rating;
import org.example.application.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository);
    }

    @Test
    void testCreate_GeneratesId() {
        // Arrange
        Rating rating = new Rating();
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Rating result = ratingService.create(rating);

        // Assert
        assertNotNull(result.getId());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testGet_ReturnsRating() {
        // Arrange
        Rating rating = new Rating();
        rating.setId("rating-123");
        when(ratingRepository.find("rating-123")).thenReturn(Optional.of(rating));

        // Act
        Rating result = ratingService.get("rating-123");

        // Assert
        assertEquals("rating-123", result.getId());
        verify(ratingRepository).find("rating-123");
    }

    @Test
    void testUpdate_UpdatesRating() {
        // Arrange
        Rating existingRating = new Rating();
        when(ratingRepository.find("rating-123")).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(existingRating);

        // Act
        ratingService.update("rating-123", new Rating());

        // Assert
        verify(ratingRepository).find("rating-123");
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testGetByMediaId_ReturnsRatings() {
        // Arrange
        List<Rating> ratings = List.of(new Rating(), new Rating());
        when(ratingRepository.findByMediaId("media-123")).thenReturn(ratings);

        // Act
        List<Rating> result = ratingService.getByMediaId("media-123");

        // Assert
        assertEquals(2, result.size());
        verify(ratingRepository).findByMediaId("media-123");
    }

}

