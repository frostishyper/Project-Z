package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackshoresbank.utils.WalletManager;

public class HomeActivity extends BaseActivity {

    private TextView walletBalanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

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

        // Navigate to Cash In Page
        LinearLayout actionCashIn = findViewById(R.id.Action_CashIn);
        actionCashIn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CashInActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh balance when returning to home
        loadWalletBalance();
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
}