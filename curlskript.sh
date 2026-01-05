#!/bin/bash

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
        UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID" \
          -H "Content-Type: application/json" \
          -d "{
            \"userId\": \"$USER_ID\",
            \"mediaId\": \"$MEDIA_ID\",
            \"rating\": 10,
            \"comment\": \"Perfect movie! One of the best films ever made. UPDATED\"
          }")
        echo "$UPDATE_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $UPDATE_RESPONSE"
        
        # Test: Kommentar-Bestätigung
        print_test "Check commentConfirmed after creation (should be false)"
        COMMENT_CONFIRMED=$(echo "$RATING_RESPONSE" | jq -r '.commentConfirmed' 2>/dev/null)
        if [ "$COMMENT_CONFIRMED" = "false" ]; then
            print_success "commentConfirmed is false after creation (correct)"
        else
            print_error "commentConfirmed should be false, but was: $COMMENT_CONFIRMED"
        fi
        
        print_test "PUT /ratings/$RATING_ID/confirm (confirm comment)"
        CONFIRM_RESPONSE=$(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID/confirm" \
          -H "Content-Type: application/json")
        echo "$CONFIRM_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $CONFIRM_RESPONSE"
        
        CONFIRMED_STATUS=$(echo "$CONFIRM_RESPONSE" | jq -r '.commentConfirmed' 2>/dev/null)
        if [ "$CONFIRMED_STATUS" = "true" ]; then
            print_success "Comment successfully confirmed (commentConfirmed: true)"
        else
            print_error "Comment should be confirmed, but commentConfirmed is: $CONFIRMED_STATUS"
        fi
        
        print_test "GET /ratings/$RATING_ID (verify comment is confirmed)"
        GET_CONFIRMED=$(curl -s "$BASE_URL/ratings/$RATING_ID" | jq -r '.commentConfirmed' 2>/dev/null)
        if [ "$GET_CONFIRMED" = "true" ]; then
            print_success "Comment confirmed status verified (commentConfirmed: true)"
        else
            print_error "Comment should be confirmed, but was: $GET_CONFIRMED"
        fi
        
        print_test "PUT /ratings/$RATING_ID (update comment - should reset confirmation)"
        UPDATE_COMMENT_RESPONSE=$(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID" \
          -H "Content-Type: application/json" \
          -d "{
            \"userId\": \"$USER_ID\",
            \"mediaId\": \"$MEDIA_ID\",
            \"rating\": 10,
            \"comment\": \"Updated comment: Even better after watching again!\"
          }")
        echo "$UPDATE_COMMENT_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $UPDATE_COMMENT_RESPONSE"
        
        UPDATED_CONFIRMED=$(echo "$UPDATE_COMMENT_RESPONSE" | jq -r '.commentConfirmed' 2>/dev/null)
        if [ "$UPDATED_CONFIRMED" = "false" ]; then
            print_success "Confirmation reset after comment update (commentConfirmed: false)"
        else
            print_error "Confirmation should be reset, but commentConfirmed is: $UPDATED_CONFIRMED"
        fi
        
        print_test "PUT /ratings/$RATING_ID/confirm (confirm again)"
        curl -s -X PUT "$BASE_URL/ratings/$RATING_ID/confirm" \
          -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "Response: $(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID/confirm" -H "Content-Type: application/json")"
        
        print_test "PUT /ratings/$RATING_ID (update rating only, keep comment - should keep confirmation)"
        UPDATE_RATING_ONLY=$(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID" \
          -H "Content-Type: application/json" \
          -d "{
            \"userId\": \"$USER_ID\",
            \"mediaId\": \"$MEDIA_ID\",
            \"rating\": 9,
            \"comment\": \"Updated comment: Even better after watching again!\"
          }")
        RATING_ONLY_CONFIRMED=$(echo "$UPDATE_RATING_ONLY" | jq -r '.commentConfirmed' 2>/dev/null)
        if [ "$RATING_ONLY_CONFIRMED" = "true" ]; then
            print_success "Confirmation kept when comment unchanged (commentConfirmed: true)"
        else
            print_error "Confirmation should be kept, but commentConfirmed is: $RATING_ONLY_CONFIRMED"
        fi
    fi
    
        # Test: Kommentar-Filterung (unbestätigte Kommentare sollten null sein)
        print_test "GET /ratings/media/$MEDIA_ID (check unconfirmed comment is null)"
        RATINGS_BEFORE_CONFIRM=$(curl -s "$BASE_URL/ratings/media/$MEDIA_ID")
        echo "$RATINGS_BEFORE_CONFIRM" | jq '.' 2>/dev/null || echo "Response: $RATINGS_BEFORE_CONFIRM"
        
        # Erstelle ein neues Rating mit unbestätigtem Kommentar
        print_test "POST /ratings (create rating with unconfirmed comment)"
        NEW_RATING_RESPONSE=$(curl -s -X POST "$BASE_URL/ratings" \
          -H "Content-Type: application/json" \
          -d "{
            \"userId\": \"$USER_ID\",
            \"mediaId\": \"$MEDIA_ID\",
            \"rating\": 8,
            \"comment\": \"This comment should be hidden until confirmed\"
          }")
        NEW_RATING_ID=$(echo "$NEW_RATING_RESPONSE" | jq -r '.id' 2>/dev/null)
        
        print_test "GET /ratings/media/$MEDIA_ID (unconfirmed comment should be null)"
        RATINGS_WITH_UNCONFIRMED=$(curl -s "$BASE_URL/ratings/media/$MEDIA_ID")
        echo "$RATINGS_WITH_UNCONFIRMED" | jq '.' 2>/dev/null || echo "Response: $RATINGS_WITH_UNCONFIRMED"
        
        # Prüfe ob der unbestätigte Kommentar null ist
        UNCONFIRMED_COMMENT=$(echo "$RATINGS_WITH_UNCONFIRMED" | jq -r ".[] | select(.id == \"$NEW_RATING_ID\") | .comment" 2>/dev/null)
        if [ "$UNCONFIRMED_COMMENT" = "null" ]; then
            print_success "Unconfirmed comment correctly hidden (null)"
        else
            print_error "Unconfirmed comment should be null, but was: $UNCONFIRMED_COMMENT"
        fi
        
        # Test: Durchschnittsberechnung
        print_test "GET /media/$MEDIA_ID (check averageRating after rating creation)"
        MEDIA_AFTER_RATING=$(curl -s "$BASE_URL/media/$MEDIA_ID")
        echo "$MEDIA_AFTER_RATING" | jq '.' 2>/dev/null || echo "Response: $MEDIA_AFTER_RATING"
        AVERAGE_RATING=$(echo "$MEDIA_AFTER_RATING" | jq -r '.averageRating' 2>/dev/null)
        if [ "$AVERAGE_RATING" != "null" ] && [ "$AVERAGE_RATING" != "0" ]; then
            print_success "Average rating calculated: $AVERAGE_RATING"
        else
            print_error "Average rating should be calculated, but was: $AVERAGE_RATING"
        fi
        
        # Test: Like-Funktionalität (benötigt Token)
        if [ "$TOKEN" != "null" ] && [ "$TOKEN" != "" ]; then
            print_test "POST /ratings/$RATING_ID/like (like rating)"
            LIKE_RESPONSE=$(curl -s -X POST "$BASE_URL/ratings/$RATING_ID/like" \
              -H "Content-Type: application/json" \
              -d "{\"token\": \"$TOKEN\"}")
            echo "$LIKE_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $LIKE_RESPONSE"
            
            LIKED_STATUS=$(echo "$LIKE_RESPONSE" | jq -r '.liked' 2>/dev/null)
            LIKE_COUNT=$(echo "$LIKE_RESPONSE" | jq -r '.likeCount' 2>/dev/null)
            if [ "$LIKED_STATUS" = "true" ] && [ "$LIKE_COUNT" != "null" ]; then
                print_success "Rating liked successfully (liked: $LIKED_STATUS, count: $LIKE_COUNT)"
            else
                print_error "Like failed or unexpected response"
            fi
            
            print_test "DELETE /ratings/$RATING_ID/like (unlike rating)"
            UNLIKE_RESPONSE=$(curl -s -X DELETE "$BASE_URL/ratings/$RATING_ID/like" \
              -H "Content-Type: application/json" \
              -d "{\"token\": \"$TOKEN\"}")
            echo "$UNLIKE_RESPONSE" | jq '.' 2>/dev/null || echo "Response: $UNLIKE_RESPONSE"
            
            UNLIKED_STATUS=$(echo "$UNLIKE_RESPONSE" | jq -r '.liked' 2>/dev/null)
            UNLIKE_COUNT=$(echo "$UNLIKE_RESPONSE" | jq -r '.likeCount' 2>/dev/null)
            if [ "$UNLIKED_STATUS" = "false" ]; then
                print_success "Rating unliked successfully (liked: $UNLIKED_STATUS, count: $UNLIKE_COUNT)"
            else
                print_error "Unlike failed or unexpected response"
            fi
        else
            print_error "Cannot test likes - missing token"
        fi
        
        print_test "GET /ratings/media/$MEDIA_ID (ratings for specific media)"
        curl -s "$BASE_URL/ratings/media/$MEDIA_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings/media/$MEDIA_ID")"
        
        print_test "GET /ratings/user/$USER_ID (ratings by specific user)"
        curl -s "$BASE_URL/ratings/user/$USER_ID" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings/user/$USER_ID")"
        
        # Test: Durchschnittsberechnung nach Update
        print_test "PUT /ratings/$RATING_ID (update rating - should recalculate average)"
        UPDATE_FOR_AVG=$(curl -s -X PUT "$BASE_URL/ratings/$RATING_ID" \
          -H "Content-Type: application/json" \
          -d "{
            \"userId\": \"$USER_ID\",
            \"mediaId\": \"$MEDIA_ID\",
            \"rating\": 5,
            \"comment\": \"Updated rating to test average calculation\"
          }")
        
        print_test "GET /media/$MEDIA_ID (check averageRating after rating update)"
        MEDIA_AFTER_UPDATE=$(curl -s "$BASE_URL/media/$MEDIA_ID")
        NEW_AVERAGE=$(echo "$MEDIA_AFTER_UPDATE" | jq -r '.averageRating' 2>/dev/null)
        echo "Average rating after update: $NEW_AVERAGE"
        if [ "$NEW_AVERAGE" != "$AVERAGE_RATING" ]; then
            print_success "Average rating updated from $AVERAGE_RATING to $NEW_AVERAGE"
        else
            print_error "Average rating should have changed, but stayed at $AVERAGE_RATING"
        fi
else
    print_error "Cannot test ratings - missing user or media ID"
fi

print_test "GET /ratings (should now contain the rating)"
curl -s "$BASE_URL/ratings" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings")"

# Test 6: DELETE Operations
print_section "DELETE Operations"

# Test: Durchschnittsberechnung nach Delete
if [ "$MEDIA_ID" != "null" ] && [ "$MEDIA_ID" != "" ]; then
    print_test "GET /media/$MEDIA_ID (check averageRating before rating deletion)"
    MEDIA_BEFORE_DELETE=$(curl -s "$BASE_URL/media/$MEDIA_ID")
    AVERAGE_BEFORE_DELETE=$(echo "$MEDIA_BEFORE_DELETE" | jq -r '.averageRating' 2>/dev/null)
    echo "Average rating before delete: $AVERAGE_BEFORE_DELETE"
fi

print_test "DELETE /ratings/$RATING_ID"
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE "$BASE_URL/ratings/$RATING_ID" | tail -1

# Test: Durchschnittsberechnung nach Delete
if [ "$MEDIA_ID" != "null" ] && [ "$MEDIA_ID" != "" ]; then
    print_test "GET /media/$MEDIA_ID (check averageRating after rating deletion)"
    MEDIA_AFTER_DELETE=$(curl -s "$BASE_URL/media/$MEDIA_ID")
    AVERAGE_AFTER_DELETE=$(echo "$MEDIA_AFTER_DELETE" | jq -r '.averageRating' 2>/dev/null)
    echo "Average rating after delete: $AVERAGE_AFTER_DELETE"
    if [ "$AVERAGE_AFTER_DELETE" != "$AVERAGE_BEFORE_DELETE" ]; then
        print_success "Average rating updated after deletion: $AVERAGE_AFTER_DELETE"
    else
        print_error "Average rating should have changed after deletion"
    fi
fi

print_test "DELETE /media/$MEDIA_ID"
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE "$BASE_URL/media/$MEDIA_ID" | tail -1

print_test "DELETE /users/$USER_ID"
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE "$BASE_URL/users/$USER_ID" | tail -1

print_test "GET /users (verify user deletion)"
curl -s "$BASE_URL/users" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/users")"

print_test "GET /media (verify media deletion)"
curl -s "$BASE_URL/media" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/media")"

print_test "GET /ratings (verify rating deletion)"
curl -s "$BASE_URL/ratings" | jq '.' 2>/dev/null || echo "Response: $(curl -s "$BASE_URL/ratings")"

# Test 7: Error Cases
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
