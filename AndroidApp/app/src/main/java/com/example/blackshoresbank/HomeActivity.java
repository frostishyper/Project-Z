package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import android.widget.Toast;

import com.example.blackshoresbank.utils.WalletManager;
import com.example.blackshoresbank.models.TransactionHistoryResponse;
import com.example.blackshoresbank.utils.TransactionManager;


public class HomeActivity extends BaseActivity {

    private TextView walletBalanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Load recent transactions
        loadRecentTransactions();

        // Get views
        TextView namePlaceholder = findViewById(R.id.Home_NamePlaceholder);
        walletBalanceView = findViewById(R.id.WalletBalance);

        // Set username from token
        String username = tokenManager.getUsername();
        if (username != null) {
            namePlaceholder.setText(username);
        }

        // Load wallet balance
        loadWalletBalance();

        // Navigate to Profile Page
        LinearLayout userContainer = findViewById(R.id.UserContainer);
        userContainer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Navigate To Transactions Page
        TextView seeAll = findViewById(R.id.SeeAllTransactions);
        seeAll.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TransactionsActivity.class);
            startActivity(intent);
        });

        // Navigate to Cash In Page
        LinearLayout actionCashIn = findViewById(R.id.Action_CashIn);
        actionCashIn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CashInActivity.class);
            startActivity(intent);
        });

        // Navigate to Transfer Page
        LinearLayout actionTransfer = findViewById(R.id.Action_Transfer);
        actionTransfer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SendActivity.class);
            startActivity(intent);
        });

        // Navigate to Shops
        LinearLayout actionShops = findViewById(R.id.Action_Shop);
        actionShops.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ShopsActivity.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh balance & recent transactions when returning to home
        loadWalletBalance();
        loadRecentTransactions();
    }

    private void loadWalletBalance() {
        String accountNumber = tokenManager.getAccountNumber();

        if (accountNumber == null) {
            Toast.makeText(this, "Account information missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        walletBalanceView.setText("...");

        // Fetch balance using reusable WalletManager
        WalletManager.fetchBalance(this, accountNumber, new WalletManager.WalletBalanceCallback() {
            @Override
            public void onSuccess(String balance) {
                walletBalanceView.setText(balance);
            }

            @Override
            public void onError(String error) {
                walletBalanceView.setText("0.00");
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Recent TransactionsActivity
    private void loadRecentTransactions() {
        String accountNumber = tokenManager.getAccountNumber();

        if (accountNumber == null) {
            return;
        }

        TransactionManager.fetchTransactions(this, accountNumber,
                new TransactionManager.TransactionCallback() {
                    @Override
                    public void onSuccess(List<TransactionHistoryResponse.Transaction> transactions) {
                        displayRecentTransactions(transactions);
                    }

                    @Override
                    public void onError(String error) {
                        // Silently handle error or show message
                        Toast.makeText(HomeActivity.this, "Could not load transactions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayRecentTransactions(List<TransactionHistoryResponse.Transaction> transactions) {
        LinearLayout container = findViewById(R.id.transactionListContainer);
        container.removeAllViews(); // Clear existing views

        // Get only the 5 most recent
        int limit = Math.min(transactions.size(), 5);
        String loggedInUser = tokenManager.getAccountNumber();

        for (int i = 0; i < limit; i++) {
            TransactionHistoryResponse.Transaction tx = transactions.get(i);
            View transactionView = createTransactionView(tx, loggedInUser);
            container.addView(transactionView);
        }

        // Show message if no transactions
        if (transactions.isEmpty()) {
            TextView noTransactions = new TextView(this);
            noTransactions.setText("No transactions yet");
            noTransactions.setTextColor(0xFFAAAAAA);
            noTransactions.setPadding(20, 40, 20, 40);
            noTransactions.setGravity(android.view.Gravity.CENTER);
            container.addView(noTransactions);
        }
    }

    private View createTransactionView(TransactionHistoryResponse.Transaction tx, String loggedInUser) {
        String direction = tx.getDirection();
        String senderType = tx.getSenderType();
        String recipientType = tx.getRecipientType();

        // Determine which layout to use
        int layoutId;
        int amountColor;
        String typeText;
        String partnerName;

        if ("OUTGOING".equals(direction)) {
            if ("MERCHANT".equals(recipientType)) {
                // Purchase from merchant
                layoutId = R.layout.home_tr_purchase;
                amountColor = 0xFFC62828; // Red
                typeText = "Purchase";
                partnerName = tx.getMerchantName() != null ? tx.getMerchantName() : "Merchant";
            } else {
                // Sent to user
                layoutId = R.layout.home_tr_sent;
                amountColor = 0xFFC62828; // Red
                typeText = "Sent";
                partnerName = tx.getRecipientNumber();
            }
        } else {
            // INCOMING
            layoutId = R.layout.home_tr_recieved;
            amountColor = 0xFF2E7D32; // Green
            typeText = "Received";

            if ("MERCHANT".equals(senderType)) {
                partnerName = tx.getMerchantName() != null ? tx.getMerchantName() : "Merchant";
            } else {
                partnerName = tx.getSenderNumber();
            }
        }

        // Inflate the layout
        View view = getLayoutInflater().inflate(layoutId, null);

        // Set data
        TextView typeView = view.findViewById(R.id.transactionType);
        TextView dateView = view.findViewById(R.id.transactionDate);
        TextView partnerView = view.findViewById(R.id.transactionPartner);
        TextView amountView = view.findViewById(R.id.transactionAmount);

        typeView.setText(typeText);
        dateView.setText(formatDate(tx.getDate()));
        partnerView.setText(partnerName);
        amountView.setText(tx.getAmount().trim().replace("₱", "$"));
        amountView.setTextColor(amountColor);

        return view;
    }

    private String formatDate(String dateString) {
        try {
            // Parse ISO date from backend
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            java.util.Date date = inputFormat.parse(dateString);

            // Format to readable date
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat(
                    "MMM dd, yyyy", java.util.Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString; // Return original if parsing fails
        }
    }
}
