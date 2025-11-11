package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackshoresbank.models.TransactionHistoryResponse;
import com.example.blackshoresbank.utils.TransactionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionsActivity extends BaseActivity {

    private LinearLayout container;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        loggedInUser = tokenManager.getAccountNumber();
        container = findViewById(R.id.CardFormContainer);

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> finish());

        loadTransactions();
    }

    private void loadTransactions() {
        TransactionManager.fetchTransactions(this, loggedInUser,
                new TransactionManager.TransactionCallback() {
                    @Override
                    public void onSuccess(List<TransactionHistoryResponse.Transaction> transactions) {
                        displayTransactions(transactions);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(TransactionsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayTransactions(List<TransactionHistoryResponse.Transaction> transactions) {
        // Remove everything after title (keep first 2 views: back button and title)
        int childCount = container.getChildCount();
        if (childCount > 2) {
            container.removeViews(2, childCount - 2);
        }

        if (transactions.isEmpty()) {
            return;
        }

        // Group by date
        Map<String, List<TransactionHistoryResponse.Transaction>> grouped = groupByDate(transactions);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map.Entry<String, List<TransactionHistoryResponse.Transaction>> entry : grouped.entrySet()) {
            String date = entry.getKey();
            List<TransactionHistoryResponse.Transaction> dayTransactions = entry.getValue();

            // Add divider with date
            View divider = inflater.inflate(R.layout.transactions_divider, container, false);
            TextView dividerText = divider.findViewById(R.id.dividerText);
            dividerText.setText(date);
            container.addView(divider);

            // Add each transaction for this day
            for (TransactionHistoryResponse.Transaction tx : dayTransactions) {
                View txView = createTransactionView(tx, inflater);
                container.addView(txView);
            }
        }
    }

    private Map<String, List<TransactionHistoryResponse.Transaction>> groupByDate(
            List<TransactionHistoryResponse.Transaction> transactions) {

        Map<String, List<TransactionHistoryResponse.Transaction>> grouped = new LinkedHashMap<>();

        for (TransactionHistoryResponse.Transaction tx : transactions) {
            String dateKey = formatDate(tx.getDate());

            if (!grouped.containsKey(dateKey)) {
                grouped.put(dateKey, new ArrayList<>());
            }
            grouped.get(dateKey).add(tx);
        }

        return grouped;
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }

    private String formatTime(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private View createTransactionView(TransactionHistoryResponse.Transaction tx, LayoutInflater inflater) {
        String direction = tx.getDirection();
        String recipientType = tx.getRecipientType();
        String senderType = tx.getSenderType();

        int layoutId;
        String entityText;

        // User paying merchant = purchase
        if ("OUTGOING".equals(direction) && "MERCHANT".equals(recipientType)) {
            layoutId = R.layout.transactions_purchase;
            entityText = tx.getMerchantName() != null ? tx.getMerchantName() : "Merchant";
        }
        // User sending to user = sent
        else if ("OUTGOING".equals(direction)) {
            layoutId = R.layout.transactions_sent;
            entityText = tx.getRecipientNumber();
        }
        // User receiving (from user OR merchant) = received
        else {
            layoutId = R.layout.transactions_recieved;
            if ("MERCHANT".equals(senderType)) {
                entityText = tx.getMerchantName() != null ? tx.getMerchantName() : "Merchant";
            } else {
                entityText = tx.getSenderNumber();
            }
        }

        View view = inflater.inflate(layoutId, container, false);

        // Set data
        // Weird bug saying missing ID's but don't worry they do exist and its a weird android studio bug
        if (layoutId == R.layout.transactions_recieved) {
            ((TextView) view.findViewById(R.id.entry_from_received)).setText(entityText);
            ((TextView) view.findViewById(R.id.entry_time_received)).setText(formatTime(tx.getDate()));
            ((TextView) view.findViewById(R.id.entry_amount_received)).setText(tx.getAmount());
        } else if (layoutId == R.layout.transactions_sent) {
            ((TextView) view.findViewById(R.id.entry_entity_sent)).setText(entityText);
            ((TextView) view.findViewById(R.id.entry_time_sent)).setText(formatTime(tx.getDate()));
            ((TextView) view.findViewById(R.id.entry_amount_sent)).setText(tx.getAmount());
        } else {
            ((TextView) view.findViewById(R.id.entry_entity_purchase)).setText(entityText);
            ((TextView) view.findViewById(R.id.entry_time_purchase)).setText(formatTime(tx.getDate()));
            ((TextView) view.findViewById(R.id.entry_amount_purchase)).setText(tx.getAmount());
        }

        // Make clickable
        view.setClickable(true);
        view.setFocusable(true);
        view.setOnClickListener(v -> openReceipt(tx));

        return view;
    }

    private void openReceipt(TransactionHistoryResponse.Transaction tx) {
        Intent intent = new Intent(this, ReceiptActivity.class);

        // Pass all transaction data
        intent.putExtra("transactionId", tx.getTransactionId());
        intent.putExtra("referenceId", tx.getReferenceId());
        intent.putExtra("direction", tx.getDirection());
        intent.putExtra("senderType", tx.getSenderType());
        intent.putExtra("senderNumber", tx.getSenderNumber());
        intent.putExtra("recipientType", tx.getRecipientType());
        intent.putExtra("recipientNumber", tx.getRecipientNumber());
        intent.putExtra("merchantName", tx.getMerchantName());
        intent.putExtra("amount", tx.getAmount());
        intent.putExtra("fee", tx.getFee());
        intent.putExtra("note", tx.getNote());
        intent.putExtra("date", tx.getDate());

        startActivity(intent);
    }
}