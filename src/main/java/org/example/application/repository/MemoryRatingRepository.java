package org.example.application.repository;

import org.example.application.model.Rating;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryRatingRepository implements RatingRepository {
    private final List<Rating> ratings;

    public MemoryRatingRepository() {
        ratings = new ArrayList<>();
    }

    @Override
    public Optional<Rating> find(String id) {
        return ratings.stream()
                .filter(rating -> id.equals(rating.getId()))
                .findFirst();
    }

    @Override
    public List<Rating> findAll() {
        return ratings;
    }

    @Override
    public List<Rating> findByMediaId(String mediaId) {
        return ratings.stream()
                .filter(rating -> mediaId.equals(rating.getMediaId()))
                .toList();
    }

    @Override
    public List<Rating> findByUserId(String userId) {
        return ratings.stream()
                .filter(rating -> userId.equals(rating.getUserId()))
                .toList();
    }

    @Override
    public Rating save(Rating rating) {
        ratings.add(rating);
        return rating;
    }

    @Override
    public Rating delete(String id) {
        Optional<Rating> rating = find(id);
        if (rating.isPresent()) {
            ratings.remove(rating.get());
            return rating.get();
        }
        return null;
    }
}
