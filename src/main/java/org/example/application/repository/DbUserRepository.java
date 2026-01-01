package org.example.application.repository;

import org.example.application.common.ConnectionPool;
import org.example.application.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbUserRepository implements UserRepository {

    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM users";
    private static final String SELECT_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String INSERT = "INSERT INTO users (id, username, email, password_hash) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE users SET username = ?, email = ?, password_hash = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM users WHERE id = ?";

    public DbUserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<User> find(String id) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)
        ) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));

                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User save(User user) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        find(user.getId()).isPresent() ? UPDATE : INSERT
                )
        ) {
            if (find(user.getId()).isPresent()) {
                // Update
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPasswordHash());
                pstmt.setString(4, user.getId());
            } else {
                // Insert
                pstmt.setString(1, user.getId());
                pstmt.setString(2, user.getUsername());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getPasswordHash());
            }
            pstmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User delete(String id) {
        Optional<User> user = find(id);
        if (user.isEmpty()) {
            return null;
        }

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(DELETE)
        ) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            return user.get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_USERNAME)
        ) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));

                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

