package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// This class maps to the items in the "listings" array
public class Listing {

    @SerializedName("listingId")
    private int listingId;

    @SerializedName("listingName")
    private String listingName;

    @SerializedName("listingPrice")
    private double listingPrice;

    @SerializedName("listingIconName")
    private String listingIconName;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("inclusions")
    private List<Inclusion> inclusions;

    // Getters
    public int getListingId() {
        return listingId;
    }

    public String getListingName() {
        return listingName;
    }

    public double getListingPrice() {
        return listingPrice;
    }

    public String getListingIconName() {
        return listingIconName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<Inclusion> getInclusions() {
        return inclusions;
    }
}