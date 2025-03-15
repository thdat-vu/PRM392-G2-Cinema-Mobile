package com.g2.moviebooking.data.remote.model.Response;

public class RegistrationResponse {
    private boolean success;
    private User data;
    private String message;
    private Object[] errors;

    public static class User {
        private String _id;
        private String name;
        private String email;
        private String phoneNumber;
        private String role;
        private boolean isDeleted;
        private String createdAt;
        private String updatedAt;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public User getData() { return data; }
    public String getMessage() { return message; }
}
