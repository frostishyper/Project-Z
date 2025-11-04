package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

// Networking Imports
import android.widget.Toast;
import com.example.blackshoresbank.models.RegisterRequest;
import com.example.blackshoresbank.models.RegisterResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private boolean isPinVisible = false;
    private boolean isConfirmVisible = false;

    // Declare UI elements as class variables so validateForm() can access them
    private EditText registerNumber, registerEmail, registerFirstname, registerLastname, registerUsername, registerPin, registerConfirm;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Reference inputs
        registerNumber = findViewById(R.id.RegisterNumber);
        registerEmail = findViewById(R.id.RegisterEmail);
        registerFirstname = findViewById(R.id.RegisterFirstname);
        registerLastname = findViewById(R.id.RegisterLastname);
        registerUsername = findViewById(R.id.RegisterUsername);
        registerPin = findViewById(R.id.RegisterPin);
        registerConfirm = findViewById(R.id.RegisterConfirm);

        // Reference button
        registerButton = findViewById(R.id.RegisterButton);

        // Reference toggle icons
        ImageView togglePin = findViewById(R.id.TogglePinVisibility);
        ImageView toggleConfirm = findViewById(R.id.ToggleConfirmVisibility);

        // Make Number field numeric
        registerNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        registerNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

        // Make PIN fields numeric and hidden by default
        registerPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        registerConfirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // PIN visibility toggle logic
        togglePin.setOnClickListener(v -> {
            if (isPinVisible) {
                registerPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                togglePin.setImageResource(R.drawable.eye_closed);
            } else {
                registerPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                togglePin.setImageResource(R.drawable.eye_open);
            }
            isPinVisible = !isPinVisible;
            registerPin.setSelection(registerPin.getText().length());
        });

        // Confirm visibility toggle logic
        toggleConfirm.setOnClickListener(v -> {
            if (isConfirmVisible) {
                registerConfirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                toggleConfirm.setImageResource(R.drawable.eye_closed);
            } else {
                registerConfirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                toggleConfirm.setImageResource(R.drawable.eye_open);
            }
            isConfirmVisible = !isConfirmVisible;
            registerConfirm.setSelection(registerConfirm.getText().length());
        });

        // Disable register button initially
        registerButton.setEnabled(false);
        registerButton.setAlpha(0.5f);

        // PIN validation (must be exactly 6 digits)
        registerPin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String pin = s.toString();
                String confirm = registerConfirm.getText().toString();

                if (pin.length() == 6) {
                    registerPin.setBackgroundResource(R.drawable.input_field_bg);
                } else {
                    registerPin.setBackgroundResource(R.drawable.input_field_error_bg);
                }

                if (!confirm.isEmpty()) {
                    if (confirm.equals(pin)) {
                        registerConfirm.setBackgroundResource(R.drawable.input_field_bg);
                    } else {
                        registerConfirm.setBackgroundResource(R.drawable.input_field_error_bg);
                    }
                }

                validateForm();
            }
        });

        // Confirm validation (must match PIN)
        registerConfirm.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String confirm = s.toString();
                String pin = registerPin.getText().toString();

                if (confirm.equals(pin)) {
                    registerConfirm.setBackgroundResource(R.drawable.input_field_bg);
                } else {
                    registerConfirm.setBackgroundResource(R.drawable.input_field_error_bg);
                }

                validateForm();
            }
        });

        // Shared validation watcher for other fields
        TextWatcher sharedWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                validateForm();
            }
        };

        registerNumber.addTextChangedListener(sharedWatcher);
        registerEmail.addTextChangedListener(sharedWatcher);
        registerFirstname.addTextChangedListener(sharedWatcher);
        registerLastname.addTextChangedListener(sharedWatcher);
        registerUsername.addTextChangedListener(sharedWatcher);

        // Hyperlink to Login
        TextView signInNow = findViewById(R.id.SignInNowText);
        signInNow.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


        // Backend Communications & API Calls Start Here

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            // Disable button to prevent double-submission
            registerButton.setEnabled(false);
            registerButton.setAlpha(0.5f);

            // Get form data
            String number = registerNumber.getText().toString().trim();
            String email = registerEmail.getText().toString().trim();
            String firstname = registerFirstname.getText().toString().trim();
            String lastname = registerLastname.getText().toString().trim();
            String username = registerUsername.getText().toString().trim();
            String pin = registerPin.getText().toString().trim();

            // Create request object
            RegisterRequest request = new RegisterRequest(
                    number, email, firstname, lastname, username, pin
            );

            // Get API service and make the call
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<RegisterResponse> call = apiService.RegisterAccount(request);

            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RegisterResponse result = response.body();

                        if (result.isSuccess()) {
                            // SUCCESS - Clear fields and show message
                            Toast.makeText(RegisterActivity.this,
                                    result.getMessage(), Toast.LENGTH_LONG).show();

                            // Clear all fields
                            registerNumber.setText("");
                            registerEmail.setText("");
                            registerFirstname.setText("");
                            registerLastname.setText("");
                            registerUsername.setText("");
                            registerPin.setText("");
                            registerConfirm.setText("");

                            // Navigate to login after short delay
                            registerButton.postDelayed(() -> {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }, 1500);

                        } else {
                            // Backend returned error (validation failed, duplicate, etc.)
                            Toast.makeText(RegisterActivity.this,
                                    result.getError(), Toast.LENGTH_LONG).show();

                            // Re-enable button
                            registerButton.setEnabled(true);
                            registerButton.setAlpha(1f);
                        }

                    } else {
                        // HTTP error response (400, 409, 500, etc.)
                        String errorMsg = "Registration failed";

                        // Try to get error from response body
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                            } catch (Exception e) {
                                errorMsg = "Registration failed. Please check your information.";
                            }
                        }

                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();

                        // Re-enable button
                        registerButton.setEnabled(true);
                        registerButton.setAlpha(1f);
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    // Network error (no internet, can't reach server, etc.)
                    Toast.makeText(RegisterActivity.this,
                            "Network error: Unable to connect to server", Toast.LENGTH_LONG).show();

                    // Re-enable button
                    registerButton.setEnabled(true);
                    registerButton.setAlpha(1f);
                }
            });
        });
    }

    // Outside OnCreate (Form Validation)
    private void validateForm() {
        String number = registerNumber.getText().toString().trim();
        String email = registerEmail.getText().toString().trim();
        String firstname = registerFirstname.getText().toString().trim();
        String lastname = registerLastname.getText().toString().trim();
        String username = registerUsername.getText().toString().trim();
        String pin = registerPin.getText().toString().trim();
        String confirm = registerConfirm.getText().toString().trim();

        boolean allFilled = !number.isEmpty() && !email.isEmpty() &&
                !firstname.isEmpty() && !lastname.isEmpty() &&
                !username.isEmpty() && !pin.isEmpty() && !confirm.isEmpty();

        boolean numberValid = number.length() >= 11;
        boolean pinValid = pin.length() == 6;
        boolean pinsMatch = pin.equals(confirm);

        boolean formValid = allFilled && numberValid && pinValid && pinsMatch;

        registerButton.setEnabled(formValid);
        registerButton.setAlpha(formValid ? 1f : 0.5f);
    }
}
