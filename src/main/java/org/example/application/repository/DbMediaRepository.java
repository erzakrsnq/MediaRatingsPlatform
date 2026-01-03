package org.example.application.repository;

import org.example.application.common.ConnectionPool;
import org.example.application.model.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbMediaRepository implements MediaRepository {

    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID = "SELECT * FROM media WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM media";
    private static final String INSERT = "INSERT INTO media (id, title, description, type, genre, release_year, director, actors, average_rating, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE media SET title = ?, description = ?, type = ?, genre = ?, release_year = ?, director = ?, actors = ?, average_rating = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM media WHERE id = ?";

    public DbMediaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Media> find(String id) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)
        ) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapResultSetToMedia(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Media> findAll() {
        List<Media> mediaList = new ArrayList<>();
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                mediaList.add(mapResultSetToMedia(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public Media save(Media media) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        find(media.getId()).isPresent() ? UPDATE : INSERT
                )
        ) {
            if (find(media.getId()).isPresent()) {
                // Update
                pstmt.setString(1, media.getTitle());
                pstmt.setString(2, media.getDescription());
                pstmt.setString(3, media.getType());
                pstmt.setString(4, media.getGenre());
                pstmt.setInt(5, media.getReleaseYear());
                pstmt.setString(6, media.getDirector());
                pstmt.setString(7, media.getActors());
                pstmt.setDouble(8, media.getAverageRating());
                pstmt.setString(9, media.getId());
            } else {
                // Insert
                pstmt.setString(1, media.getId());
                pstmt.setString(2, media.getTitle());
                pstmt.setString(3, media.getDescription());
                pstmt.setString(4, media.getType());
                pstmt.setString(5, media.getGenre());
                pstmt.setInt(6, media.getReleaseYear());
                pstmt.setString(7, media.getDirector());
                pstmt.setString(8, media.getActors());
                pstmt.setDouble(9, media.getAverageRating());
                pstmt.setString(10, media.getOwnerId());
            }
            pstmt.executeUpdate();
            return media;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Media delete(String id) {
        Optional<Media> media = find(id);
        if (media.isEmpty()) {
            return null;
        }

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(DELETE)
        ) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            return media.get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Media> search(String title, String genre, String type, Double minRating) {
        List<Media> mediaList = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM media WHERE 1=1");
        
        if (title != null && !title.isEmpty()) {
            query.append(" AND LOWER(title) LIKE ?");
        }
        if (genre != null && !genre.isEmpty()) {
            query.append(" AND LOWER(genre) = ?");
        }
        if (type != null && !type.isEmpty()) {
            query.append(" AND LOWER(type) = ?");
        }
        if (minRating != null) {
            query.append(" AND average_rating >= ?");
        }
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (title != null && !title.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + title.toLowerCase() + "%");
            }
            if (genre != null && !genre.isEmpty()) {
                pstmt.setString(paramIndex++, genre.toLowerCase());
            }
            if (type != null && !type.isEmpty()) {
                pstmt.setString(paramIndex++, type.toLowerCase());
            }
            if (minRating != null) {
                pstmt.setDouble(paramIndex++, minRating);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getString("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setType(rs.getString("type"));
        media.setGenre(rs.getString("genre"));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setDirector(rs.getString("director"));
        media.setActors(rs.getString("actors"));
        media.setAverageRating(rs.getDouble("average_rating"));
        media.setOwnerId(rs.getString("owner_id"));
        return media;
    }
}

