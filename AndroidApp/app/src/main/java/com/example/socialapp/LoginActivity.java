package com.example.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Track Pin Visibility State
    private boolean isPinVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Get References
        EditText numberInput = findViewById(R.id.LoginNumberInput);
        EditText pinInput = findViewById(R.id.LoginPinInput);
        ImageView toggleIcon = findViewById(R.id.TogglePinVisibility);

        // Limit Length
        numberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        pinInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        // Use Numeric Keypad
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // Click Listener For Eye Icon
        toggleIcon.setOnClickListener(v -> {
            if (isPinVisible) {
                // Hide the PIN
                pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                toggleIcon.setImageResource(R.drawable.eye_closed);
                isPinVisible = false;
            } else {
                // Show The Pin
                pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                toggleIcon.setImageResource(R.drawable.eye_open);
                isPinVisible = true;
            }

            // Keep The Cursor At The End
            pinInput.setSelection(pinInput.getText().length());
        });

        // Hyperlink to Register
        TextView registerNow = findViewById(R.id.RegisterNowText);
        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
