package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackshoresbank.models.SendMoneyPrepareRequest;
import com.example.blackshoresbank.models.SendMoneyPrepareResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;
import com.example.blackshoresbank.utils.WalletManager;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendActivity extends BaseActivity {

    private EditText recipientInput, amountInput, noteInput;
    private TextView amountError, amountApprove, amountHelper;
    private TextView noteApprove, noteHelper;
    private Button proceedBtn;
    private String userNumber;
    private double userBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);

        userNumber = tokenManager.getAccountNumber();

        // Get views
        recipientInput = findViewById(R.id.RecipientInput);
        amountInput = findViewById(R.id.SendAmountInput);
        noteInput = findViewById(R.id.NoteInput);
        amountError = findViewById(R.id.SendAmountError);
        amountApprove = findViewById(R.id.SendAmountApprove);
        amountHelper = findViewById(R.id.SendAmountHelper);
        noteApprove = findViewById(R.id.NoteApprove);
        noteHelper = findViewById(R.id.NoteHelper);
        proceedBtn = findViewById(R.id.Proceed);

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> finish());

        // Fix note input - should be text, not number
        noteInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        noteInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

        // Load user balance
        loadUserBalance();

        // Set up validation
        setupValidation();

        // Disable button initially
        proceedBtn.setEnabled(false);
        proceedBtn.setAlpha(0.5f);

        // Proceed button
        proceedBtn.setOnClickListener(v -> handleProceed());
    }

    private void loadUserBalance() {
        WalletManager.fetchBalance(this, userNumber, new WalletManager.WalletBalanceCallback() {
            @Override
            public void onSuccess(String balance) {
                userBalance = Double.parseDouble(balance);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SendActivity.this, "Could not load balance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupValidation() {
        TextWatcher validationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
            }
        };

        recipientInput.addTextChangedListener(validationWatcher);
        amountInput.addTextChangedListener(validationWatcher);
        noteInput.addTextChangedListener(validationWatcher);

        // Amount loses focus - format decimal
        amountInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String amountStr = amountInput.getText().toString().trim();
                if (!amountStr.isEmpty() && !amountStr.contains(".")) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        amountInput.setText(String.format("%.2f", amount));
                    } catch (NumberFormatException e) {
                        // Invalid number, leave as is
                    }
                }
            }
        });

        // Amount validation
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateAmount();
            }
        });

        // Note validation with background change
        noteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String note = s.toString().trim();
                if (!note.isEmpty()) {
                    noteApprove.setVisibility(android.view.View.VISIBLE);
                    noteHelper.setVisibility(android.view.View.GONE);
                    noteInput.setBackgroundResource(R.drawable.input_field_approve_bg);
                } else {
                    noteApprove.setVisibility(android.view.View.GONE);
                    noteHelper.setVisibility(android.view.View.VISIBLE);
                    noteInput.setBackgroundResource(R.drawable.input_field_bg);
                }
            }
        });
    }

    private void validateAmount() {
        String amountStr = amountInput.getText().toString().trim();

        if (amountStr.isEmpty()) {
            amountError.setVisibility(android.view.View.GONE);
            amountApprove.setVisibility(android.view.View.GONE);
            amountHelper.setVisibility(android.view.View.VISIBLE);
            amountInput.setBackgroundResource(R.drawable.input_field_bg);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            double totalRequired = amount + 1.00;

            if (totalRequired > userBalance) {
                amountError.setVisibility(android.view.View.VISIBLE);
                amountApprove.setVisibility(android.view.View.GONE);
                amountHelper.setVisibility(android.view.View.GONE);
                amountInput.setBackgroundResource(R.drawable.input_field_error_bg);
            } else {
                amountError.setVisibility(android.view.View.GONE);
                amountApprove.setVisibility(android.view.View.VISIBLE);
                amountHelper.setVisibility(android.view.View.GONE);
                amountInput.setBackgroundResource(R.drawable.input_field_approve_bg);
            }
        } catch (NumberFormatException e) {
            amountError.setVisibility(android.view.View.VISIBLE);
            amountApprove.setVisibility(android.view.View.GONE);
            amountHelper.setVisibility(android.view.View.GONE);
            amountInput.setBackgroundResource(R.drawable.input_field_error_bg);
        }
    }

    private void validateForm() {
        String recipient = recipientInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();

        boolean recipientValid = !recipient.isEmpty() && recipient.length() >= 11;
        boolean amountValid = !amount.isEmpty() && amountApprove.getVisibility() == android.view.View.VISIBLE;

        boolean formValid = recipientValid && amountValid;

        proceedBtn.setEnabled(formValid);
        proceedBtn.setAlpha(formValid ? 1f : 0.5f);
    }

    private void handleProceed() {
        String recipient = recipientInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();

        proceedBtn.setEnabled(false);
        proceedBtn.setAlpha(0.5f);

        SendMoneyPrepareRequest request = new SendMoneyPrepareRequest(
                userNumber, recipient, amount, note
        );

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<SendMoneyPrepareResponse> call = apiService.PrepareSendMoney(request);

        call.enqueue(new Callback<SendMoneyPrepareResponse>() {
            @Override
            public void onResponse(Call<SendMoneyPrepareResponse> call, Response<SendMoneyPrepareResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SendMoneyPrepareResponse result = response.body();

                    if (result.isSuccess()) {
                        // Navigate to confirmation
                        Intent intent = new Intent(SendActivity.this, SendConfirmActivity.class);
                        intent.putExtra("userNumber", userNumber);
                        intent.putExtra("recipientNumber", result.getRecipientNumber());
                        intent.putExtra("recipientUsername", result.getRecipientUsername());
                        intent.putExtra("transferAmount", result.getTransferAmount());
                        intent.putExtra("transferFee", result.getTransferFee());
                        intent.putExtra("totalAmount", result.getTotalAmount());
                        intent.putExtra("projectedBalance", result.getProjectedBalance());
                        intent.putExtra("note", result.getNote());
                        intent.putExtra("currentBalance", String.valueOf(userBalance));
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SendActivity.this, result.getError(), Toast.LENGTH_LONG).show();
                        proceedBtn.setEnabled(true);
                        proceedBtn.setAlpha(1f);
                    }
                } else {
                    Toast.makeText(SendActivity.this, "Failed to prepare transfer", Toast.LENGTH_LONG).show();
                    proceedBtn.setEnabled(true);
                    proceedBtn.setAlpha(1f);
                }
            }

            @Override
            public void onFailure(Call<SendMoneyPrepareResponse> call, Throwable t) {
                Toast.makeText(SendActivity.this, "Network error", Toast.LENGTH_LONG).show();
                proceedBtn.setEnabled(true);
                proceedBtn.setAlpha(1f);
            }
        });
    }
}