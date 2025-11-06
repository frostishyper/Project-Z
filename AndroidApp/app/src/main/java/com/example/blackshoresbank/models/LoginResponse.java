package com.example.blackshoresbank.models;

// Handles Response Reception
public class LoginResponse {
    private boolean success;
    private String message;
    private String error;
    private String token;
    private User user;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private String accountNumber;
        private String firstName;
        private String lastName;
        private String username;
        private String email;

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}