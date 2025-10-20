package org.example.application.services;

import org.example.application.model.Token;
import org.example.application.model.User;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    
    private final UserService userService;
    private final Map<String, Token> activeTokens = new ConcurrentHashMap<>();

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public Token login(String username, String password) {
        try {
            User user = userService.login(username, password);
            
            // Generate token
            String tokenValue = UUID.randomUUID().toString();
            long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours
            
            Token token = new Token(tokenValue, user.getId(), expiresAt);
            activeTokens.put(tokenValue, token);
            
            return token;
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid credentials", e);
        }
    }

    public void logout(String tokenValue) {
        activeTokens.get(tokenValue).setExpired(true);
    }

    public Optional<Token> getToken(String tokenValue) {
        return Optional.ofNullable(activeTokens.get(tokenValue))
                .filter(token -> !token.isExpired() && token.getExpiresAt() > System.currentTimeMillis());
    }
}
