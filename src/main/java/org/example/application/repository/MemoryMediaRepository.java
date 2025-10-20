package org.example.application.repository;

import org.example.application.model.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryMediaRepository implements MediaRepository {
    private final List<Media> mediaList;

    public MemoryMediaRepository() {
        mediaList = new ArrayList<>();
    }

    @Override
    public Optional<Media> find(String id) {
        return mediaList.stream()
                .filter(media -> id.equals(media.getId()))
                .findFirst();
    }

    @Override
    public List<Media> findAll() {
        return mediaList;
    }

    @Override
    public Media save(Media media) {
        mediaList.add(media);
        return media;
    }

    @Override
    public Media delete(String id) {
        Optional<Media> media = find(id);
        if (media.isPresent()) {
            mediaList.remove(media.get());
            return media.get();
        }
        return null;
    }
}
