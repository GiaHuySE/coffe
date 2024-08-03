package com.example.model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    // Constructor
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getter for access token
    public String getAccessToken() {
        return accessToken;
    }

    // Setter for access token
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter for refresh token
    public String getRefreshToken() {
        return refreshToken;
    }

    // Setter for refresh token
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
