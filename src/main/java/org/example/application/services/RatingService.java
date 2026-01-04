package org.example.application.services;

import org.example.application.exception.EntityNotFoundException;
import org.example.application.model.Rating;
import org.example.application.repository.RatingRepository;
import java.util.List;
import java.util.UUID;

public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating create(Rating rating) {
        // Generate ID
        rating.setId(UUID.randomUUID().toString());
        
        // Neue Kommentare sind standardmäßig nicht bestätigt
        rating.setCommentConfirmed(false);
        
        return ratingRepository.save(rating);
    }

    public Rating get(String id) {
        return ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Rating> getAll() {
        return ratingRepository.findAll();
    }

    public List<Rating> getByMediaId(String mediaId) {
        return ratingRepository.findByMediaId(mediaId);
    }

    public List<Rating> getByUserId(String userId) {
        return ratingRepository.findByUserId(userId);
    }

    public Rating update(String id, Rating update) {
        Rating rating = ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);

        rating.setRating(update.getRating());
        
        // Wenn Kommentar geändert wurde, Bestätigung zurücksetzen
        // Sonst bestehenden Bestätigungsstatus beibehalten
        if (update.getComment() != null && !update.getComment().equals(rating.getComment())) {
            rating.setCommentConfirmed(false);
        }
        // Wenn Kommentar nicht geändert wurde, bleibt commentConfirmed unverändert
        rating.setComment(update.getComment());

        return ratingRepository.save(rating);
    }

    public Rating confirmComment(String id) {
        Rating rating = ratingRepository.confirmComment(id);
        if (rating == null) {
            throw new EntityNotFoundException();
        }
        return rating;
    }

    public Rating delete(String id) {
        return ratingRepository.delete(id);
    }
}
