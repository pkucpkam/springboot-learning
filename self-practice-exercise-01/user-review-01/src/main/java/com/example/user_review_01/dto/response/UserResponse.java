package com.example.user_review_01.dto.response;

import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String username;
    private String email;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
