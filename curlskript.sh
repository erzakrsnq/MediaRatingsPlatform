#!/bin/bash
# MediaRatingsPlatform API Curl Script

echo "MediaRatingsPlatform API Tests"
echo "=================================="

BASE_URL="http://localhost:8080"

print_section() {
    echo -e "\n=== $1 ==="
}

print_test() {
    echo -e "\nTesting: $1"
}

print_success() {
    echo -e "SUCCESS: $1"
}

print_error() {
    echo -e "ERROR: $1"
}

# Test 1: Root endpoint (sollte 404 geben)
print_section "Root Endpoint Test"
print_test "GET /"
response=$(curl -s -w "%{http_code}" -o /dev/null "$BASE_URL/")
if [ "$response" = "404" ]; then
    print_success "Root endpoint correctly returns 404"
else
    print_error "Root endpoint returned $response (expected 404)"
fi

# Test 2: Users Endpoints
print_section "Users Endpoints"

print_test "GET /users (should be empty initially)"
curl -s "$BASE_URL/users" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/users")"

print_test "POST /users (create test user)"
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "passwordHash": "password123"
  }')
echo "$USER_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $USER_RESPONSE"

# Extract user ID for later tests
USER_ID=$(echo "$USER_RESPONSE" | jq -r '.id' 2>/dev/null)
if [ "$USER_ID" != "null" ] && [ "$USER_ID" != "" ]; then
    print_success "User created with ID: $USER_ID"
    
    print_test "GET /users/$USER_ID"
    curl -s "$BASE_URL/users/$USER_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/users/$USER_ID")"
else
    print_error "Could not extract user ID from response"
fi

print_test "GET /users (should now contain the user)"
curl -s "$BASE_URL/users" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/users")"

# Test 3: Auth Endpoints
print_section "Auth Endpoints"

print_test "POST /auth/login"
AUTH_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')
echo "$AUTH_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $AUTH_RESPONSE"

# Extract token for logout test
TOKEN=$(echo "$AUTH_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ "$TOKEN" != "null" ] && [ "$TOKEN" != "" ]; then
    print_success "Login successful, token: ${TOKEN:0:20}..."
    
    print_test "POST /auth/logout"
    curl -s -X POST "$BASE_URL/auth/logout" \
      -H "Content-Type: application/json" \
      -d "{\"token\": \"$TOKEN\"}" | jq '.' 2>/dev/null || echo "Response: $(curl -s -X POST "$BASE_URL/auth/logout" -H "Content-Type: application/json" -d "{\"token\": \"$TOKEN\"}")"
else
    print_error "Could not extract token from login response"
fi

# Test 4: Media Endpoints
print_section "Media Endpoints"

print_test "GET /media (should be empty initially)"
curl -s "$BASE_URL/media" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/media")"

print_test "POST /media (create test media)"
MEDIA_RESPONSE=$(curl -s -X POST "$BASE_URL/media" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Inception",
    "description": "A mind-bending thriller about dreams within dreams",
    "type": "Movie",
    "genre": "Sci-Fi",
    "releaseYear": 2010,
    "director": "Christopher Nolan",
    "actors": "Leonardo DiCaprio, Marion Cotillard, Tom Hardy"
  }')
echo "$MEDIA_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $MEDIA_RESPONSE"

# Extract media ID for later tests
MEDIA_ID=$(echo "$MEDIA_RESPONSE" | jq -r '.id' 2>/dev/null)
if [ "$MEDIA_ID" != "null" ] && [ "$MEDIA_ID" != "" ]; then
    print_success "Media created with ID: $MEDIA_ID"
    
    print_test "GET /media/$MEDIA_ID"
    curl -s "$BASE_URL/media/$MEDIA_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/media/$MEDIA_ID")"
    
    print_test "PUT /media/$MEDIA_ID (update media)"
    curl -s -X PUT "$BASE_URL/media/$MEDIA_ID" \
      -H "Content-Type: application/json" \
      -d '{
        "title": "Inception (Updated)",
        "description": "A mind-bending thriller about dreams within dreams - UPDATED",
        "type": "Movie",
        "genre": "Sci-Fi Thriller",
        "releaseYear": 2010,
        "director": "Christopher Nolan",
        "actors": "Leonardo DiCaprio, Marion Cotillard, Tom Hardy, Joseph Gordon-Levitt"
      }' | jq '.' 2>/dev/null || echo "Response: $(curl -s -X PUT "$BASE_URL/media/$MEDIA_ID" -H "Content-Type: application/json" -d '{"title": "Inception (Updated)", "description": "A mind-bending thriller about dreams within dreams - UPDATED", "type": "Movie", "genre": "Sci-Fi Thriller", "releaseYear": 2010, "director": "Christopher Nolan", "actors": "Leonardo DiCaprio, Marion Cotillard, Tom Hardy, Joseph Gordon-Levitt"}')"
