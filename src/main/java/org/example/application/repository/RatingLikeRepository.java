package org.example.application.repository;

import java.util.List;

public interface RatingLikeRepository {
    void add(String ratingId, String userId);
    void remove(String ratingId, String userId);
    boolean exists(String ratingId, String userId);
    int countByRatingId(String ratingId);
    List<String> findUserIdsByRatingId(String ratingId);
}

