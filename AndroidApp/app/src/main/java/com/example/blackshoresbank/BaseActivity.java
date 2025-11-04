package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.blackshoresbank.utils.TokenManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);

        // Check if activity requires authentication
        if (requiresAuth() && !tokenManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
    }

    // Override this in activities that need authentication
    protected boolean requiresAuth() {
        return true; // Default: require auth
    }

    protected void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}