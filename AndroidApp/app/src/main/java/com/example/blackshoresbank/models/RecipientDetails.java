package com.example.blackshoresbank.models;

import com.google.gson.annotations.SerializedName;


public class RecipientDetails {

    @SerializedName("steamEmail")
    private String steamEmail;

    @SerializedName("wuwaUserID")
    private String wuwaUserID;

    //  Constructors
    public RecipientDetails() {
        // Default constructor
    }

    // Setters
    public void setSteamEmail(String steamEmail) {
        this.steamEmail = steamEmail;
    }

    public void setWuwaUserID(String wuwaUserID) {
        this.wuwaUserID = wuwaUserID;
    }

    // GETTERS
    public String getSteamEmail() {
        return steamEmail;
    }

    public String getWuwaUserID() {
        return wuwaUserID;
    }
    // ----------------------
}