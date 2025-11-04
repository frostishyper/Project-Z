package com.example.blackshoresbank.models;

public class RegisterRequest {

    // Fields must match the backend's expected JSON keys EXACTLY
    private String RegisterNumber;
    private String RegisterEmail;
    private String RegisterFirstname;
    private String RegisterLastname;
    private String RegisterUsername;
    private String RegisterPin;

    // Constructor used to create a registration request.
    // All parameters are required and will be serialized to JSON.
    public RegisterRequest (String registerNumber, String registerEmail,
                           String registerFirstname, String registerLastname,
                           String registerUsername, String registerPin)
    {
        this.RegisterNumber = registerNumber;
        this.RegisterEmail = registerEmail;
        this.RegisterFirstname = registerFirstname;
        this.RegisterLastname = registerLastname;
        this.RegisterUsername = registerUsername;
        this.RegisterPin = registerPin;
    }

    // Getters (used by Retrofit/Gson to serialize data)
    public String getRegisterNumber() {
        return RegisterNumber;
    }

    public String getRegisterEmail() {
        return RegisterEmail;
    }

    public String getRegisterFirstname() {
        return RegisterFirstname;
    }

    public String getRegisterLastname() {
        return RegisterLastname;
    }

    public String getRegisterUsername() {
        return RegisterUsername;
    }

    public String getRegisterPin() {
        return RegisterPin;
    }
}
