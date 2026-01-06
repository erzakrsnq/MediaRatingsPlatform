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

