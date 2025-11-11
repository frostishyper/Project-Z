package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;

// This is the response for the /prepare endpoint
public class ShopPurchasePrepareResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("error")
    private String error;

    @SerializedName("merchantName")
    private String merchantName;

    @SerializedName("listingName")
    private String listingName;

    @SerializedName("initialBalance")
    private double initialBalance;

    @SerializedName("cost")
    private double cost;

    @SerializedName("projectedNewBalance")
    private double projectedNewBalance;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getListingName() {
        return listingName;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public double getCost() {
        return cost;
    }

    public double getProjectedNewBalance() {
        return projectedNewBalance;
    }
}