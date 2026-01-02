package org.example.application.services;

import org.example.application.exception.EntityNotFoundException;
import org.example.application.model.Media;
import org.example.application.repository.MediaRepository;
import java.util.List;
import java.util.UUID;

public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media create(Media media, String ownerId) {
        // Generate ID
        media.setId(UUID.randomUUID().toString());
        
        // Initialize average rating
        media.setAverageRating(0.0);
        
        // Set owner
        media.setOwnerId(ownerId);
        
        return mediaRepository.save(media);
    }

    public Media get(String id) {
        return mediaRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Media> getAll() {
        return mediaRepository.findAll();
    }

    public Media update(String id, Media update, String userId) {
        Media media = mediaRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);

        // Check ownership
        if (media.getOwnerId() == null || !media.getOwnerId().equals(userId)) {
            throw new RuntimeException("Only the owner can update this media");
        }

        media.setTitle(update.getTitle());
        media.setDescription(update.getDescription());
        media.setType(update.getType());
        media.setGenre(update.getGenre());
        media.setReleaseYear(update.getReleaseYear());
        media.setDirector(update.getDirector());
        media.setActors(update.getActors());

        return mediaRepository.save(media);
    }

    public Media delete(String id, String userId) {
        Media media = mediaRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);

        // Check ownership
        if (media.getOwnerId() == null || !media.getOwnerId().equals(userId)) {
            throw new RuntimeException("Only the owner can delete this media");
        }

        return mediaRepository.delete(id);
    }
}
