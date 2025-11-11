package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;

// This model is used to send the merchant_name in the body of a POST request
public class GetShopRequest {

    @SerializedName("merchant_name")
    private String merchantName;

    // Constructor to create the request object
    public GetShopRequest(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantName() {
        return merchantName;
    }
}