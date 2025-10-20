package org.example.application.services;

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
        
        return ratingRepository.save(rating);
    }

    public Rating get(String id) {
        return ratingRepository.find(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
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
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        rating.setRating(update.getRating());
        rating.setComment(update.getComment());

        return ratingRepository.save(rating);
    }

    public Rating delete(String id) {
        return ratingRepository.delete(id);
    }
}
