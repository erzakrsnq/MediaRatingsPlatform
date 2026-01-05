package org.example.application.services;

import org.example.application.model.Media;
import org.example.application.model.Rating;
import org.example.application.repository.MediaRepository;
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
    
    @Mock
    private MediaRepository mediaRepository;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository, mediaRepository);
    }

    @Test
    void testCreate_GeneratesId() {
        // Arrange
        Rating rating = new Rating();
        rating.setMediaId("media-123");
        Media media = new Media();
        media.setId("media-123");
        
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByMediaId("media-123")).thenReturn(List.of(rating));
        when(mediaRepository.find("media-123")).thenReturn(Optional.of(media));
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Rating result = ratingService.create(rating);

        // Assert
        assertNotNull(result.getId());
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingRepository).findByMediaId("media-123");
        verify(mediaRepository).find("media-123");
        verify(mediaRepository).save(any(Media.class));
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
        existingRating.setMediaId("media-123");
        Media media = new Media();
        media.setId("media-123");
        
        when(ratingRepository.find("rating-123")).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(existingRating);
        when(ratingRepository.findByMediaId("media-123")).thenReturn(List.of(existingRating));
        when(mediaRepository.find("media-123")).thenReturn(Optional.of(media));
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ratingService.update("rating-123", new Rating());

        // Assert
        verify(ratingRepository).find("rating-123");
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingRepository).findByMediaId("media-123");
        verify(mediaRepository).find("media-123");
        verify(mediaRepository).save(any(Media.class));
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

