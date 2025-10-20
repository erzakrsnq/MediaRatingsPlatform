package org.example.application.services;

import org.example.application.model.User;
import org.example.application.repository.UserRepository;
import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        // Generate ID
        user.setId(UUID.randomUUID().toString());
        
        // Simple password hashing (in real app, use proper hashing)
        if (user.getPasswordHash() != null) {
            user.setPasswordHash("hashed_" + user.getPasswordHash());
        }
        
        return userRepository.save(user);
    }

    public User get(String id) {
        return userRepository.find(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User login(String username, String password) {
        return userRepository.findAll().stream()
                .filter(user -> username.equals(user.getUsername()))
                .filter(user -> ("hashed_" + password).equals(user.getPasswordHash()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }
}
