package org.example.application.repository;

import org.example.application.model.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingRepository {

    Optional<Rating> find(String id);
    List<Rating> findAll();
    List<Rating> findByMediaId(String mediaId);
    List<Rating> findByUserId(String userId);
    Rating save(Rating rating);
    Rating delete(String id);
}
