package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackshoresbank.models.SendMoneyCommitRequest;
import com.example.blackshoresbank.models.SendMoneyCommitResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendConfirmActivity extends BaseActivity {

    private String userNumber, recipientNumber, transferAmount, transferFee, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_confirm);

        // Get data from intent
        userNumber = getIntent().getStringExtra("userNumber");
        recipientNumber = getIntent().getStringExtra("recipientNumber");
        String recipientUsername = getIntent().getStringExtra("recipientUsername");
        transferAmount = getIntent().getStringExtra("transferAmount");
        transferFee = getIntent().getStringExtra("transferFee");
        String totalAmount = getIntent().getStringExtra("totalAmount");
        String projectedBalance = getIntent().getStringExtra("projectedBalance");
        String currentBalance = getIntent().getStringExtra("currentBalance");
        note = getIntent().getStringExtra("note");

        // Get views
        TextView amountHeader = findViewById(R.id.AmountHeader);
        TextView userNumberView = findViewById(R.id.sendconfirm_placeholder_usernumber);
        TextView recipientUsernameView = findViewById(R.id.sendconfirm_placeholder_recipientusername);
        TextView recipientNumberView = findViewById(R.id.sendconfirm_placeholder_recipientnumber);
        TextView initialBalanceView = findViewById(R.id.sendconfirm_placeholder_initial);
        TextView transferAmountView = findViewById(R.id.sendconfirm_placeholder_transferamount);
        TextView feeView = findViewById(R.id.sendconfirm_placeholder_fee);
        TextView newBalanceView = findViewById(R.id.sendconfirm_placeholder_newbalance);
        TextView noteView = findViewById(R.id.receipt_place);
        Button sendBtn = findViewById(R.id.SendMoneyBtn);
        ImageView cancelBtn = findViewById(R.id.CacelBtn);

        // Set data
        amountHeader.setText("₱" + transferAmount);
        userNumberView.setText(userNumber);
        recipientUsernameView.setText(recipientUsername);
        recipientNumberView.setText(recipientNumber);
        initialBalanceView.setText("₱" + currentBalance);
        transferAmountView.setText("₱" + transferAmount);
        feeView.setText("₱" + transferFee);
        newBalanceView.setText("₱" + projectedBalance);
        noteView.setText(note != null && !note.isEmpty() ? note : "No note");

        // Cancel button
        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        // Send button
        sendBtn.setOnClickListener(v -> commitTransaction());
    }

    private void commitTransaction() {
        Button sendBtn = findViewById(R.id.SendMoneyBtn);
        sendBtn.setEnabled(false);
        sendBtn.setAlpha(0.5f);

        SendMoneyCommitRequest request = new SendMoneyCommitRequest(
                userNumber, recipientNumber, transferAmount, note
        );

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<SendMoneyCommitResponse> call = apiService.CommitSendMoney(request);

        call.enqueue(new Callback<SendMoneyCommitResponse>() {
            @Override
            public void onResponse(Call<SendMoneyCommitResponse> call, Response<SendMoneyCommitResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SendMoneyCommitResponse result = response.body();

                    if (result.isSuccess()) {
                        Toast.makeText(SendConfirmActivity.this,
                                "Transfer successful!", Toast.LENGTH_LONG).show();

                        // Navigate to home
                        Intent intent = new Intent(SendConfirmActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SendConfirmActivity.this, result.getError(), Toast.LENGTH_LONG).show();
                        sendBtn.setEnabled(true);
                        sendBtn.setAlpha(1f);
                    }
                } else {
                    Toast.makeText(SendConfirmActivity.this,
                            "Transfer failed", Toast.LENGTH_LONG).show();
                    sendBtn.setEnabled(true);
                    sendBtn.setAlpha(1f);
                }
            }

            @Override
            public void onFailure(Call<SendMoneyCommitResponse> call, Throwable t) {
                Toast.makeText(SendConfirmActivity.this,
                        "Network error", Toast.LENGTH_LONG).show();
                sendBtn.setEnabled(true);
                sendBtn.setAlpha(1f);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Just go back, transaction not committed
        super.onBackPressed();
    }
}