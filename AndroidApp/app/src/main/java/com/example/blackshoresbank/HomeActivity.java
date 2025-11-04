package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reference Home Page
        setContentView(R.layout.home);

        // Go To Profile Page
        LinearLayout userContainer = findViewById(R.id.UserContainer);
        userContainer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class); // Move Page
            startActivity(intent);
        });
    }

}
