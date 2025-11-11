package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;

// This is the response for the /commit endpoint
public class ShopPurchaseCommitResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    @SerializedName("referenceID")
    private String referenceID;

    @SerializedName("newBalance")
    private double newBalance;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public double getNewBalance() {
        return newBalance;
    }
}