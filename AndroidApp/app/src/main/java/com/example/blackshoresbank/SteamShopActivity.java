package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.blackshoresbank.utils.TokenManager;

import com.example.blackshoresbank.models.GetShopResponse;
import com.example.blackshoresbank.models.Inclusion;
import com.example.blackshoresbank.models.Listing;
import com.example.blackshoresbank.models.RecipientDetails;
import com.example.blackshoresbank.models.ShopPurchasePrepareResponse;
import com.example.blackshoresbank.models.ShopPurchaseRequest;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SteamShopActivity extends BaseActivity {

    private static final String TAG = "SteamShopActivity";
    private EditText steamEmailInput;
    private TextView steamEmailError;
    private ProgressBar loadingIndicator;

    private TokenManager tokenManager;
    private String currentUserNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steam_shop);

        // --- CORRECTED: Use TokenManager ---
        tokenManager = new TokenManager(this);
        currentUserNumber = tokenManager.getAccountNumber();

        if (currentUserNumber == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> finish());

        // Initialize views for validation
        steamEmailInput = findViewById(R.id.Steam_Email_input);
        steamEmailError = findViewById(R.id.Steam_Email_error);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Add text watcher for real-time validation
        steamEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (steamEmailError.getVisibility() == View.VISIBLE) {
                    hideError();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        String shopDataJson = getIntent().getStringExtra("shop_data");
        if (shopDataJson != null) {
            GetShopResponse shopData = new Gson().fromJson(shopDataJson, GetShopResponse.class);
            displayListings(shopData.getListings());
        }
    }

    private boolean validateInput() {
        String email = steamEmailInput.getText().toString().trim();
        if (email.isEmpty()) {
            // You should create this string in strings.xml
            showError("Email cannot be empty.");
            return false;
        }
        // Use Android's built-in email validator
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // You should create this string in strings.xml: shops_steam_email_error
            showError("Invalid email format.");
            return false;
        }
        hideError();
        return true;
    }

    private void showError(String message) {
        steamEmailError.setText(message);
        steamEmailError.setVisibility(View.VISIBLE);
        steamEmailInput.setBackgroundResource(R.drawable.input_field_error_bg);
    }

    private void hideError() {
        steamEmailError.setVisibility(View.GONE);
        steamEmailInput.setBackgroundResource(R.drawable.input_field_bg);
    }

    private void displayListings(List<Listing> listings) {
        LinearLayout container = findViewById(R.id.Steam_listings);
        container.removeAllViews();

        if (listings == null || listings.isEmpty()) {
            TextView noListings = new TextView(this);
            noListings.setText(R.string.no_items_available);
            noListings.setTextColor(0xFFCCCCCC);
            noListings.setPadding(20, 40, 20, 40);
            container.addView(noListings);
            return;
        }

        for (int i = 0; i < listings.size(); i++) {
            Listing listing = listings.get(i);
            LinearLayout listingView = createListingView(listing);
            container.addView(listingView);

            if (i < listings.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(1)
                );
                params.setMargins(0, dpToPx(8), 0, dpToPx(8));
                divider.setLayoutParams(params);
                divider.setBackgroundColor(0xFF333333);
                container.addView(divider);
            }
        }
    }

    private LinearLayout createListingView(Listing listing) {
        // Create main container
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mainLayout.setLayoutParams(mainParams);
        mainLayout.setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8));
        mainLayout.setClickable(true);
        mainLayout.setFocusable(true);

        // Set selectable background
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        mainLayout.setBackgroundResource(outValue.resourceId);

        // Icon
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(70), dpToPx(70));
        iconParams.setMarginEnd(dpToPx(10));
        icon.setLayoutParams(iconParams);
        icon.setAdjustViewBounds(true);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Set icon from drawable
        String iconName = listing.getListingIconName();
        Log.d(TAG, "Attempting to load icon. Name from server: '" + iconName + "'");

        if (iconName != null && !iconName.isEmpty()) {
            int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
            if (resId != 0) {
                Log.d(TAG, "Success! Found drawable resource: " + iconName);
                icon.setImageResource(resId);
            } else {
                Log.w(TAG, "Failed to find drawable resource: " + iconName);
                icon.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            Log.w(TAG, "Icon name was null or empty.");
            icon.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        mainLayout.addView(icon);

        // Content container
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        contentLayout.setLayoutParams(contentParams);

        // Listing name
        TextView nameText = new TextView(this);
        nameText.setText(listing.getListingName());
        nameText.setTextColor(0xFFFFFFFF);
        nameText.setTextSize(16);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        contentLayout.addView(nameText);

        // Inclusions container
        LinearLayout inclusionsLayout = new LinearLayout(this);
        inclusionsLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams inclusionsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        inclusionsParams.setMargins(0, dpToPx(4), 0, 0);
        inclusionsLayout.setLayoutParams(inclusionsParams);

        // Add each inclusion
        for (Inclusion inclusion : listing.getInclusions()) {
            LinearLayout inclusionRow = new LinearLayout(this);
            inclusionRow.setOrientation(LinearLayout.HORIZONTAL);
            inclusionRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, dpToPx(2), 0, 0);
            inclusionRow.setLayoutParams(rowParams);

            // Count
            TextView countText = new TextView(this);
            countText.setText(String.valueOf(inclusion.getQuantity()));
            countText.setTextColor(0xFFCCCCCC);
            countText.setTextSize(13);
            countText.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            countParams.setMarginEnd(dpToPx(6));
            countText.setLayoutParams(countParams);
            inclusionRow.addView(countText);

            // Name
            TextView inclusionNameText = new TextView(this);
            inclusionNameText.setText(inclusion.getInclusionName());
            inclusionNameText.setTextColor(0xFFCCCCCC);
            inclusionNameText.setTextSize(13);
            inclusionRow.addView(inclusionNameText);

            inclusionsLayout.addView(inclusionRow);
        }
        contentLayout.addView(inclusionsLayout);

        // Price
        TextView priceText = new TextView(this);
        priceText.setText(String.format("$%.2f", listing.getListingPrice()));
        priceText.setTextColor(0xFF2E7D32); // Green color
        priceText.setTextSize(15);
        priceText.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        priceParams.setMargins(0, dpToPx(6), 0, 0);
        priceText.setLayoutParams(priceParams);
        contentLayout.addView(priceText);

        mainLayout.addView(contentLayout);

        // --- UPDATED Click Listener ---
        mainLayout.setOnClickListener(v -> {
            // Step 1: Validate the input field
            if (!validateInput()) {
                Toast.makeText(SteamShopActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Step 2: Show loading
            setLoading(true);

            // Step 3: Get the required data
            String steamEmail = steamEmailInput.getText().toString().trim();
            int listingId = listing.getListingId();

            // Step 4: Create RecipientDetails and Request objects
            RecipientDetails recipientDetails = new RecipientDetails();
            recipientDetails.setSteamEmail(steamEmail);

            ShopPurchaseRequest request = new ShopPurchaseRequest(currentUserNumber, listingId, recipientDetails);

            // Step 5: Call the /prepare API
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ShopPurchasePrepareResponse> call = apiService.shopPurchasePrepare(request);

            call.enqueue(new Callback<ShopPurchasePrepareResponse>() {
                @Override
                public void onResponse(Call<ShopPurchasePrepareResponse> call, Response<ShopPurchasePrepareResponse> response) {
                    setLoading(false);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Success! Navigate to confirmation screen
                        ShopPurchasePrepareResponse prepareData = response.body();

                        Intent intent = new Intent(SteamShopActivity.this, ConfirmPurchaseActivity.class);
                        intent.putExtra("USER_NUMBER", currentUserNumber);
                        intent.putExtra("LISTING_ID", listingId);
                        intent.putExtra("RECIPIENT_DETAILS_JSON", new Gson().toJson(recipientDetails));
                        intent.putExtra("PREPARE_RESPONSE_JSON", new Gson().toJson(prepareData));
                        startActivity(intent);

                    } else {
                        // Handle API error
                        String error = "Failed to prepare purchase. Please try again.";
                        if (response.body() != null && response.body().getError() != null) {
                            error = response.body().getError();
                        }
                        Toast.makeText(SteamShopActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ShopPurchasePrepareResponse> call, Throwable t) {
                    setLoading(false);
                    Toast.makeText(SteamShopActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return mainLayout;
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}