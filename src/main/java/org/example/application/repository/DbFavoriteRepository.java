package org.example.application.repository;

import org.example.application.common.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbFavoriteRepository implements FavoriteRepository {

    private final ConnectionPool connectionPool;

    private static final String INSERT = "INSERT INTO favorites (user_id, media_id) VALUES (?, ?)";
    private static final String DELETE = "DELETE FROM favorites WHERE user_id = ? AND media_id = ?";
    private static final String SELECT_BY_USER = "SELECT media_id FROM favorites WHERE user_id = ?";

    public DbFavoriteRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void add(String userId, String mediaId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, mediaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(String userId, String mediaId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, mediaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findByUserId(String userId) {
        List<String> mediaIds = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_USER)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mediaIds.add(rs.getString("media_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaIds;
    }
}

