package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackshoresbank.models.RecipientDetails;
import com.example.blackshoresbank.models.ShopPurchaseCommitResponse;
import com.example.blackshoresbank.models.ShopPurchasePrepareResponse;
import com.example.blackshoresbank.models.ShopPurchaseRequest;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPurchaseActivity extends BaseActivity {

    private ProgressBar loadingIndicator;
    private Button purchaseBtn;
    private ImageView cancelBtn;

    // Data needed for commit
    private String userNumber;
    private int listingId;
    private RecipientDetails recipientDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_purchase);

        // --- Initialize Views ---
        loadingIndicator = findViewById(R.id.loading_indicator);
        purchaseBtn = findViewById(R.id.PurchaseBtn);
        cancelBtn = findViewById(R.id.CancelBtn);

        TextView costHeader = findViewById(R.id.PurchasePreview_CostHeader);
        TextView userNumberText = findViewById(R.id.PurchasePreview_UserNumber);
        TextView merchantNameText = findViewById(R.id.PurchasePreview_MerchantName);
        TextView listingNameText = findViewById(R.id.PurchasePreview_ListingName);
        TextView initialBalanceText = findViewById(R.id.PurchasePreview_InitialBalance);
        TextView costText = findViewById(R.id.PurchasePreview_Cost);
        TextView newBalanceText = findViewById(R.id.PurchasePreview_NewBalance);
        TextView noteText = findViewById(R.id.PurchasePreview_Note);

        // --- Get Data from Intent ---
        Intent intent = getIntent();
        userNumber = intent.getStringExtra("USER_NUMBER");
        listingId = intent.getIntExtra("LISTING_ID", -1);
        String recipientDetailsJson = intent.getStringExtra("RECIPIENT_DETAILS_JSON");
        String prepareResponseJson = intent.getStringExtra("PREPARE_RESPONSE_JSON");

        if (userNumber == null || listingId == -1 || recipientDetailsJson == null || prepareResponseJson == null) {
            Toast.makeText(this, "Error: Missing purchase data", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Deserialize JSON Data ---
        Gson gson = new Gson();
        recipientDetails = gson.fromJson(recipientDetailsJson, RecipientDetails.class);
        ShopPurchasePrepareResponse prepareData = gson.fromJson(prepareResponseJson, ShopPurchasePrepareResponse.class);

        // --- Populate UI with Preview Data ---
        String costFormatted = String.format("$%.2f", prepareData.getCost());
        costHeader.setText(costFormatted);

        userNumberText.setText(userNumber);
        merchantNameText.setText(prepareData.getMerchantName());
        listingNameText.setText(prepareData.getListingName());

        initialBalanceText.setText(String.format("$%.2f", prepareData.getInitialBalance()));
        costText.setText(String.format("-$%.2f", prepareData.getCost())); // Show as negative
        newBalanceText.setText(String.format("$%.2f", prepareData.getProjectedNewBalance()));

        // --- Generate and set the Note ---
        noteText.setText(generateNote(prepareData.getMerchantName(), prepareData.getListingName(), prepareData.getCost(), recipientDetails));

        // --- Set Click Listeners ---
        cancelBtn.setOnClickListener(v -> {
            if(loadingIndicator.getVisibility() == View.GONE) {
                finish();
            }
        });

        purchaseBtn.setOnClickListener(v -> {
            handleCommitPurchase();
        });
    }

    private void handleCommitPurchase() {
        setLoading(true);

        // Create the request object
        ShopPurchaseRequest commitRequest = new ShopPurchaseRequest(userNumber, listingId, recipientDetails);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ShopPurchaseCommitResponse> call = apiService.shopPurchaseCommit(commitRequest);

        call.enqueue(new Callback<ShopPurchaseCommitResponse>() {
            @Override
            public void onResponse(Call<ShopPurchaseCommitResponse> call, Response<ShopPurchaseCommitResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // --- PURCHASE SUCCESSFUL ---
                    ShopPurchaseCommitResponse commitData = response.body();
                    Toast.makeText(ConfirmPurchaseActivity.this, "Purchase Successful!", Toast.LENGTH_LONG).show();

                    // Clear back stack and go to Home
                    Intent intent = new Intent(ConfirmPurchaseActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close this activity

                } else {
                    // Handle API error
                    String error = "Purchase failed. Please try again.";
                    if (response.body() != null && response.body().getError() != null) {
                        error = response.body().getError();
                    }
                    Toast.makeText(ConfirmPurchaseActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ShopPurchaseCommitResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ConfirmPurchaseActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Client-side note generation for preview
    private String generateNote(String merchantName, String listingName, double cost, RecipientDetails details) {
        if (merchantName.equals("Valve Corp")) {
            String email = details != null ? details.getSteamEmail() : "N/A";
            return String.format("Steam Gift Card ($%.2f) Code sent to %s", cost, email);
        } else if (merchantName.equals("Kuro Games")) {
            String uid = details != null ? details.getWuwaUserID() : "N/A";
            return String.format("%s Sent to %s", listingName, uid);
        }
        return "Purchase of " + listingName;
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            purchaseBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            purchaseBtn.setEnabled(true);
            cancelBtn.setEnabled(true);
        }
    }
}