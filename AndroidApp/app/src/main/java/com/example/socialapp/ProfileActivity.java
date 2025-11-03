package com.example.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
public class ProfileActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reference Profile Page
        setContentView(R.layout.profile);

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> finish());
    }

}
