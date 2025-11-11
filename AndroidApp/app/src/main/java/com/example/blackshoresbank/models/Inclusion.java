package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;

// This class maps to the items in the "inclusions" array
public class Inclusion {

    @SerializedName("inclusionId")
    private int inclusionId;

    @SerializedName("inclusionName")
    private String inclusionName;

    @SerializedName("quantity")
    private int quantity;

    // Getters
    public int getInclusionId() {
        return inclusionId;
    }

    public String getInclusionName() {
        return inclusionName;
    }

    public int getQuantity() {
        return quantity;
    }
}