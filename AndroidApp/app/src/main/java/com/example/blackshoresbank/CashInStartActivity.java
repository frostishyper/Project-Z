package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for selecting card payment partner.
 * Displays a list of supported card networks (Visa, MasterCard, AmEx, JCB, Discover)
 * and navigates to CardDetailsActivity when a partner is selected.
 */
public class CashInStartActivity extends AppCompatActivity {

    private ImageView backButton;
    private LinearLayout partnerVisa;
    private LinearLayout partnerMastercard;
    private LinearLayout partnerAmex;
    private LinearLayout partnerJcb;
    private LinearLayout partnerDiscover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_in_start);

        initializeViews();
        setupClickListeners();
    }

    /**
     * Initialize all view references from the layout
     */
    private void initializeViews() {
        backButton = findViewById(R.id.ico_back);
        partnerVisa = findViewById(R.id.partner_visa);
        partnerMastercard = findViewById(R.id.partner_mastercard);
        partnerAmex = findViewById(R.id.partner_amex);
        partnerJcb = findViewById(R.id.partner_jcb);
        partnerDiscover = findViewById(R.id.partner_discover);
    }

    /**
     * Setup click listeners for all partner cards and back button
     */
    private void setupClickListeners() {
        // Back button closes the activity
        backButton.setOnClickListener(v -> finish());

        // Partner card click listeners - each opens CardDetailsActivity with specific card type
        partnerVisa.setOnClickListener(v -> openCardDetails("Visa"));
        partnerMastercard.setOnClickListener(v -> openCardDetails("MasterCard"));
        partnerAmex.setOnClickListener(v -> openCardDetails("AmericanExpress"));
        partnerJcb.setOnClickListener(v -> openCardDetails("JCB"));
        partnerDiscover.setOnClickListener(v -> openCardDetails("Discover"));
    }

    /**
     * Navigate to CardDetailsActivity with the selected card type
     * \@param cardType The type of card selected (Visa, MasterCard, AmericanExpress, JCB, Discover)
     */
    private void openCardDetails(String cardType) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra("CARD_TYPE", cardType);
        startActivity(intent);
    }
}