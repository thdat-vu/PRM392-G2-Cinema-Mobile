package com.g2.moviebooking.data.remote.model;

public class GoogleTokenRequest {
    private String idToken;

    public GoogleTokenRequest(String idToken) {
        this.idToken = idToken;
    }
}