package org.example.application.repository;

import org.example.application.common.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbRatingLikeRepository implements RatingLikeRepository {

    private final ConnectionPool connectionPool;

    private static final String INSERT = "INSERT INTO rating_likes (rating_id, user_id) VALUES (?, ?)";
    private static final String DELETE = "DELETE FROM rating_likes WHERE rating_id = ? AND user_id = ?";
    private static final String EXISTS = "SELECT COUNT(*) FROM rating_likes WHERE rating_id = ? AND user_id = ?";
    private static final String COUNT_BY_RATING = "SELECT COUNT(*) FROM rating_likes WHERE rating_id = ?";
    private static final String SELECT_BY_RATING = "SELECT user_id FROM rating_likes WHERE rating_id = ?";

    public DbRatingLikeRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void add(String ratingId, String userId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT)) {
            pstmt.setString(1, ratingId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(String ratingId, String userId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE)) {
            pstmt.setString(1, ratingId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String ratingId, String userId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(EXISTS)) {
            pstmt.setString(1, ratingId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public int countByRatingId(String ratingId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_BY_RATING)) {
            pstmt.setString(1, ratingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public List<String> findUserIdsByRatingId(String ratingId) {
        List<String> userIds = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_RATING)) {
            pstmt.setString(1, ratingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getString("user_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userIds;
    }
}

