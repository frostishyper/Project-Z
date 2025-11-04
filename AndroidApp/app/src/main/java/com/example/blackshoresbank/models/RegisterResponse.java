package com.example.blackshoresbank.models;


// Handles Response Reception
public class RegisterResponse {
    private boolean success;
    private String message;
    private String error;
    private String accountNumber;

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

    public String getAccountNumber() {
        return accountNumber;
    }
}
