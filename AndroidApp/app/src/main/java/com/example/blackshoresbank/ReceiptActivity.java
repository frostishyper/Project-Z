package com.example.blackshoresbank;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> finish());

        // Get transaction data
        String direction = getIntent().getStringExtra("direction");
        String senderType = getIntent().getStringExtra("senderType");
        String senderNumber = getIntent().getStringExtra("senderNumber");
        String recipientType = getIntent().getStringExtra("recipientType");
        String recipientNumber = getIntent().getStringExtra("recipientNumber");
        String merchantName = getIntent().getStringExtra("merchantName");
        String amount = getIntent().getStringExtra("amount");
        String fee = getIntent().getStringExtra("fee");
        String note = getIntent().getStringExtra("note");
        String date = getIntent().getStringExtra("date");
        String referenceId = getIntent().getStringExtra("referenceId");

        // Get views
        TextView titleView = findViewById(R.id.receipt_title);
        TextView amountView = findViewById(R.id.receipt_amount_value);
        TextView currencyView = findViewById(R.id.receipt_currency_symbol);
        TextView dateTimeView = findViewById(R.id.receipt_datetime);
        TextView noteView = findViewById(R.id.receipt_place);
        TextView accountView = findViewById(R.id.receipt_account_value);
        TextView feeView = findViewById(R.id.receipt_fee_value);
        TextView referenceView = findViewById(R.id.receipt_reference_value);

        // Determine transaction type and set title
        String title;
        String accountNumber;
        int amountColor;

        if ("OUTGOING".equals(direction) && "MERCHANT".equals(recipientType)) {
            // Purchase
            title = getString(R.string.receipt_purchaselabel);
            accountNumber = merchantName != null ? merchantName : "Merchant";
            amountColor = 0xFFC62828; // Red
        } else if ("OUTGOING".equals(direction)) {
            // Sent to user
            title = getString(R.string.receipt_sentlabel);
            accountNumber = recipientNumber;
            amountColor = 0xFFC62828; // Red
        } else {
            // Received (from user or merchant)
            title = getString(R.string.receipt_recievedlabel);
            if ("MERCHANT".equals(senderType)) {
                accountNumber = merchantName != null ? merchantName : "Merchant";
            } else {
                accountNumber = senderNumber;
            }
            amountColor = 0xFF2E7D32; // Green
        }

        // Set data
        titleView.setText(title);
        amountView.setText(amount);
        amountView.setTextColor(amountColor);
        currencyView.setTextColor(amountColor);
        dateTimeView.setText(formatDateTime(date));
        noteView.setText(note != null && !note.isEmpty() ? note : "No note");
        accountView.setText(accountNumber);
        feeView.setText("$" + fee);
        referenceView.setText(referenceId);
    }

    private String formatDateTime(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
}