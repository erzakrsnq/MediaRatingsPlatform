package org.example.application.services;

import org.example.application.exception.EntityNotFoundException;
import org.example.application.model.Rating;
import org.example.application.repository.MediaRepository;
import org.example.application.repository.RatingRepository;
import java.util.List;
import java.util.UUID;

public class RatingService {

    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
    }

    public Rating create(Rating rating) {
        // Generate ID
        rating.setId(UUID.randomUUID().toString());
        
        // Neue Kommentare sind standardmäßig nicht bestätigt
        rating.setCommentConfirmed(false);
        
        Rating savedRating = ratingRepository.save(rating);
        
        // Durchschnitt aktualisieren
        updateAverageRating(rating.getMediaId());
        
        return savedRating;
    }

    public Rating get(String id) {
        Rating rating = ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        return hideUnconfirmedComment(rating);
    }

    public List<Rating> getAll() {
        List<Rating> ratings = ratingRepository.findAll();
        return ratings.stream()
                .map(this::hideUnconfirmedComment)
                .toList();
    }

    public List<Rating> getByMediaId(String mediaId) {
        List<Rating> ratings = ratingRepository.findByMediaId(mediaId);
        return ratings.stream()
                .map(this::hideUnconfirmedComment)
                .toList();
    }

    public List<Rating> getByUserId(String userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return ratings.stream()
                .map(this::hideUnconfirmedComment)
                .toList();
    }

    public Rating update(String id, Rating update) {
        Rating rating = ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);

        String mediaId = rating.getMediaId();
        rating.setRating(update.getRating());
        
        // Wenn Kommentar geändert wurde, Bestätigung zurücksetzen
        // Sonst bestehenden Bestätigungsstatus beibehalten
        if (update.getComment() != null && !update.getComment().equals(rating.getComment())) {
            rating.setCommentConfirmed(false);
        }
        // Wenn Kommentar nicht geändert wurde, bleibt commentConfirmed unverändert
        rating.setComment(update.getComment());

        Rating savedRating = ratingRepository.save(rating);
        
        // Durchschnitt aktualisieren
        updateAverageRating(mediaId);
        
        return savedRating;
    }

    public Rating confirmComment(String id) {
        Rating rating = ratingRepository.confirmComment(id);
        if (rating == null) {
            throw new EntityNotFoundException();
        }
        return rating;
    }

    public Rating delete(String id) {
        Rating rating = ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        String mediaId = rating.getMediaId();
        
        Rating deletedRating = ratingRepository.delete(id);
        
        // Durchschnitt aktualisieren
        updateAverageRating(mediaId);
        
        return deletedRating;
    }

    private Rating hideUnconfirmedComment(Rating rating) {
        if (rating != null && !rating.isCommentConfirmed()) {
            Rating filteredRating = new Rating();
            filteredRating.setId(rating.getId());
            filteredRating.setUserId(rating.getUserId());
            filteredRating.setMediaId(rating.getMediaId());
            filteredRating.setRating(rating.getRating());
            filteredRating.setComment(null); // Kommentar ausblenden
            filteredRating.setCommentConfirmed(rating.isCommentConfirmed());
            return filteredRating;
        }
        return rating;
    }

    /**
     * Berechnet und aktualisiert den durchschnittlichen Rating-Wert für ein Media.
     * Berechnet den Durchschnitt aller Ratings für das angegebene Media.
     */
    private void updateAverageRating(String mediaId) {
        List<Rating> ratings = ratingRepository.findByMediaId(mediaId);
        
        if (ratings.isEmpty()) {
            // Keine Ratings vorhanden, Durchschnitt auf 0.0 setzen
            mediaRepository.find(mediaId).ifPresent(media -> {
                media.setAverageRating(0.0);
                mediaRepository.save(media);
            });
        } else {
            // Durchschnitt berechnen
            double sum = ratings.stream()
                    .mapToInt(Rating::getRating)
                    .sum();
            double average = sum / ratings.size();
            
            // Media aktualisieren
            mediaRepository.find(mediaId).ifPresent(media -> {
                media.setAverageRating(average);
                mediaRepository.save(media);
            });
        }
    }
}
