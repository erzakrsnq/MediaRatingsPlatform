package org.example.application.services;

import org.example.application.model.Media;
import org.example.application.repository.FavoriteRepository;
import org.example.application.repository.MediaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, MediaRepository mediaRepository) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    public void addFavorite(String userId, String mediaId) {
        favoriteRepository.add(userId, mediaId);
    }

    public void removeFavorite(String userId, String mediaId) {
        favoriteRepository.remove(userId, mediaId);
    }

    public List<Media> getFavoritesByUserId(String userId) {
        List<String> mediaIds = favoriteRepository.findByUserId(userId);
        List<Media> favorites = new ArrayList<>();
        for (String mediaId : mediaIds) {
            Optional<Media> media = mediaRepository.find(mediaId);
            if (media.isPresent()) {
                favorites.add(media.get());
            }
        }
        return favorites;
    }
}

