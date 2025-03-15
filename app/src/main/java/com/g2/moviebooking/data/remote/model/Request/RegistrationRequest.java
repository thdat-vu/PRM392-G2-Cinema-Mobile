package com.g2.moviebooking.data.remote.model.Request;

public class RegistrationRequest {
    private String name;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    private boolean isDeleted;

    public RegistrationRequest(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "user";
        this.phoneNumber = phoneNumber;
        this.isDeleted = false;
    }

    // Getters and setters
}
