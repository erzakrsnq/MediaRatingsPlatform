package org.example.application.services;

import org.example.application.model.Media;
import org.example.application.repository.MediaRepository;
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
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(mediaRepository);
    }

    @Test
    void testCreate_GeneratesIdAndSetsOwner() {
        // Arrange
        Media media = new Media();
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Media result = mediaService.create(media, "owner-123");

        // Assert
        assertNotNull(result.getId());
        assertEquals("owner-123", result.getOwnerId());
        assertEquals(0.0, result.getAverageRating());
        verify(mediaRepository).save(any(Media.class));
    }

    @Test
    void testGet_ReturnsMedia() {
        // Arrange
        Media media = new Media();
        media.setId("media-123");
        when(mediaRepository.find("media-123")).thenReturn(Optional.of(media));

        // Act
        Media result = mediaService.get("media-123");

        // Assert
        assertEquals("media-123", result.getId());
        verify(mediaRepository).find("media-123");
    }

    @Test
    void testUpdate_OwnerCanUpdate() {
        // Arrange
        Media existingMedia = new Media();
        existingMedia.setOwnerId("owner-123");
        when(mediaRepository.find("media-123")).thenReturn(Optional.of(existingMedia));
        when(mediaRepository.save(any(Media.class))).thenReturn(existingMedia);

        // Act
        mediaService.update("media-123", new Media(), "owner-123");

        // Assert
        verify(mediaRepository).find("media-123");
        verify(mediaRepository).save(any(Media.class));
    }

    @Test
    void testDelete_OwnerCanDelete() {
        // Arrange
        Media media = new Media();
        media.setOwnerId("owner-123");
        when(mediaRepository.find("media-123")).thenReturn(Optional.of(media));
        when(mediaRepository.delete("media-123")).thenReturn(media);

        // Act
        mediaService.delete("media-123", "owner-123");

        // Assert
        verify(mediaRepository).find("media-123");
        verify(mediaRepository).delete("media-123");
    }

}

