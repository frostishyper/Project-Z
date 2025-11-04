package com.example.socialapp;

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
    private TextView helperCvvDigits;
    private TextView errorCardNumber;
    private TextView errorExpiry;
    private TextView errorCvv;
    private AppCompatButton btnCashIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        cardType = getIntent().getStringExtra("CARD_TYPE");

        initializeViews();
        setupCardType();
        setupInputValidation();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        cardLogo = findViewById(R.id.card_logo);
        cardName = findViewById(R.id.card_name);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiryMonth = findViewById(R.id.et_expiry_month);
        etExpiryYear = findViewById(R.id.et_expiry_year);
        etCvv = findViewById(R.id.et_cvv);
        helperCardDigits = findViewById(R.id.helper_card_digits);
        helperCvvDigits = findViewById(R.id.helper_cvv_digits);
        errorCardNumber = findViewById(R.id.error_card_number);
        errorExpiry = findViewById(R.id.error_expiry);
        errorCvv = findViewById(R.id.error_cvv);
        btnCashIn = findViewById(R.id.btn_cash_in);
    }

    private void setupCardType() {
        if ("Visa".equals(cardType)) {
            cardLogo.setImageResource(R.drawable.visa_logo);
            cardName.setText(R.string.card_visa);
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            // 16 digits -> 16 + 3 hyphens = 19 chars
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
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
            // AmEx helper strings you may have different ones
            helperCardDigits.setText(R.string.helper_card_digits_amex);
            helperCvvDigits.setText(R.string.helper_cvv_digits_amex);
            // 15 digits -> 15 + 3 hyphens = 18 chars (4-4-4-3 grouping)
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

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
            // default behavior
            helperCardDigits.setText(R.string.helper_card_digits);
            helperCvvDigits.setText(R.string.helper_cvv_digits);
            etCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            etCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        }
    }

    private void setupInputValidation() {
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

                // Keep track of cursor position
                int selectionStart = etCardNumber.getSelectionStart();

                String digitsOnly = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();

                if ("AmericanExpress".equals(cardType)) {
                    // Desired grouping: 4-4-4-3 (total 15 digits)
                    int[] groups = {4, 4, 4, 3};
                    int index = 0;
                    for (int g = 0; g < groups.length && index < digitsOnly.length(); g++) {
                        int end = Math.min(index + groups[g], digitsOnly.length());
                        formatted.append(digitsOnly, index, end);
                        index = end;
                        if (index < digitsOnly.length()) formatted.append("-");
                    }
                } else {
                    // Standard grouping: 4-4-4-4 (for up to 16 digits)
                    for (int i = 0; i < digitsOnly.length(); i++) {
                        if (i > 0 && i % 4 == 0) formatted.append("-");
                        formatted.append(digitsOnly.charAt(i));
                    }
                }

                // update text and restore cursor as close as possible
                String formattedStr = formatted.toString();
                s.replace(0, s.length(), formattedStr);

                // Simple cursor correction: place cursor at end or original relative position
                int newPos = selectionStart;

                // If cursor was after a digit that became a hyphen, move it forward
                if (selectionStart > 0 && !formattedStr.isEmpty()) {
                    // Count digits before old selection
                    int digitsBefore = 0;
                    for (int i = 0; i < Math.min(selectionStart, s.length()); i++) {
                        if (Character.isDigit(s.charAt(i))) digitsBefore++;
                    }
                    // Now compute new cursor position by scanning formattedStr
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

        etExpiryYear.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearExpiryError(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        etCvv.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearCvvError(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        etCvv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnCashIn.setOnClickListener(v -> {
            if (validateInputs()) {
                processCashIn();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String cardNumber = etCardNumber.getText().toString().replaceAll("\\D", "");
        String month = etExpiryMonth.getText().toString();
        String year = etExpiryYear.getText().toString();
        String cvv = etCvv.getText().toString();

        // Validate card number
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

        // Validate expiry
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

        // Validate CVV
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

    private void showCardNumberError(String error) {
        etCardNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        errorCardNumber.setText(error);
        errorCardNumber.setVisibility(TextView.VISIBLE);
    }

    private void clearCardNumberError() {
        etCardNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        errorCardNumber.setVisibility(TextView.GONE);
    }

    private void showExpiryError(String error) {
        etExpiryMonth.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        etExpiryYear.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        errorExpiry.setText(error);
        errorExpiry.setVisibility(TextView.VISIBLE);
    }

    private void clearExpiryError() {
        etExpiryMonth.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        etExpiryYear.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        errorExpiry.setVisibility(TextView.GONE);
    }

    private void showCvvError(String error) {
        etCvv.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_error_bg));
        errorCvv.setText(error);
        errorCvv.setVisibility(TextView.VISIBLE);
    }

    private void clearCvvError() {
        etCvv.setBackground(ContextCompat.getDrawable(this, R.drawable.input_field_bg));
        errorCvv.setVisibility(TextView.GONE);
    }

    private void processCashIn() {
        // TODO: Implement cash in logic
    }
}
