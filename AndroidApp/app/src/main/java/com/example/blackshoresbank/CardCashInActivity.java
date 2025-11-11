package com.example.blackshoresbank;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

// Networking Imports
import com.example.blackshoresbank.models.CardCashInRequest;
import com.example.blackshoresbank.models.CardCashInResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;
import com.example.blackshoresbank.utils.TokenManager;
import retrofit2.Call;

public class CardCashInActivity extends BaseActivity {

    private String cardType;
    private ImageView backButton, cardLogo;
    private TextView CardTitle;

    private EditText cardNumberInput, expiryMonthInput, expiryYearInput, cvvInput, cashInAmountInput;
    private TextView cardNumberHelper, expiryHelper, cvvHelper, cashInAmountHelper;
    private TextView cardNumberError, expiryError, cvvError, cashInAmountError;

    private AppCompatButton cashInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_in_card);

        cardType = getIntent().getStringExtra("CARD_TYPE");
        initViews();
        setupCardType();
        setupInputValidation();
        setupListeners();
        updateButtonState();
    }

    private void initViews() {
        backButton = findViewById(R.id.BackBtn);
        cardLogo = findViewById(R.id.CardLogo);
        CardTitle = findViewById(R.id.CardTitle);

        cardNumberInput = findViewById(R.id.CardNumberInput);
        expiryMonthInput = findViewById(R.id.ExpiryMonthInput);
        expiryYearInput = findViewById(R.id.ExpiryYearInput);
        cvvInput = findViewById(R.id.CvvInput);
        cashInAmountInput = findViewById(R.id.CashInAmountInput);

        cardNumberHelper = findViewById(R.id.CardNumberHelper);
        expiryHelper = findViewById(R.id.ExpiryHelper);
        cvvHelper = findViewById(R.id.CvvHelper);
        cashInAmountHelper = findViewById(R.id.CashInAmountHelper);

        cardNumberError = findViewById(R.id.CardNumberError);
        expiryError = findViewById(R.id.ExpiryError);
        cvvError = findViewById(R.id.CvvError);
        cashInAmountError = findViewById(R.id.CashInAmmountError);

        cashInButton = findViewById(R.id.CashInBtn);
    }

    private void setupCardType() {
        if ("Visa".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.visa_logo);
            CardTitle.setText("Visa");
            cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            cvvInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else if ("MasterCard".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.mastercard_logo);
            CardTitle.setText("MasterCard");
            cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            cvvInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else if ("AmericanExpress".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.amex_logo);
            CardTitle.setText("American Express");
            cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
            cvvInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        } else if ("JCB".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.jcb_logo);
            CardTitle.setText("JCB");
            cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            cvvInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else if ("Discover".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.discover_logo);
            CardTitle.setText("Discover");
            cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            cvvInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        }
    }

    private void setupInputValidation() {
        // --- Card Number Formatting ---
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearCardNumberError();
                updateButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String digits = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();

                if ("AmericanExpress".equals(cardType)) {
                    int[] groups = {4, 4, 4, 3};
                    int idx = 0;
                    for (int g : groups) {
                        if (idx >= digits.length()) break;
                        int end = Math.min(idx + g, digits.length());
                        formatted.append(digits, idx, end);
                        idx = end;
                        if (idx < digits.length()) formatted.append("-");
                    }
                } else {
                    for (int i = 0; i < digits.length(); i++) {
                        if (i > 0 && i % 4 == 0) formatted.append("-");
                        formatted.append(digits.charAt(i));
                    }
                }

                cardNumberInput.removeTextChangedListener(this);
                cardNumberInput.setText(formatted);
                cardNumberInput.setSelection(formatted.length());
                cardNumberInput.addTextChangedListener(this);
                isFormatting = false;
            }
        });

        // --- Expiry Fields ---
        expiryMonthInput.addTextChangedListener(new SimpleTextWatcher(() -> {
            clearExpiryError();
            updateButtonState();
        }));

        expiryYearInput.addTextChangedListener(new SimpleTextWatcher(() -> {
            clearExpiryError();
            updateButtonState();
        }));

        // --- CVV ---
        cvvInput.addTextChangedListener(new SimpleTextWatcher(() -> {
            clearCvvError();
            updateButtonState();
        }));

        cvvInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // --- Cash In Amount ---
        cashInAmountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        cashInAmountInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearCashInAmountError();
                updateButtonState();
            }
            @Override public void afterTextChanged(Editable s) {
                String input = s.toString();

                // Restrict to 2 decimal places
                if (input.contains(".")) {
                    int index = input.indexOf(".");
                    if (input.length() - index - 1 > 2) {
                        s.delete(index + 3, input.length());
                    }
                }
            }
        });
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        cashInButton.setOnClickListener(v -> {
            if (validateInputs()) {
                processCashIn();
            }
        });
    }

    private boolean validateInputs() {
        boolean valid = true;

        String cardNum = cardNumberInput.getText().toString().replaceAll("\\D", "");
        String month = expiryMonthInput.getText().toString();
        String year = expiryYearInput.getText().toString();
        String cvv = cvvInput.getText().toString();
        String amountStr = cashInAmountInput.getText().toString().trim();

        // --- Card Fields Validation ---
        if (cardNum.isEmpty()) {
            showCardNumberError(getString(R.string.error_card_number_required));
            valid = false;
        } else {
            int expected = "AmericanExpress".equals(cardType) ? 15 : 16;
            if (cardNum.length() != expected) {
                showCardNumberError(getString(R.string.error_invalid_card_number));
                valid = false;
            }
        }

        if (month.isEmpty() || year.isEmpty()) {
            showExpiryError(getString(R.string.error_expiry_required));
            valid = false;
        }

        if (cvv.isEmpty()) {
            showCvvError(getString(R.string.error_cvv_required));
            valid = false;
        } else {
            int expectedCVV = "AmericanExpress".equals(cardType) ? 4 : 3;
            if (cvv.length() != expectedCVV) {
                showCvvError(getString(R.string.error_invalid_cvv));
                valid = false;
            }
        }

        // --- Amount Validation ---
        if (amountStr.isEmpty()) {
            showCashInAmountError(getString(R.string.error_invalid_amount));
            valid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showCashInAmountError(getString(R.string.error_invalid_amount));
                    valid = false;
                } else if (!amountStr.contains(".")) {
                    // Append .00 if no decimals
                    cashInAmountInput.setText(String.format("%.2f", amount));
                }
            } catch (NumberFormatException e) {
                showCashInAmountError(getString(R.string.error_invalid_amount));
                valid = false;
            }
        }

        updateButtonState();
        return valid;
    }

    private void updateButtonState() {
        boolean enabled = !cardNumberInput.getText().toString().trim().isEmpty()
                && !expiryMonthInput.getText().toString().trim().isEmpty()
                && !expiryYearInput.getText().toString().trim().isEmpty()
                && !cvvInput.getText().toString().trim().isEmpty()
                && !cashInAmountInput.getText().toString().trim().isEmpty();
        cashInButton.setEnabled(enabled);
        cashInButton.setAlpha(enabled ? 1f : 0.5f);
    }

    // --- Error Handling ---
    private void showCardNumberError(String msg) {
        cardNumberInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        cardNumberHelper.setVisibility(TextView.GONE);
        cardNumberError.setText(msg);
        cardNumberError.setVisibility(TextView.VISIBLE);
    }
    private void clearCardNumberError() {
        cardNumberInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        cardNumberError.setVisibility(TextView.GONE);
        cardNumberHelper.setVisibility(TextView.VISIBLE);
    }

    private void showExpiryError(String msg) {
        expiryMonthInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        expiryYearInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        expiryHelper.setVisibility(TextView.GONE);
        expiryError.setText(msg);
        expiryError.setVisibility(TextView.VISIBLE);
    }
    private void clearExpiryError() {
        expiryMonthInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        expiryYearInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        expiryError.setVisibility(TextView.GONE);
        expiryHelper.setVisibility(TextView.VISIBLE);
    }

    private void showCvvError(String msg) {
        cvvInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        cvvHelper.setVisibility(TextView.GONE);
        cvvError.setText(msg);
        cvvError.setVisibility(TextView.VISIBLE);
    }
    private void clearCvvError() {
        cvvInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        cvvError.setVisibility(TextView.GONE);
        cvvHelper.setVisibility(TextView.VISIBLE);
    }

    private void showCashInAmountError(String msg) {
        cashInAmountInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        cashInAmountHelper.setVisibility(TextView.GONE);
        cashInAmountError.setText(msg);
        cashInAmountError.setVisibility(TextView.VISIBLE);
    }
    private void clearCashInAmountError() {
        cashInAmountInput.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        cashInAmountError.setVisibility(TextView.GONE);
        cashInAmountHelper.setVisibility(TextView.VISIBLE);
    }

    // Cash-In Button Click
    private void processCashIn() {
        // --- Disable button to prevent spamming ---
        cashInButton.setEnabled(false);
        cashInButton.setAlpha(0.5f);

        // --- Retrieve input values ---
        String accountNumber = new TokenManager(this).getAccountNumber(); // get from local session
        String cardNum = cardNumberInput.getText().toString().replaceAll("\\D", "");
        String expiryMonth = expiryMonthInput.getText().toString().trim();
        String expiryYear = expiryYearInput.getText().toString().trim();
        String cvv = cvvInput.getText().toString().trim();
        String amountStr = cashInAmountInput.getText().toString().trim();

        double cashInAmount;
        try {
            cashInAmount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showCashInAmountError(getString(R.string.error_invalid_amount));
            cashInButton.setEnabled(true);
            cashInButton.setAlpha(1f);
            return;
        }

        // --- Build request ---
        CardCashInRequest request = new CardCashInRequest(
                accountNumber,
                cardType,
                cardNum,
                expiryMonth,
                expiryYear,
                cvv,
                cashInAmount
        );

        // --- Prepare API Service ---
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<CardCashInResponse> call = apiService.CardCashIn(request);

        // --- Show progress indicator ---
        runOnUiThread(() -> Toast.makeText(this, "Processing Cash-In...", Toast.LENGTH_SHORT).show());

        // --- Execute request ---
        call.enqueue(new retrofit2.Callback<CardCashInResponse>() {
            @Override
            public void onResponse(Call<CardCashInResponse> call, retrofit2.Response<CardCashInResponse> response) {
                runOnUiThread(() -> {
                    cashInButton.setEnabled(true);
                    cashInButton.setAlpha(1f);
                });

                if (response.isSuccessful() && response.body() != null) {
                    CardCashInResponse res = response.body();

                    if (res.isSuccess()) {
                        // --- Success ---
                        double credited = res.getCredited();
                        double fee = res.getFee();
                        double chargedTotal = res.getChargedTotal();

                        String msg = String.format(
                                "Success! ₱%.2f credited to your wallet.\n(₱%.2f fee applied, ₱%.2f charged to card)",
                                credited, fee, chargedTotal
                        );

                        runOnUiThread(() -> {
                            Toast.makeText(CardCashInActivity.this, msg, Toast.LENGTH_LONG).show();
                            finish(); // close the activity after success
                        });
                    } else {
                        // --- Backend responded with failure ---
                        runOnUiThread(() ->
                                Toast.makeText(CardCashInActivity.this, res.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                } else {
                    // --- Show specific backend error message if available ---
                    String errorMsg = "Cash-In failed. Try again later.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = new org.json.JSONObject(response.errorBody().string())
                                    .optString("error", errorMsg);
                        }
                    } catch (Exception ignored) {}

                    String finalErrorMsg = errorMsg;
                    runOnUiThread(() ->
                            Toast.makeText(CardCashInActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<CardCashInResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    cashInButton.setEnabled(true);
                    cashInButton.setAlpha(1f);
                    Toast.makeText(CardCashInActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    // --- Utility inner class for shorter watchers ---
    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable onChange;
        SimpleTextWatcher(Runnable onChange) { this.onChange = onChange; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { onChange.run(); }
        @Override public void afterTextChanged(Editable s) {}
    }
}
