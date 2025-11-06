package com.example.blackshoresbank;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class CardCashInActivity extends AppCompatActivity {

    private String cardType;
    private ImageView backButton, cardLogo;
    private TextView CardTitle;

    private EditText cardNumberInput, expiryMonthInput, expiryYearInput, cvvInput;
    private TextView cardNumberHelper, expiryHelper, cvvHelper;
    private TextView cardNumberError, expiryError, cvvError;

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
    }

    private void initViews() {
        backButton = findViewById(R.id.BackBtn);
        cardLogo = findViewById(R.id.CardLogo);
        CardTitle = findViewById(R.id.CardTitle);

        cardNumberInput = findViewById(R.id.CardNumberInput);
        expiryMonthInput = findViewById(R.id.ExpiryMonthInput);
        expiryYearInput = findViewById(R.id.ExpiryYearInput);
        cvvInput = findViewById(R.id.CvvInput);

        cardNumberHelper = findViewById(R.id.CardNumberHelper);
        expiryHelper = findViewById(R.id.ExpiryHelper);
        cvvHelper = findViewById(R.id.CvvHelper);

        cardNumberError = findViewById(R.id.CardNumberError);
        expiryError = findViewById(R.id.ExpiryError);
        cvvError = findViewById(R.id.CvvError);

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
        // Auto-format card number
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearCardNumberError();
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

        // Expiry month
        expiryMonthInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearExpiryError(); }
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 2) {
                    try {
                        int m = Integer.parseInt(s.toString());
                        if (m < 1 || m > 12) {
                            showExpiryError(getString(R.string.error_invalid_month));
                        } else {
                            expiryYearInput.requestFocus();
                        }
                    } catch (NumberFormatException e) {
                        showExpiryError(getString(R.string.error_invalid_month));
                    }
                }
            }
        });

        // Expiry year
        expiryYearInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearExpiryError(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // CVV
        cvvInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearCvvError(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        cvvInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
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

        return valid;
    }

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

    private void processCashIn() {
        // TODO: connect to backend or simulate transaction
    }
}
