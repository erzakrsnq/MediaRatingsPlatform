package org.example.application.repository;

import java.util.List;

public interface FavoriteRepository {
    void add(String userId, String mediaId);
    void remove(String userId, String mediaId);
    List<String> findByUserId(String userId);
}

