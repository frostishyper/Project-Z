package com.example.blackshoresbank.utils;

import android.content.Context;
import android.content.SharedPreferences;

// Manages Token Storage & Auth

public class TokenManager {
    private static final String PREF_NAME = "BlackShoresBank";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_ACCOUNT_NUMBER = "account_number";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveUserData(String accountNumber, String firstName, String lastName,
                             String username, String email) {
        prefs.edit()
                .putString(KEY_ACCOUNT_NUMBER, accountNumber)
                .putString(KEY_FIRST_NAME, firstName)
                .putString(KEY_LAST_NAME, lastName)
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public String getAccountNumber() {
        return prefs.getString(KEY_ACCOUNT_NUMBER, null);
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // Getter Methods
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getFirstName() {
        return prefs.getString(KEY_FIRST_NAME, null);
    }

    public String getLastName() {
        return prefs.getString(KEY_LAST_NAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

}