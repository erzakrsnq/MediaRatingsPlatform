package org.example.application.repository;

import org.example.application.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryUserRepository implements UserRepository {
    private final List<User> users;

    public MemoryUserRepository() {
        users = new ArrayList<>();
    }

    @Override
    public Optional<User> find(String id) {
        return users.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        users.add(user);
        return user;
    }

    @Override
    public User delete(String id) {
        Optional<User> user = find(id);
        if (user.isPresent()) {
            users.remove(user.get());
            return user.get();
        }
        return null;
    }
}
