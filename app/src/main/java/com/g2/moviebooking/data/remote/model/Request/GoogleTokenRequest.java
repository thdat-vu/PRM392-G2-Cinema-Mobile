package com.g2.moviebooking.data.remote.model.Request;

public class GoogleTokenRequest {
    private String idToken;

    public GoogleTokenRequest(String idToken) {
        this.idToken = idToken;
    }
}