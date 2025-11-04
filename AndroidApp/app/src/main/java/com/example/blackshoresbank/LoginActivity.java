package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

// Networking Imports
import android.widget.Toast;
import com.example.blackshoresbank.models.LoginRequest;
import com.example.blackshoresbank.models.LoginResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;
import com.example.blackshoresbank.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private boolean isPinVisible = false;
    private EditText numberInput;
    private EditText pinInput;
    private Button loginButton;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize TokenManager
        tokenManager = new TokenManager(this);

        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // Get References
        numberInput = findViewById(R.id.LoginNumber);
        pinInput = findViewById(R.id.LoginPin);
        ImageView toggleIcon = findViewById(R.id.TogglePinVisibility);
        loginButton = findViewById(R.id.LoginButton);

        // Limit Length
        numberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        pinInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        // Use Numeric Keypad
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // Click Listener For Eye Icon
        toggleIcon.setOnClickListener(v -> {
            if (isPinVisible) {
                pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                toggleIcon.setImageResource(R.drawable.eye_closed);
                isPinVisible = false;
            } else {
                pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                toggleIcon.setImageResource(R.drawable.eye_open);
                isPinVisible = true;
            }
            pinInput.setSelection(pinInput.getText().length());
        });

        // Hyperlink to Register
        TextView registerNow = findViewById(R.id.RegisterNowText);
        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Login Button Click
        loginButton.setOnClickListener(v -> handleLogin());
    }

    // Backend Communications & API Calls Start Here

    private void handleLogin() {
        String number = numberInput.getText().toString().trim();
        String pin = pinInput.getText().toString().trim();

        // Basic validation
        if (number.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "Please enter account number and PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.length() != 6) {
            Toast.makeText(this, "PIN must be 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button during request
        loginButton.setEnabled(false);
        loginButton.setAlpha(0.5f);

        // Create request
        LoginRequest request = new LoginRequest(number, pin);

        // Make API call
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.LoginAccount(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse result = response.body();

                    if (result.isSuccess()) {
                        // Save token and user data
                        tokenManager.saveToken(result.getToken());

                        LoginResponse.User user = result.getUser();
                        tokenManager.saveUserData(
                                user.getAccountNumber(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getUsername(),
                                user.getEmail()
                        );

                        Toast.makeText(LoginActivity.this,
                                "Login successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to home
                        navigateToHome();

                    } else {
                        // Error from backend
                        Toast.makeText(LoginActivity.this,
                                result.getError(), Toast.LENGTH_LONG).show();
                        clearInputs();
                        loginButton.setEnabled(true);
                        loginButton.setAlpha(1f);
                    }

                } else {
                    // HTTP error
                    Toast.makeText(LoginActivity.this,
                            "Login failed. Please check your credentials.", Toast.LENGTH_LONG).show();
                    clearInputs();
                    loginButton.setEnabled(true);
                    loginButton.setAlpha(1f);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Network error: Unable to connect to server", Toast.LENGTH_LONG).show();
                clearInputs();
                loginButton.setEnabled(true);
                loginButton.setAlpha(1f);
            }
        });
    }

    private void clearInputs() {
        numberInput.setText("");
        pinInput.setText("");
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
