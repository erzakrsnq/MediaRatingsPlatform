package org.example.application.services;

import org.example.application.repository.RatingLikeRepository;

public class RatingLikeService {

    private final RatingLikeRepository ratingLikeRepository;

    public RatingLikeService(RatingLikeRepository ratingLikeRepository) {
        this.ratingLikeRepository = ratingLikeRepository;
    }

    public void likeRating(String ratingId, String userId) {
        if (!ratingLikeRepository.exists(ratingId, userId)) {
            ratingLikeRepository.add(ratingId, userId);
        }
    }

    public void unlikeRating(String ratingId, String userId) {
        ratingLikeRepository.remove(ratingId, userId);
    }

    public boolean isLiked(String ratingId, String userId) {
        return ratingLikeRepository.exists(ratingId, userId);
    }

    public int getLikeCount(String ratingId) {
        return ratingLikeRepository.countByRatingId(ratingId);
    }
}

