package com.example.blackshoresbank;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Get user details from TokenManager
        String firstName = tokenManager.getFirstName();
        String lastName = tokenManager.getLastName();
        String username = tokenManager.getUsername();
        String accountNumber = tokenManager.getAccountNumber();
        String email = tokenManager.getEmail();

        // Set user details in UI
        TextView fullName = findViewById(R.id.User_FullName);
        TextView handle = findViewById(R.id.User_Handle);
        TextView number = findViewById(R.id.User_Number);
        TextView userEmail = findViewById(R.id.User_Email);

        fullName.setText(firstName + " " + lastName);
        handle.setText("@" + username);
        number.setText(accountNumber);
        userEmail.setText(email);

        // Back button
        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> finish());

        // Logout button
        Button logoutBtn = findViewById(R.id.Logout_Btn);
        logoutBtn.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        // Clear all stored data (token, user info)
        tokenManager.clearAll();

        // Navigate back to LoginActivity and clear back stack
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // ensure this activity is removed from stack
    }
}
