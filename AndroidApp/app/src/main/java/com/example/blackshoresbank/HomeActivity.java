package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Set username from token
        TextView namePlaceholder = findViewById(R.id.Home_NamePlaceholder);
        String username = tokenManager.getUsername();
        if (username != null) {
            namePlaceholder.setText(username);
        }

        // Navigate to Profile Page
        LinearLayout userContainer = findViewById(R.id.UserContainer);
        userContainer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
