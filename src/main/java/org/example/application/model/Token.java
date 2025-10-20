package org.example.application.model;

public class Token {
    private String token;
    private String userId;
    private long expiresAt;
    private boolean expired;

    public Token() {
    }

    public Token(String token, String userId, long expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.expired = false;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expired || System.currentTimeMillis() > expiresAt;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
