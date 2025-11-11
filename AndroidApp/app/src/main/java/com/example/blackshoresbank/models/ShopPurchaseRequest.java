package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;

public class ShopPurchaseRequest {

    @SerializedName("UserNumber")
    private String userNumber;

    @SerializedName("Listing_ID")
    private int listingId;

    // UPDATED
    @SerializedName("recipientDetails")
    private RecipientDetails recipientDetails;

    // Constructor
    // UPDATED
    public ShopPurchaseRequest(String userNumber, int listingId, RecipientDetails recipientDetails) {
        this.userNumber = userNumber;
        this.listingId = listingId;
        this.recipientDetails = recipientDetails;
    }

    // Getters
    public String getUserNumber() {
        return userNumber;
    }

    public int getListingId() {
        return listingId;
    }

    // UPDATED
    public RecipientDetails getRecipientDetails() {
        return recipientDetails;
    }
}