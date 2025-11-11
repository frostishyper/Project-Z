package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// This is the root response object for the /api/getshop call
public class GetShopResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("merchant")
    private String merchant;

    @SerializedName("listings")
    private List<Listing> listings;

    @SerializedName("error")
    private String error;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMerchant() {
        return merchant;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public String getError() {
        return error;
    }
}