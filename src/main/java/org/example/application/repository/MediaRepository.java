package org.example.application.repository;

import org.example.application.model.Media;
import java.util.List;
import java.util.Optional;

public interface MediaRepository {

    Optional<Media> find(String id);
    List<Media> findAll();
    Media save(Media media);
    Media delete(String id);
}
