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

/**
 * Activity for handling card payment details.
 * Dynamically adapts UI and validation based on selected card type (Visa, MasterCard, etc.)
 */
public class CardDetailsActivity extends AppCompatActivity {

    private String cardType;
    private ImageView backButton;
    private ImageView cardLogo;
    private TextView cardName;
    private EditText etCardNumber;
    private EditText etExpiryMonth;
    private EditText etExpiryYear;
    private EditText etCvv;
    private TextView helperCardDigits;
    private TextView helperExpiry;
    private TextView helperCvvDigits;
    private TextView errorCardNumber;
    private TextView errorExpiry;
    private TextView errorCvv;
    private AppCompatButton btnCashIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        // Get card type passed from previous activity
        cardType = getIntent().getStringExtra("CARD_TYPE");

        initializeViews();
        setupCardType();
        setupInputValidation();
        setupClickListeners();
    }

    /**
     * Initialize all view references from the layout
     */
    private void initializeViews() {
        backButton = findViewById(R.id.ico_back);
        cardLogo = findViewById(R.id.card_logo);
        cardName = findViewById(R.id.card_name);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiryMonth = findViewById(R.id.et_expiry_month);
        etExpiryYear = findViewById(R.id.et_expiry_year);
        etCvv = findViewById(R.id.et_cvv);
        helperCardDigits = findViewById(R.id.helper_card_digits);
        helperExpiry = findViewById(R.id.helper_expiry);
        helperCvvDigits = findViewById(R.id.helper_cvv_digits);
        errorCardNumber = findViewById(R.id.error_card_number);
        errorExpiry = findViewById(R.id.error_expiry);
        errorCvv = findViewById(R.id.error_cvv);
        btnCashIn = findViewById(R.id.btn_cash_in);
    }

    /**
     * Configure UI elements based on the selected card type
     * Sets appropriate logo, name, and input field lengths for each card
     */
    private void setupCardType() {
        if ("Visa".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.visa_logo);
            cardName.setText(R.string.card_visa);
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)}); // 16 digits + 3 hyphens
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        } else if ("MasterCard".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.mastercard_logo);
            cardName.setText(R.string.card_mastercard);
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        } else if ("AmericanExpress".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.amex_logo);
            cardName.setText(R.string.card_amex);
            helperCardDigits.setText(R.string.helper_card_digits_amex);
            helperCvvDigits.setText(R.string.helper_cvv_digits_amex);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)}); // 15 digits + 3 hyphens
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)}); // AmEx uses 4-digit CVV

        } else if ("JCB".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.jcb_logo);
            cardName.setText(R.string.card_jcb);
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        } else if ("Discover".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.discover_logo);
            cardName.setText(R.string.card_discover);
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        } else {
            // Default behavior for unknown card types
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        }
    }

    /**
     * Setup real-time input validation and formatting for all input fields
     * Includes auto-formatting for card numbers with hyphens
     */
    private void setupInputValidation() {
        // Card number input with auto-formatting
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearCardNumberError();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                int selectionStart = etCardNumber.getSelectionStart();
                String digitsOnly = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();

                // Format based on card type (AmEx uses 4-4-4-3, others use 4-4-4-4)
                if ("AmericanExpress".equals(cardType)) {
                    int[] groups = {4, 4, 4, 3};
                    int index = 0;
                    for (int g = 0; g < groups.length && index < digitsOnly.length(); g++) {
                        int end = Math.min(index + groups[g], digitsOnly.length());
                        formatted.append(digitsOnly, index, end);
                        index = end;
                        if (index < digitsOnly.length()) formatted.append("-");
                    }
                } else {
                    for (int i = 0; i < digitsOnly.length(); i++) {
                        if (i > 0 && i % 4 == 0) formatted.append("-");
                        formatted.append(digitsOnly.charAt(i));
                    }
                }

                String formattedStr = formatted.toString();
                s.replace(0, s.length(), formattedStr);

                // Restore cursor position after formatting
                int newPos = selectionStart;
                if (selectionStart > 0 && !formattedStr.isEmpty()) {
                    int digitsBefore = 0;
                    for (int i = 0; i < Math.min(selectionStart, s.length()); i++) {
                        if (Character.isDigit(s.charAt(i))) digitsBefore++;
                    }
                    int pos = 0;
                    int digitsSeen = 0;
                    while (pos < formattedStr.length() && digitsSeen < digitsBefore) {
                        if (Character.isDigit(formattedStr.charAt(pos))) digitsSeen++;
                        pos++;
                    }
                    newPos = pos;
                }

                if (newPos < 0) newPos = 0;
                if (newPos > formattedStr.length()) newPos = formattedStr.length();
                etCardNumber.setSelection(newPos);

                isFormatting = false;
            }
        });

        // Expiry month validation - auto-focus to year after valid input
        etExpiryMonth.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearExpiryError(); }
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 2) {
                    try {
                        int month = Integer.parseInt(s.toString());
                        if (month > 12 || month == 0) {
                            showExpiryError(getString(R.string.error_invalid_month));
                        } else {
                            etExpiryYear.requestFocus();
                        }
                    } catch (NumberFormatException e) {
                        showExpiryError(getString(R.string.error_invalid_month));
                    }
                }
            }
        });

        // Expiry year validation
        etExpiryYear.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearExpiryError(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // CVV validation
        etCvv.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearCvvError(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Set CVV as password type (dots instead of numbers)
        etCvv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }

    /**
     * Setup click listeners for interactive elements
     */
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnCashIn.setOnClickListener(v -> {
            if (validateInputs()) {
                processCashIn();
            }
        });
    }

    /**
     * Validate all input fields before processing payment
     * Returns true if all validations pass, false otherwise
     */
    private boolean validateInputs() {
        boolean isValid = true;

        String cardNumber = etCardNumber.getText().toString().replaceAll("\\D", "");
        String month = etExpiryMonth.getText().toString();
        String year = etExpiryYear.getText().toString();
        String cvv = etCvv.getText().toString();

        // Validate card number length
        if (cardNumber.isEmpty()) {
            showCardNumberError(getString(R.string.error_card_number_required));
            isValid = false;
        } else {
            int expectedLength = "AmericanExpress".equals(cardType) ? 15 : 16;
            if (cardNumber.length() != expectedLength) {
                showCardNumberError(getString(R.string.error_invalid_card_number));
                isValid = false;
            }
        }

        // Validate expiry date
        if (month.isEmpty() || year.isEmpty()) {
            showExpiryError(getString(R.string.error_expiry_required));
            isValid = false;
        } else if (month.length() == 2) {
            try {
                int monthInt = Integer.parseInt(month);
                if (monthInt > 12 || monthInt == 0) {
                    showExpiryError(getString(R.string.error_invalid_expiry));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                showExpiryError(getString(R.string.error_invalid_expiry));
                isValid = false;
            }
        }

        // Validate CVV length
        if (cvv.isEmpty()) {
            showCvvError(getString(R.string.error_cvv_required));
            isValid = false;
        } else {
            int expectedCvvLength = "AmericanExpress".equals(cardType) ? 4 : 3;
            if (cvv.length() != expectedCvvLength) {
                showCvvError(getString(R.string.error_invalid_cvv));
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Display error message for card number field and hide helper text
     */
    private void showCardNumberError(String error) {
        etCardNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        helperCardDigits.setVisibility(TextView.GONE);
        errorCardNumber.setText(error);
        errorCardNumber.setVisibility(TextView.VISIBLE);
    }

    /**
     * Clear error state from card number field and show helper text
     */
    private void clearCardNumberError() {
        etCardNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        errorCardNumber.setVisibility(TextView.GONE);
        helperCardDigits.setVisibility(TextView.VISIBLE);
    }

    /**
     * Display error message for expiry date fields and hide helper text
     */
    private void showExpiryError(String error) {
        etExpiryMonth.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        etExpiryYear.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        helperExpiry.setVisibility(TextView.GONE);
        errorExpiry.setText(error);
        errorExpiry.setVisibility(TextView.VISIBLE);
    }

    /**
     * Clear error state from expiry date fields and show helper text
     */
    private void clearExpiryError() {
        etExpiryMonth.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        etExpiryYear.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        errorExpiry.setVisibility(TextView.GONE);
        helperExpiry.setVisibility(TextView.VISIBLE);
    }

    /**
     * Display error message for CVV field and hide helper text
     */
    private void showCvvError(String error) {
        etCvv.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        helperCvvDigits.setVisibility(TextView.GONE);
        errorCvv.setText(error);
        errorCvv.setVisibility(TextView.VISIBLE);
    }

    /**
     * Clear error state from CVV field and show helper text
     */
    private void clearCvvError() {
        etCvv.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        errorCvv.setVisibility(TextView.GONE);
        helperCvvDigits.setVisibility(TextView.VISIBLE);
    }

    private void processCashIn() {
        // TODO: Implement cash in logic
    }
}