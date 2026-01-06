CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS media (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    genre VARCHAR(100),
    release_year INTEGER,
    director VARCHAR(255),
    actors TEXT,
    average_rating DOUBLE PRECISION DEFAULT 0.0,
    age_restriction INTEGER,
    owner_id VARCHAR(36) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ratings (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    media_id VARCHAR(36) NOT NULL REFERENCES media(id),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    comment_confirmed BOOLEAN DEFAULT FALSE,
    UNIQUE(user_id, media_id)
);

CREATE TABLE IF NOT EXISTS favorites (
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    media_id VARCHAR(36) NOT NULL REFERENCES media(id),
    PRIMARY KEY (user_id, media_id)
);

CREATE TABLE IF NOT EXISTS rating_likes (
    rating_id VARCHAR(36) NOT NULL REFERENCES ratings(id),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    PRIMARY KEY (rating_id, user_id)
);

-- Testdaten einfügen
-- Lösche alte Testdaten falls vorhanden
DELETE FROM rating_likes WHERE rating_id IN ('rating-001', 'rating-002', 'rating-003');
DELETE FROM favorites;
DELETE FROM ratings WHERE id IN ('rating-001', 'rating-002', 'rating-003');
DELETE FROM media WHERE id IN ('media-001', 'media-002', 'media-003', 'media-004');
DELETE FROM users WHERE id IN ('test-123', 'user-001', 'user-002');

INSERT INTO users (id, username, email, password_hash) VALUES
    ('test-123', 'testuser', 'test@example.com', 'hashed_password123'),
    ('user-001', 'alice', 'alice@example.com', 'hashed_alice123'),
    ('user-002', 'bob', 'bob@example.com', 'hashed_bob123');

INSERT INTO media (id, title, description, type, genre, release_year, director, actors, average_rating, age_restriction, owner_id) VALUES
    ('media-001', 'Inception', 'A mind-bending thriller about dreams within dreams', 'Movie', 'Sci-Fi', 2010, 'Christopher Nolan', 'Leonardo DiCaprio, Marion Cotillard, Tom Hardy', 0.0, NULL, 'test-123'),
    ('media-002', 'The Matrix', 'A computer hacker learns about the true nature of reality', 'Movie', 'Sci-Fi', 1999, 'The Wachowskis', 'Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss', 0.0, 16, 'user-001'),
    ('media-003', 'Pulp Fiction', 'The lives of two mob hitmen, a boxer, and more', 'Movie', 'Crime', 1994, 'Quentin Tarantino', 'John Travolta, Samuel L. Jackson, Uma Thurman', 0.0, 18, 'user-002'),
    ('media-004', 'Family Movie', 'A family-friendly movie', 'Movie', 'Family', 2019, 'Family Director', 'Actor Five, Actor Six', 0.0, NULL, 'test-123');

INSERT INTO ratings (id, user_id, media_id, rating, comment, comment_confirmed) VALUES
    ('rating-001', 'test-123', 'media-001', 5, 'Amazing movie!', true),
    ('rating-002', 'user-001', 'media-001', 4, 'Great concept', true),
    ('rating-003', 'test-123', 'media-002', 5, 'Mind-blowing!', false);

-- Aktualisiere average_rating für Media
UPDATE media SET average_rating = (
    SELECT AVG(rating)::DOUBLE PRECISION 
    FROM ratings 
    WHERE ratings.media_id = media.id
) WHERE id IN ('media-001', 'media-002');

