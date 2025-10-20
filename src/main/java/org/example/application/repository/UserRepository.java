package org.example.application.repository;

import org.example.application.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> find(String id);
    List<User> findAll();
    User save(User user);
    User delete(String id);
}
