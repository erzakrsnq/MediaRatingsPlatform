package org.example.application.repository;

import org.example.application.common.ConnectionPool;
import org.example.application.model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbRatingRepository implements RatingRepository {

    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID = "SELECT * FROM ratings WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM ratings";
    private static final String SELECT_BY_MEDIA_ID = "SELECT * FROM ratings WHERE media_id = ?";
    private static final String SELECT_BY_USER_ID = "SELECT * FROM ratings WHERE user_id = ?";
    private static final String INSERT = "INSERT INTO ratings (id, user_id, media_id, rating, comment, comment_confirmed) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE ratings SET rating = ?, comment = ?, comment_confirmed = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM ratings WHERE id = ?";
    private static final String CONFIRM_COMMENT = "UPDATE ratings SET comment_confirmed = TRUE WHERE id = ?";

    public DbRatingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Rating> find(String id) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)
        ) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Rating> findAll() {
        List<Rating> ratings = new ArrayList<>();
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public List<Rating> findByMediaId(String mediaId) {
        List<Rating> ratings = new ArrayList<>();
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_MEDIA_ID)
        ) {
            pstmt.setString(1, mediaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public List<Rating> findByUserId(String userId) {
        List<Rating> ratings = new ArrayList<>();
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_USER_ID)
        ) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public Rating save(Rating rating) {
        Optional<Rating> existing = find(rating.getId());
        boolean isUpdate = existing.isPresent();
        
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        isUpdate ? UPDATE : INSERT
                )
        ) {
            if (isUpdate) {
               
                pstmt.setInt(1, rating.getRating());
                pstmt.setString(2, rating.getComment());
                pstmt.setBoolean(3, rating.isCommentConfirmed());
                pstmt.setString(4, rating.getId());
            } else {
                // Insert - neue Kommentare sind standardmäßig nicht bestätigt
                pstmt.setString(1, rating.getId());
                pstmt.setString(2, rating.getUserId());
                pstmt.setString(3, rating.getMediaId());
                pstmt.setInt(4, rating.getRating());
                pstmt.setString(5, rating.getComment());
                pstmt.setBoolean(6, rating.isCommentConfirmed());
            }
            pstmt.executeUpdate();
            return rating;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rating delete(String id) {
        Optional<Rating> rating = find(id);
        if (rating.isEmpty()) {
            return null;
        }

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(DELETE)
        ) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            return rating.get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rating confirmComment(String id) {
        Optional<Rating> rating = find(id);
        if (rating.isEmpty()) {
            return null;
        }

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(CONFIRM_COMMENT)
        ) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            
            // Return updated rating
            return find(id).orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getString("id"));
        rating.setUserId(rs.getString("user_id"));
        rating.setMediaId(rs.getString("media_id"));
        rating.setRating(rs.getInt("rating"));
        rating.setComment(rs.getString("comment"));
        rating.setCommentConfirmed(rs.getBoolean("comment_confirmed"));
        return rating;
    }
}

