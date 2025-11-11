package com.example.blackshoresbank.models;

// Response model for Card Cash-In API call.
// Contains result message and updated wallet info if successful.

public class CardCashInResponse {
    private boolean success;
    private String message;
    private double credited;
    private double fee;
    private double chargedTotal;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message != null ? message : error;
    }

    public double getCredited() {
        return credited;
    }

    public double getFee() {
        return fee;
    }

    public double getChargedTotal() {
        return chargedTotal;
    }

    public String getError() {
        return error;
    }
}
