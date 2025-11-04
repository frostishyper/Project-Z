package com.example.blackshoresbank.models;

public class LoginRequest {

    // Fields must match the backend's expected JSON keys EXACTLY
    private String LoginNumber;
    private String LoginPin;

    // Constructor used to create a registration request.
    // All parameters are required and will be serialized to JSON.

    public LoginRequest(String loginNumber, String loginPin) {
        this.LoginNumber = loginNumber;
        this.LoginPin = loginPin;
    }

    // Getters (used by Retrofit/Gson to serialize data)
    public String getLoginNumber() {
        return LoginNumber;
    }

    public String getLoginPin() {
        return LoginPin;
    }
}