else
    print_error "Could not extract media ID from response"
fi

print_test "GET /media (should now contain the media)"
curl -s "$BASE_URL/media" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/media")"

# Test 5: Rating Endpoints
print_section "Rating Endpoints"

print_test "GET /ratings (should be empty initially)"
curl -s "$BASE_URL/ratings" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings")"

if [ "$USER_ID" != "null" ] && [ "$USER_ID" != "" ] && [ "$MEDIA_ID" != "null" ] && [ "$MEDIA_ID" != "" ]; then
    print_test "POST /ratings (create test rating)"
    RATING_RESPONSE=$(curl -s -X POST "$BASE_URL/ratings" \
      -H "Content-Type: application/json" \
      -d "{
        \"userId\": \"$USER_ID\",
        \"mediaId\": \"$MEDIA_ID\",
        \"rating\": 9,
        \"comment\": \"Amazing movie! Mind-blowing concept and execution.\"
      }")
    echo "$RATING_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $RATING_RESPONSE"
    
    # Extract rating ID for later tests
    RATING_ID=$(echo "$RATING_RESPONSE" | jq -r '.id' 2>/dev/null)
    if [ "$RATING_ID" != "null" ] && [ "$RATING_ID" != "" ]; then
        print_success "Rating created with ID: $RATING_ID"
        
        print_test "GET /ratings/$RATING_ID"
        curl -s "$BASE_URL/ratings/$RATING_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings/$RATING_ID")"
        
        print_test "PUT /ratings/$RATING_ID (update rating)"
        curl -s -X PUT "$BASE_URL/ratings/$RATING_ID" \
          -H "Content-Type: application/json" \
          -d "{
            \"userId\": \"$USER_ID\",
            \"mediaId\": \"$MEDIA_ID\",
            \"rating\": 10,
            \"comment\": \"Perfect movie! One of the best films ever made. UPDATED\"
          }" | jq '.' 2>/dev/null || echo "Response: $(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID" -H "Content-Type: application/json" -d "{\"userId\": \"$USER_ID\", \"mediaId\": \"$MEDIA_ID\", \"rating\": 10, \"comment\": \"Perfect movie! One of the best films ever made. UPDATED\"}")"
    fi
    
    print_test "GET /ratings/media/$MEDIA_ID (ratings for specific media)"
    curl -s "$BASE_URL/ratings/media/$MEDIA_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings/media/$MEDIA_ID")"
    
    print_test "GET /ratings/user/$USER_ID (ratings by specific user)"
    curl -s "$BASE_URL/ratings/user/$USER_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings/user/$USER_ID")"
else
    print_error "Cannot test ratings - missing user or media ID"
fi

print_test "GET /ratings (should now contain the rating)"
curl -s "$BASE_URL/ratings" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings")"

# Test 6: Error Cases
print_section "Error Cases"

print_test "GET /users/nonexistent (should return 404)"
curl -s -w "HTTP Status: %{http_code}\n" "$BASE_URL/users/nonexistent" | tail -1

print_test "GET /media/nonexistent (should return 404)"
curl -s -w "HTTP Status: %{http_code}\n" "$BASE_URL/media/nonexistent" | tail -1

print_test "POST /auth/login with wrong credentials (should return 401)"
curl -s -w "HTTP Status: %{http_code}\n" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "wronguser", "password": "wrongpass"}' | tail -1

print_test "POST /users with invalid JSON (should return 400)"
curl -s -w "HTTP Status: %{http_code}\n" -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d '{"invalid": json}' | tail -1

echo -e "\nAPI Tests completed!"
echo -e "Check the responses above for any errors."
echo -e "Note: If you see 'jq: command not found', install jq for better JSON formatting:"
echo -e "  brew install jq"
