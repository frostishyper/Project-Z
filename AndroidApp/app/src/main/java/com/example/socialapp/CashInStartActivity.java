package com.example.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

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

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        partnerVisa = findViewById(R.id.partner_visa);
        partnerMastercard = findViewById(R.id.partner_mastercard);
        partnerAmex = findViewById(R.id.partner_amex);
        partnerJcb = findViewById(R.id.partner_jcb);
        partnerDiscover = findViewById(R.id.partner_discover);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        partnerVisa.setOnClickListener(v -> openCardDetails("Visa"));

        partnerMastercard.setOnClickListener(v -> openCardDetails("MasterCard"));

        partnerAmex.setOnClickListener(v -> openCardDetails("AmericanExpress"));

        partnerJcb.setOnClickListener(v -> openCardDetails("JCB"));

        partnerDiscover.setOnClickListener(v -> openCardDetails("Discover"));
    }

    private void openCardDetails(String cardType) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra("CARD_TYPE", cardType);
        startActivity(intent);
    }
}